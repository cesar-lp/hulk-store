package com.todo.hulkstore.converter;

import com.todo.hulkstore.domain.Inventory;
import com.todo.hulkstore.dto.InventoryDTO;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryConverterTest {

    static InventoryConverter converter = new InventoryConverter();

    @Test
    void shouldConvertToInventorySuccessfully() {
        var dto = new InventoryDTO(1L, 1L, 15);
        var inventory = converter.toInventory(dto);

        assertEquals(dto.getId(), inventory.getId());
        assertEquals(dto.getProductId(), inventory.getId());
        assertEquals(dto.getStock(), inventory.getStock());
    }

    @Test
    void shouldThrowExceptionWhenConvertingInvalidInventoryDTO() {
        assertThrows(IllegalArgumentException.class, () -> converter.toInventory(null));
    }

    @Test
    void shouldConvertToInventoryDTOSuccessfully() {
        var inventory = new Inventory(1L, 1L, 15);
        var dto = converter.toInventoryDTO(inventory);

        assertEquals(inventory.getId(), dto.getId());
        assertEquals(inventory.getProductId(), dto.getProductId());
        assertEquals(inventory.getStock(), dto.getStock());
    }

    @Test
    void shouldThrowExceptionWhenConvertingInvalidInventory() {
        assertThrows(IllegalArgumentException.class, () -> converter.toInventoryDTO(null));
    }
}
