package com.todo.hulkstore.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import static com.todo.hulkstore.utils.ValidationUtils.checkNotNull;
import static com.todo.hulkstore.utils.ValidationUtils.checkNotNullNorNegative;

@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "product_inventory")
public class Inventory {

    public Inventory(Long id, Long productId, Integer stock) {
        checkNotNull(productId, "Inventory must be associated to a Product");

        this.id = id;
        this.productId = productId;
        setStock(stock);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "product_id")
    Long productId;

    Integer stock;

    public void setStock(Integer stock) {
        checkNotNullNorNegative(stock, "Inventory stock cannot be null nor negative.");
        this.stock = stock;
    }
}
