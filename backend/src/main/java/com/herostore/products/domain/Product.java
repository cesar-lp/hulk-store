package com.herostore.products.domain;

import com.herostore.products.domain.common.ValidationEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static com.herostore.products.utils.ValidationUtils.checkNotNullNorNegative;

@Getter
@Entity
@Builder
@NoArgsConstructor
@Table(name = "product")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product extends ValidationEntity<Product> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false, nullable = false)
    Long id;

    @NotBlank(message = "Name cannot be empty")
    @Column(name = "name", nullable = false)
    String name;

    @ManyToOne
    @JoinColumn(name = "product_type_id", nullable = false)
    @NotNull(message = "Product type cannot be null")
    ProductType productType;

    @NotNull(message = "Stock cannot be null")
    @Min(value = 0, message = "Stock cannot be negative")
    @Column(name = "stock", nullable = false)
    Integer stock;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0", message = "Price cannot be negative")
    @Column(name = "price", nullable = false)
    BigDecimal price;

    private Product(Long id, String name, ProductType productType, Integer stock, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.productType = productType;
        this.stock = stock;
        this.price = price;
        validateEntity();
    }

    public void removeFromStock(Integer amountToRemove) {
        checkNotNullNorNegative(amountToRemove, "Amount to remove from stock cannot be null nor negative.");
        if (amountToRemove > this.stock) {
            throw new IllegalArgumentException("Amount to remove from stock is greater than available stock.");
        }
        this.stock -= amountToRemove;
    }
}
