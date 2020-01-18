package com.todo.hulkstore.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentOrderRequestDTO implements Serializable {

    static final long serialVersionUID = 7741660781447574172L;

    @NotEmpty(message = "Payment order must contain at least one product order.")
    List<ProductOrderRequestDTO> productOrders;
}
