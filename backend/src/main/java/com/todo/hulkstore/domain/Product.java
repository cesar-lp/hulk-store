package com.todo.hulkstore.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

import static com.todo.hulkstore.utils.ValidationUtils.checkNotNull;
import static com.todo.hulkstore.utils.ValidationUtils.checkNotNullNorEmpty;
import static com.todo.hulkstore.utils.ValidationUtils.checkNotNullNorNegative;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @ManyToOne
    @JoinColumn(name = "product_type_id", nullable = false)
    ProductType productType;

    Integer stock;

    BigDecimal price;

    public void removeFromStock(Integer amountToRemove) {
        checkNotNullNorNegative(amountToRemove, "Amount to remove from stock cannot be null nor negative.");
        if (amountToRemove > this.stock) {
            throw new IllegalArgumentException("Amount to remove from stock is greater than available stock.");
        }
        this.stock -= amountToRemove;
    }

    public static class ProductBuilder {
        public ProductBuilder name(String name) {
            checkNotNullNorEmpty(name, "Name cannot be null nor empty.");
            this.name = name;
            return this;
        }

        public ProductBuilder productType(ProductType productType) {
            checkNotNull(productType, "Product type cannot be null.");
            this.productType = productType;
            return this;
        }

        public ProductBuilder stock(Integer stock) {
            checkNotNullNorNegative(stock, "Stock cannot be null nor negative.");
            this.stock = stock;
            return this;
        }

        public ProductBuilder price(BigDecimal price) {
            checkNotNullNorNegative(price, "Price cannot be null nor negative.");
            this.price = price;
            return this;
        }
    }
}
