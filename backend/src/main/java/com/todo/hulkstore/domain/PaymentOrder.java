package com.todo.hulkstore.domain;

import com.todo.hulkstore.domain.common.ValidationEntity;
import com.todo.hulkstore.exception.InvalidEntityStateException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.todo.hulkstore.utils.NumberUtils.roundToTwoDecimalPlaces;

@Entity
@Builder
@Getter
@NoArgsConstructor
@Table(name = "payment_order")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentOrder extends ValidationEntity<PaymentOrder> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToMany(cascade = CascadeType.ALL)
    @NotEmpty(message = "Must contain at least one product order")
    List<ProductOrder> productOrders;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    LocalDateTime createdAt;

    @NotNull(message = "Total cannot be null")
    @DecimalMin(value = "0", message = "Total cannot be negative")
    BigDecimal total;

    private PaymentOrder(Long id, List<ProductOrder> productOrders, LocalDateTime createdAt, BigDecimal total) {
        this.id = id;
        this.productOrders = productOrders;
        this.createdAt = createdAt;
        this.total = roundToTwoDecimalPlaces(total);
        validateEntity();
        validateTotal(this.total, this.productOrders);
    }

    private void validateTotal(BigDecimal paymentOrderTotal, List<ProductOrder> productOrders) {
        var calculatedTotal = productOrders
                .stream()
                .map(ProductOrder::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (!paymentOrderTotal.equals(calculatedTotal)) {
            throw new InvalidEntityStateException("total", "Payment order total and sum of product orders' total are not equal");
        }
    }
}
