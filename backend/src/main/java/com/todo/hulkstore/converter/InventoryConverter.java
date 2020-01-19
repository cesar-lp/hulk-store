package com.todo.hulkstore.converter;

import com.todo.hulkstore.domain.Inventory;
import com.todo.hulkstore.dto.InventoryDTO;

public class InventoryConverter {

    public Inventory toInventory(InventoryDTO source) {
        if (source == null) {
            throw new IllegalArgumentException("InventoryDTO cannot be null.");
        }
        return new Inventory(source.getId(), source.getProductId(), source.getStock());
    }

    public InventoryDTO toInventoryDTO(Inventory source) {
        if (source == null) {
            throw new IllegalArgumentException("Inventory cannot be null.");
        }
        return new InventoryDTO(source.getId(), source.getProductId(), source.getStock());
    }
}
