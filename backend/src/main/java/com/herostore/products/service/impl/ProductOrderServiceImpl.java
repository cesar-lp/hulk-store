package com.herostore.products.service.impl;

import com.herostore.products.constants.FileType;
import com.herostore.products.domain.Product;
import com.herostore.products.domain.ProductOrder;
import com.herostore.products.domain.ProductOrderLine;
import com.herostore.products.domain.embedded.ProductDetail;
import com.herostore.products.dto.request.ProductOrderLineRequest;
import com.herostore.products.dto.request.ProductOrderRequest;
import com.herostore.products.dto.response.ProductOrderResponse;
import com.herostore.products.exception.InvalidProductOrderLineException;
import com.herostore.products.exception.ResourceNotFoundException;
import com.herostore.products.exception.ServiceException;
import com.herostore.products.exception.error.InvalidProductOrderLineError;
import com.herostore.products.handler.ProductOrdersPDFWriter;
import com.herostore.products.mapper.ProductOrderMapper;
import com.herostore.products.repository.ProductOrderRepository;
import com.herostore.products.repository.ProductRepository;
import com.herostore.products.service.ProductOrderService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductOrderServiceImpl implements ProductOrderService {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    ProductOrderRepository productOrderRepository;
    ProductOrderMapper productOrderMapper;
    ProductRepository productRepository;
    ProductOrdersPDFWriter pdfHandler;

    /**
     * Returns existing product orders.
     *
     * @return existing product orders.
     */
    @Override
    public List<ProductOrderResponse> getAllProductOrders() {
        try {
            return productOrderMapper.toProductOrderResponseList(productOrderRepository.findAll());
        } catch (Exception e) {
            logger.error("Couldn't retrieve existing product orders", e);
            throw new ServiceException("Couldn't retrieve existing product orders", e);
        }
    }

    /**
     * Registers a product order.
     *
     * @param productOrderRequest product order request.
     * @return the created product order.
     */
    @Override
    public ProductOrderResponse registerProductOrder(ProductOrderRequest productOrderRequest) {
        try {
            var productIds = productOrderRequest.getOrderLines()
                    .stream()
                    .mapToLong(ProductOrderLineRequest::getProductId)
                    .boxed()
                    .collect(toList());

            var products = productRepository.findByIdIn(productIds);
            var productOrderLines = zipToProductOrderLines(productOrderRequest.getOrderLines(), products);

            handleProductOrderLines(productOrderLines, products);

            var productOrderTotal = productOrderLines.stream()
                    .map(ProductOrderLine::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            var paymentOrder = ProductOrder.builder()
                    .productOrderLines(productOrderLines)
                    .total(productOrderTotal)
                    .build();

            productRepository.saveAll(products);

            var createdProductOrder = productOrderRepository.save(paymentOrder);

            return productOrderMapper.toProductOrderResponse(createdProductOrder);
        } catch (ResourceNotFoundException rnfExc) {
            logger.error(rnfExc.getMessage());
            throw rnfExc;
        } catch (InvalidProductOrderLineException invalidProductOrderExc) {
            logger.error("Couldn't register product order: there are invalid product order lines");
            throw invalidProductOrderExc;
        } catch (Exception e) {
            logger.error("Couldn't register product order", e);
            throw new ServiceException("Couldn't register product order", e);
        }
    }

    /**
     * Exports all product orders to a file.
     *
     * @param os       output stream that the file will be written to.
     * @param fileType file type (PDF, Excel, CSV) to export.
     */
    @Override
    public void exportProductOrders(OutputStream os, FileType fileType) {
        try {
            var productOrders = getAllProductOrders();

            if (fileType != FileType.PDF) {
                throw new IllegalArgumentException("File format " + fileType.getDesc() + " not valid.");
            }

            pdfHandler.setProductOrders(productOrders);
            pdfHandler.createDocument(os);
            pdfHandler.openDocument();
            pdfHandler.writeDocument();
            pdfHandler.closeDocument();
        } catch (Exception exc) {
            if (exc instanceof IllegalArgumentException) throw exc;
            logger.error("Couldn't generate product orders {} file", fileType.getDesc(), exc);
            throw new ServiceException("Couldn't generate product orders " + fileType.getDesc() + " file", exc);
        }
    }

    private List<ProductOrderLine> zipToProductOrderLines(
            List<ProductOrderLineRequest> orderLines, List<Product> products) {
        var productOrders = new ArrayList<ProductOrderLine>();

        for (var orderLine : orderLines) {
            var orderProductId = orderLine.getProductId();
            var orderQuantity = orderLine.getQuantity();

            products.stream()
                    .filter(p -> p.getId().equals(orderProductId))
                    .findFirst()
                    .ifPresentOrElse(
                            p -> productOrders.add(buildProductOrder(p, orderQuantity)),
                            () -> handleProductNotFound(orderProductId));
        }

        return productOrders;
    }

    private ProductOrderLine buildProductOrder(Product p, Integer quantity) {
        var productDetail = ProductDetail.builder()
                .id(p.getId())
                .name(p.getName())
                .price(p.getPrice())
                .build();

        return ProductOrderLine.builder()
                .productDetail(productDetail)
                .quantity(quantity)
                .total(p.getPrice().multiply(BigDecimal.valueOf(quantity)))
                .build();
    }

    private void handleProductNotFound(Long id) {
        throw new ResourceNotFoundException("Couldn't register product order: product not found for id " + id);
    }

    private void handleProductOrderLines(List<ProductOrderLine> productOrderLines, List<Product> products) {
        var invalidProductOrderLineErrors = new ArrayList<InvalidProductOrderLineError>();

        for (var order : productOrderLines) {
            products.stream()
                    .filter(p -> p.getId().equals(order.getProductDetail().getId()))
                    .findFirst()
                    .ifPresentOrElse(
                            p -> handleProductInventory(p, order, invalidProductOrderLineErrors),
                            () -> handleProductInventoryNotFound(order.getProductDetail().getId())
                    );
        }

        if (!invalidProductOrderLineErrors.isEmpty()) {
            throw new InvalidProductOrderLineException(invalidProductOrderLineErrors);
        }
    }

    private void handleProductInventory(
            Product p, ProductOrderLine order, List<InvalidProductOrderLineError> invalidOrders) {
        if (order.getQuantity() > p.getStock()) {
            invalidOrders.add(new InvalidProductOrderLineError(order, p.getStock()));
        } else {
            p.removeFromStock(order.getQuantity());
        }
    }

    private void handleProductInventoryNotFound(Long productId) {
        throw new ResourceNotFoundException(
                "Couldn't register product order: product with id " + productId + " not found in stock");
    }
}
