package com.herostore.products.domain.embedded;

import com.herostore.products.domain.common.ValidationEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetail extends ValidationEntity<ProductDetail> {

    @NotNull(message = "Product id cannot be null")
    @Column(name = "product_id", nullable = false)
    Long id;

    @NotBlank(message = "Product name cannot be empty")
    @Column(name = "product_name", nullable = false)
    String name;

    @NotNull(message = "Product price cannot be null")
    @DecimalMin(value = "0", message = "Product price cannot be negative")
    @Column(name = "product_price", nullable = false)
    BigDecimal price;

    private ProductDetail(Long id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
        validateEntity();
    }
}
