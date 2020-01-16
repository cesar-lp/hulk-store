package com.todo.hulkstore.domain;

import com.todo.hulkstore.dto.pojo.ProductOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.todo.hulkstore.utils.ValidationUtils.checkNotNullNorNegative;
import static com.todo.hulkstore.utils.ValidationUtils.checkValidId;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "payment_order")
public class PaymentOrder {

    public PaymentOrder(ProductOrder productOrder) {
        var totalPrice = productOrder
                .getPrice()
                .multiply(BigDecimal.valueOf(productOrder.getQuantity()));

        this.productId = productOrder.getProductId();
        this.quantity = productOrder.getQuantity();
        this.unitPrice = productOrder.getPrice();
        this.total = totalPrice;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "product_id")
    Long productId;

    Integer quantity;

    @Column(name = "product_price")
    BigDecimal unitPrice;

    BigDecimal total;

    @CreationTimestamp
    @Column(name = "date", columnDefinition = "TIMESTAMP")
    private LocalDateTime date;

    public static class PaymentOrderBuilder {
        public PaymentOrderBuilder productId(Long productId) {
            checkValidId(productId, "Product id has an invalid value.");
            this.productId = productId;
            return this;
        }

        public PaymentOrderBuilder quantity(Integer requestedQuantity) {
            checkNotNullNorNegative(requestedQuantity, "Quantity cannot be null nor negative.");
            this.quantity = requestedQuantity;
            return this;
        }

        public PaymentOrderBuilder unitPrice(BigDecimal unitPrice) {
            checkNotNullNorNegative(unitPrice, "Unit price cannot be null nor negative.");
            this.unitPrice = unitPrice;
            return this;
        }

        public PaymentOrderBuilder total(BigDecimal total) {
            checkNotNullNorNegative(total, "Total cannot be null nor negative.");
            this.total = total;
            return this;
        }
    }
}
