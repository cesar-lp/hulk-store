package com.todo.hulkstore.domain;

import com.todo.hulkstore.domain.common.ValidationEntity;
import com.todo.hulkstore.domain.embedded.ProductDetail;
import com.todo.hulkstore.exception.InvalidEntityStateException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static com.todo.hulkstore.utils.NumberUtils.roundToTwoDecimalPlaces;

@Getter
@Entity
@Builder
@ToString
@Table(name = "product_order")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductOrder extends ValidationEntity<ProductOrder> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Embedded
    @NotNull(message = "Product detail cannot be null")
    ProductDetail productDetail;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity cannot be negative")
    Integer quantity;

    @NotNull(message = "Total cannot be null")
    @DecimalMin(value = "0", message = "Total cannot be negative")
    BigDecimal total;

    private ProductOrder(Long id, ProductDetail productDetail, Integer quantity, BigDecimal total) {
        this.id = id;
        this.productDetail = productDetail;
        this.quantity = quantity;
        this.total = roundToTwoDecimalPlaces(total);
        validateEntity();
        validateTotal(productDetail.getPrice(), quantity, this.total);
    }

    private void validateTotal(BigDecimal price, Integer quantity, BigDecimal total) {
        var expectedTotal = roundToTwoDecimalPlaces(price.multiply(BigDecimal.valueOf(quantity)));

        if (!expectedTotal.equals(total)) {
            throw new InvalidEntityStateException("total", "Supplied total and calculated total do not match");
        }
    }
}
