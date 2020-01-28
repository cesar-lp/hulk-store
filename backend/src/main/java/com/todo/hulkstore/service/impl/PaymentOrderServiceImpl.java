package com.todo.hulkstore.service.impl;

import com.todo.hulkstore.converter.PaymentOrderConverter;
import com.todo.hulkstore.domain.PaymentOrder;
import com.todo.hulkstore.domain.Product;
import com.todo.hulkstore.domain.ProductOrder;
import com.todo.hulkstore.dto.request.PaymentOrderRequestDTO;
import com.todo.hulkstore.dto.request.ProductOrderRequestDTO;
import com.todo.hulkstore.dto.response.PaymentOrderResponseDTO;
import com.todo.hulkstore.exception.InvalidProductOrderException;
import com.todo.hulkstore.exception.ResourceNotFoundException;
import com.todo.hulkstore.exception.ServiceException;
import com.todo.hulkstore.exception.error.InvalidProductOrderError;
import com.todo.hulkstore.repository.PaymentOrderRepository;
import com.todo.hulkstore.repository.ProductRepository;
import com.todo.hulkstore.service.PaymentOrderService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
    PaymentOrderConverter converter;
    ProductRepository productRepository;

    /**
     * Returns existing payment orders.
     *
     * @return existing payment orders.
     */
    @Override
    public List<PaymentOrderResponseDTO> getAllPaymentOrders() {
        return paymentOrderRepository.findAll()
                .stream()
                .map(converter::toPaymentOrderResponseDTO)
                .collect(toList());
    }

    /**
     * Registers a payment order.
     *
     * @param paymentOrderRequest payment order request.
     * @return the created payment order.
     */
    @Override
    public PaymentOrderResponseDTO registerPaymentOrder(PaymentOrderRequestDTO paymentOrderRequest) {
        try {
            var productIds = paymentOrderRequest.getProductOrders()
                    .stream()
                    .mapToLong(ProductOrderRequestDTO::getProductId)
                    .boxed()
                    .collect(toList());

            var products = productRepository.findByIdIn(productIds);
            var productOrders = zipToProductOrder(paymentOrderRequest.getProductOrders(), products);

            handleProductOrders(productOrders, products);

            var paymentOrderTotal = productOrders.stream()
                    .map(ProductOrder::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            var paymentOrder = PaymentOrder.builder()
                    .productOrders(productOrders)
                    .total(paymentOrderTotal)
                    .build();

            productRepository.saveAll(products);
            paymentOrderRepository.save(paymentOrder);

            return converter.toPaymentOrderResponseDTO(paymentOrder);
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

    private List<ProductOrder> zipToProductOrder(
            List<ProductOrderRequestDTO> productOrderReq, List<Product> products) {
        var productOrders = new ArrayList<ProductOrder>();

        for (var poReq : productOrderReq) {
            var orderProductId = poReq.getProductId();
            var orderQuantity = poReq.getQuantity();

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
        return ProductOrder.builder()
                .productId(p.getId())
                .productName(p.getName())
                .productPrice(p.getPrice())
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
                    .filter(p -> p.getId().equals(order.getProductId()))
                    .findFirst()
                    .ifPresentOrElse(
                            p -> handleProductInventory(p, order, invalidProductOrders),
                            () -> handleProductInventoryNotFound(order.getProductId())
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