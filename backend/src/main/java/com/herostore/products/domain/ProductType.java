package com.herostore.products.domain;

import com.herostore.products.domain.common.ValidationEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Getter
@Builder
@ToString
@NoArgsConstructor
@Table(name = "product_type")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductType extends ValidationEntity<ProductType> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank(message = "Name cannot be empty")
    String name;

    @OneToMany(
            mappedBy = "productType",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    Set<Product> products;

    public ProductType(Long id, String name, Set<Product> products) {
        this.id = id;
        this.name = name;
        this.products = products;
        validateEntity();
    }

    public void updateName(String name) {
        this.name = name;
        validateEntity();
    }
}

