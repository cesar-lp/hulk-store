package com.herostore.products.domain;

import com.herostore.products.domain.common.ValidationEntity;
import com.herostore.products.exception.InvalidEntityStateException;
import com.herostore.products.utils.NumberUtils;
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
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor
@Table(name = "product_order")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductOrder extends ValidationEntity<ProductOrder> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "payment_id", referencedColumnName = "id")
    @NotEmpty(message = "Must contain at least one product order line")
    List<ProductOrderLine> productOrderLines;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    LocalDateTime createdAt;

    @NotNull(message = "Total cannot be null")
    @DecimalMin(value = "0", message = "Total cannot be negative")
    BigDecimal total;

    private ProductOrder(Long id, List<ProductOrderLine> productOrderLines, LocalDateTime createdAt, BigDecimal total) {
        this.id = id;
        this.productOrderLines = productOrderLines;
        this.createdAt = createdAt;
        this.total = NumberUtils.roundToTwoDecimalPlaces(total);
        validateEntity();
        validateTotal(this.total, this.productOrderLines);
    }

    private void validateTotal(BigDecimal paymentOrderTotal, List<ProductOrderLine> productOrderLines) {
        var calculatedTotal = productOrderLines
                .stream()
                .map(ProductOrderLine::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (!paymentOrderTotal.equals(calculatedTotal)) {
            throw new InvalidEntityStateException("total", "Total and order lines' total do not match");
        }
    }
}
