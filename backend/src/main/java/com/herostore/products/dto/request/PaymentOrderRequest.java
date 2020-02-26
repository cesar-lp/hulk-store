package com.herostore.products.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentOrderRequest implements Serializable {

    static final long serialVersionUID = 7741660781447574172L;

    @NotEmpty(message = "Order lines cannot be empty")
    List<OrderLineRequest> orderLines;
}
