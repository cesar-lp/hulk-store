package com.todo.hulkstore.domain.embedded;

import com.todo.hulkstore.domain.common.ValidationEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Builder
@ToString
@Embeddable
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetail extends ValidationEntity<ProductDetail> {

    @Column(name = "product_id")
    @NotNull(message = "Product id cannot be null")
    Long id;

    @Column(name = "product_name")
    @NotBlank(message = "Product name cannot be empty")
    String name;

    @Column(name = "product_price")
    @NotNull(message = "Product price cannot be null")
    @DecimalMin(value = "0", message = "Product price cannot be negative")
    BigDecimal price;

    private ProductDetail(Long id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
        validateEntity();
    }
}
