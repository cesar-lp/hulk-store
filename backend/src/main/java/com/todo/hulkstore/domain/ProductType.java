package com.todo.hulkstore.domain;

import com.todo.hulkstore.domain.common.ValidationEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

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

    private ProductType(Long id, String name) {
        this.id = id;
        this.name = name;
        validateEntity();
    }

    public void updateName(String name) {
        this.name = name;
        validateEntity();
    }
}

