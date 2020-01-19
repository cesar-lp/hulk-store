package com.todo.hulkstore.service;

import com.todo.hulkstore.dto.InventoryDTO;

public interface InventoryService {

    InventoryDTO createInventory(InventoryDTO inventory);

    InventoryDTO updateInventory(Long id, InventoryDTO updatedInventory);
}

