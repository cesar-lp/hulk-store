package com.todo.hulkstore.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductOrderRequestDTO implements Serializable {

    private static final long serialVersionUID = 1060677346666560106L;

    @NotNull(message = "ProductId cannot be null.")
    Long productId;

    @Min(value = 1, message = "Quantity must be greater than 0.")
    Integer quantity;
}
