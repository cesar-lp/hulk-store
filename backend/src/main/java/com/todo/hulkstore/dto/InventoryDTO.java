package com.todo.hulkstore.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class InventoryDTO implements Serializable {

    static final long serialVersionUID = 7817431180262181160L;

    Long id;

    @NotNull(message = "Product id is required.")
    Long productId;

    @Min(value = 0, message = "Stock cannot be negative.")
    Integer stock;
}
