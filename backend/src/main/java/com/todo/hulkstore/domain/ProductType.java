package com.todo.hulkstore.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

import static com.todo.hulkstore.utils.ValidationUtils.checkNotNullNorEmpty;

@Entity
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "product_type")
public class ProductType {

    public ProductType(Long id, String name) {
        this.id = id;
        setName(name);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Setter(value = AccessLevel.NONE)
    String name;

    @OneToMany(
            cascade = CascadeType.ALL,
            mappedBy = "productType",
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<Product> product;

    public void setName(String name) {
        checkNotNullNorEmpty(name, "Name cannot be null nor empty.");
        this.name = name;
    }
}

