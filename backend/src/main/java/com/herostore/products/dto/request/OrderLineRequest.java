package com.herostore.products.dto.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
public class OrderLineRequest implements Serializable {

    private static final long serialVersionUID = 1060677346666560106L;

    @NotNull(message = "ProductId cannot be null.")
    Long productId;

    @Min(value = 1, message = "Quantity must be greater than 0.")
    Integer quantity;
}
