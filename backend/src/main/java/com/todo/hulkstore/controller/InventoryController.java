package com.todo.hulkstore.controller;

import com.todo.hulkstore.dto.InventoryDTO;
import com.todo.hulkstore.service.InventoryService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(
        value = "/api/inventory",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryController {

    InventoryService inventoryService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    InventoryDTO createProductInventory(@Valid @RequestBody InventoryDTO inventory) {
        return inventoryService.createInventory(inventory);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    InventoryDTO updateProductInventory(@PathVariable Long id, @Valid @RequestBody InventoryDTO updatedInventory) {
        return inventoryService.updateInventory(id, updatedInventory);
    }
}


