package com.todo.hulkstore.service.impl;

import com.todo.hulkstore.domain.Inventory;
import com.todo.hulkstore.domain.PaymentOrder;
import com.todo.hulkstore.domain.Product;
import com.todo.hulkstore.dto.PaymentOrderRequestDTO;
import com.todo.hulkstore.dto.ProductOrderRequestDTO;
import com.todo.hulkstore.dto.pojo.ProductOrder;
import com.todo.hulkstore.exception.InvalidProductOrderException;
import com.todo.hulkstore.exception.ResourceNotFoundException;
import com.todo.hulkstore.exception.ServiceException;
import com.todo.hulkstore.exception.error.InvalidProductOrderError;
import com.todo.hulkstore.repository.InventoryRepository;
import com.todo.hulkstore.repository.PaymentOrderRepository;
import com.todo.hulkstore.repository.ProductRepository;
import com.todo.hulkstore.service.PaymentOrderService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentOrderServiceImpl implements PaymentOrderService {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    PaymentOrderRepository paymentOrderRepository;
    InventoryRepository inventoryRepository;
    ProductRepository productRepository;

    @Override
    public void registerOrder(PaymentOrderRequestDTO paymentOrderRequest) {
        try {
            var productIds = paymentOrderRequest.getProductOrders()
                    .stream()
                    .mapToLong(ProductOrderRequestDTO::getProductId)
                    .boxed()
                    .collect(toList());

            var products = productRepository.findByIdIn(productIds);
            var inventories = inventoryRepository.findByProductIdIn(productIds);

            var productOrders = zipToProductOrder(paymentOrderRequest.getProductOrders(), products);

            handleProductOrders(productOrders, inventories);

            var paymentOrders = productOrders
                    .stream()
                    .map(PaymentOrder::new)
                    .collect(toList());

            paymentOrderRepository.saveAll(paymentOrders);
            inventoryRepository.saveAll(inventories);
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
                            p -> productOrders.add(ProductOrder.getFor(p, orderQuantity)),
                            () -> handleProductNotFound(orderProductId));
        }
        return productOrders;
    }

    private void handleProductNotFound(Long id) {
        throw new ResourceNotFoundException("Couldn't register payment order: product not found for id " + id);
    }

    private void handleProductOrders(List<ProductOrder> productOrders, List<Inventory> inventories) {
        var invalidLines = new ArrayList<InvalidProductOrderError>();

        for (var order : productOrders) {
            inventories.stream()
                    .filter(inv -> inv.getProductId().equals(order.getProductId()))
                    .findFirst()
                    .ifPresentOrElse(
                            inv -> handleProductInventory(inv, order, invalidLines),
                            () -> handleProductInventoryNotFound(order.getProductId())
                    );
        }

        if (!invalidLines.isEmpty()) {
            throw new InvalidProductOrderException(invalidLines);
        }
    }

    private void handleProductInventory(
            Inventory inv, ProductOrder order, List<InvalidProductOrderError> invalidOrders) {
        if (order.getQuantity() > inv.getStock()) {
            invalidOrders.add(new InvalidProductOrderError(order, inv.getStock()));
        } else {
            inv.setStock(inv.getStock() - order.getQuantity());
        }
    }

    private void handleProductInventoryNotFound(Long productId) {
        throw new ResourceNotFoundException(
                "Couldn't register payment order: inventory not found for product with id " + productId);
    }
}
