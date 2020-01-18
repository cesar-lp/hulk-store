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

    BigDecimal price;

    public static class ProductBuilder {
        public ProductBuilder name(String name) {
            checkNotNullNorEmpty(name, "Product name cannot be null nor empty.");
            this.name = name;
            return this;
        }

        public ProductBuilder productType(ProductType productType) {
            checkNotNull(productType, "Product type cannot be null.");
            this.productType = productType;
            return this;
        }

        public ProductBuilder price(BigDecimal price) {
            checkNotNull(productType, "Product price cannot be null nor negative.");
            this.price = price;
            return this;
        }
    }
}
