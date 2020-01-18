package com.todo.hulkstore.service.impl;

import com.todo.hulkstore.converter.InventoryConverter;
import com.todo.hulkstore.dto.InventoryDTO;
import com.todo.hulkstore.exception.ResourceNotFoundException;
import com.todo.hulkstore.exception.ServiceException;
import com.todo.hulkstore.repository.InventoryRepository;
import com.todo.hulkstore.service.InventoryService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryServiceImpl implements InventoryService {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    InventoryRepository repository;
    InventoryConverter converter;

    @Override
    public InventoryDTO createInventory(InventoryDTO newInventory) {
        try {
            var inventory = converter.toInventory(newInventory);
            return converter.toInventoryDTO(repository.save(inventory));
        } catch (Exception e) {
            logger.error("createInventory({}): Could not create inventory", newInventory, e);
            throw new ServiceException("Couldn't create inventory", e);
        }
    }

    @Override
    public InventoryDTO updateInventory(Long id, InventoryDTO updatedInventory) {
        try {
            var invOpt = repository.findById(id);

            if (invOpt.isEmpty()) {
                logger.error("updateInventory({}, {}): Inventory not found", id, updatedInventory);
                throw new ResourceNotFoundException("Inventory not found for id " + id);
            }

            var inv = invOpt.get();
            inv.setStock(updatedInventory.getStock());
            return converter.toInventoryDTO(repository.save(inv));
        } catch (Exception e) {
            if (e instanceof ResourceNotFoundException) throw e;

            logger.error("updateInventory({}, {}): Couldn't update inventory", id, updatedInventory, e);
            throw new ServiceException("Couldn't update inventory", e);
        }
    }
}
