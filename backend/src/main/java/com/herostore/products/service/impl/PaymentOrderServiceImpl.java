package com.herostore.products.service.impl;

import com.herostore.products.constants.FileType;
import com.herostore.products.domain.PaymentOrder;
import com.herostore.products.domain.Product;
import com.herostore.products.domain.ProductOrder;
import com.herostore.products.domain.embedded.ProductDetail;
import com.herostore.products.dto.request.OrderLineRequest;
import com.herostore.products.dto.request.PaymentOrderRequest;
import com.herostore.products.dto.response.PaymentOrderResponse;
import com.herostore.products.exception.InvalidProductOrderException;
import com.herostore.products.exception.ResourceNotFoundException;
import com.herostore.products.exception.ServiceException;
import com.herostore.products.exception.error.InvalidProductOrderError;
import com.herostore.products.handler.PaymentOrdersPDFWriter;
import com.herostore.products.mapper.PaymentOrderMapper;
import com.herostore.products.repository.PaymentOrderRepository;
import com.herostore.products.repository.ProductRepository;
import com.herostore.products.service.PaymentOrderService;
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
public class PaymentOrderServiceImpl implements PaymentOrderService {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    PaymentOrderRepository paymentOrderRepository;
    PaymentOrderMapper paymentOrderMapper;
    ProductRepository productRepository;
    PaymentOrdersPDFWriter pdfHandler;

    /**
     * Returns existing payment orders.
     *
     * @return existing payment orders.
     */
    @Override
    public List<PaymentOrderResponse> getAllPaymentOrders() {
        return paymentOrderMapper.toPaymentOrderResponseList(paymentOrderRepository.findAll());
    }

    /**
     * Registers a payment order.
     *
     * @param paymentOrderRequest payment order request.
     * @return the created payment order.
     */
    @Override
    public PaymentOrderResponse registerPaymentOrder(PaymentOrderRequest paymentOrderRequest) {
        try {
            var productIds = paymentOrderRequest.getOrderLines()
                    .stream()
                    .mapToLong(OrderLineRequest::getProductId)
                    .boxed()
                    .collect(toList());

            var products = productRepository.findByIdIn(productIds);
            var productOrders = zipToProductOrders(paymentOrderRequest.getOrderLines(), products);

            handleProductOrders(productOrders, products);

            var paymentOrderTotal = productOrders.stream()
                    .map(ProductOrder::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            var paymentOrder = PaymentOrder.builder()
                    .productOrders(productOrders)
                    .total(paymentOrderTotal)
                    .build();

            productRepository.saveAll(products);

            var createdPaymentOrder = paymentOrderRepository.save(paymentOrder);

            return paymentOrderMapper.toPaymentOrderResponse(createdPaymentOrder);
        } catch (ResourceNotFoundException rnfExc) {
            logger.error(rnfExc.getMessage());
            throw rnfExc;
        } catch (InvalidProductOrderException invalidProductOrderExc) {
            logger.error("Couldn't register payment order: there are invalid product orders");
            throw invalidProductOrderExc;
        } catch (Exception e) {
            logger.error("Couldn't register payment order", e);
            throw new ServiceException("Couldn't register payment order", e);
        }
    }

    /**
     * Exports all payment orders to a file.
     *
     * @param os       output stream that the file will be written to.
     * @param fileType file type (PDF, Excel, CSV) to export.
     */
    @Override
    public void exportPaymentOrders(OutputStream os, FileType fileType) {
        try {
            var paymentOrders = getAllPaymentOrders();

            if (fileType != FileType.PDF) {
                throw new IllegalArgumentException("File format " + fileType.getDesc() + " not valid.");
            }

            pdfHandler.setPaymentOrders(paymentOrders);
            pdfHandler.createDocument(os);
            pdfHandler.openDocument();
            pdfHandler.writeDocument();
            pdfHandler.closeDocument();
        } catch (Exception exc) {
            if (exc instanceof IllegalArgumentException) throw exc;
            logger.error("Couldn't generate payment orders {} file", fileType.getDesc(), exc);
            throw new ServiceException("Couldn't generate payment orders " + fileType.getDesc() + " file", exc);
        }
    }

    private List<ProductOrder> zipToProductOrders(
            List<OrderLineRequest> orderLines, List<Product> products) {
        var productOrders = new ArrayList<ProductOrder>();

        for (var order : orderLines) {
            var orderProductId = order.getProductId();
            var orderQuantity = order.getQuantity();

            products.stream()
                    .filter(p -> p.getId().equals(orderProductId))
                    .findFirst()
                    .ifPresentOrElse(
                            p -> productOrders.add(buildProductOrder(p, orderQuantity)),
                            () -> handleProductNotFound(orderProductId));
        }

        return productOrders;
    }

    private ProductOrder buildProductOrder(Product p, Integer quantity) {
        var productDetail = ProductDetail.builder()
                .id(p.getId())
                .name(p.getName())
                .price(p.getPrice())
                .build();

        return ProductOrder.builder()
                .productDetail(productDetail)
                .quantity(quantity)
                .total(p.getPrice().multiply(BigDecimal.valueOf(quantity)))
                .build();
    }

    private void handleProductNotFound(Long id) {
        throw new ResourceNotFoundException("Couldn't register payment order: product not found for id " + id);
    }

    private void handleProductOrders(List<ProductOrder> productOrders, List<Product> products) {
        var invalidProductOrders = new ArrayList<InvalidProductOrderError>();

        for (var order : productOrders) {
            products.stream()
                    .filter(p -> p.getId().equals(order.getProductDetail().getId()))
                    .findFirst()
                    .ifPresentOrElse(
                            p -> handleProductInventory(p, order, invalidProductOrders),
                            () -> handleProductInventoryNotFound(order.getProductDetail().getId())
                    );
        }

        if (!invalidProductOrders.isEmpty()) {
            throw new InvalidProductOrderException(invalidProductOrders);
        }
    }

    private void handleProductInventory(
            Product p, ProductOrder order, List<InvalidProductOrderError> invalidOrders) {
        if (order.getQuantity() > p.getStock()) {
            invalidOrders.add(new InvalidProductOrderError(order, p.getStock()));
        } else {
            p.removeFromStock(order.getQuantity());
        }
    }

    private void handleProductInventoryNotFound(Long productId) {
        throw new ResourceNotFoundException(
                "Couldn't register payment order: product with id " + productId + " not found in stock");
    }
}
