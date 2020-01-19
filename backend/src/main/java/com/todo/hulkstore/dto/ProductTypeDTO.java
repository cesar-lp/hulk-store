package com.todo.hulkstore.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductTypeDTO implements Serializable {

    private static final long serialVersionUID = 7787891167832730320L;

    Long id;

    @NotBlank(message = "Name is required.")
    String name;
}
