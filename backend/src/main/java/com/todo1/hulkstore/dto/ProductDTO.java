package com.todo1.hulkstore.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ProductDTO implements Serializable {

    private static final long serialVersionUID = -5624378278309074803L;

    Long id;

    @NotBlank(message = "Name is required.")
    String name;

    @Valid
    @NotNull(message = "Product type is required.")
    ProductTypeDTO productType;

    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0.0", message = "Price cannot be negative.")
    BigDecimal price;

    @NotNull(message = "Stock is required.")
    @Min(value = 0L, message = "Stock cannot be negative.")
    Integer stock;
}
