package com.todo.hulkstore.service.impl;

import com.todo.hulkstore.converter.InventoryConverter;
import com.todo.hulkstore.domain.Inventory;
import com.todo.hulkstore.dto.InventoryDTO;
import com.todo.hulkstore.exception.ResourceNotFoundException;
import com.todo.hulkstore.repository.InventoryRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryServiceImplTest {

    @Mock
    InventoryConverter converter;

    @Mock
    InventoryRepository repository;

    @InjectMocks
    InventoryServiceImpl inventoryService;

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(repository, converter);
    }

    @Test
    void shouldCreateInventorySuccessfully() {
        var newInventoryDTO = mockNewInventoryDTO();
        var newInventory = mockNewInventory();
        var createdInventory = mockExistingInventory();
        var createdInventoryDTO = mockExistingInventoryDTO();

        when(converter.toInventory(newInventoryDTO)).thenReturn(newInventory);
        when(repository.save(newInventory)).thenReturn(createdInventory);
        when(converter.toInventoryDTO(createdInventory)).thenReturn(createdInventoryDTO);

        var returnedInventory = inventoryService.createInventory(newInventoryDTO);

        assertThat(returnedInventory, samePropertyValuesAs(createdInventoryDTO));
        verify(converter, times(1)).toInventory(newInventoryDTO);
        verify(repository, times(1)).save(newInventory);
        verify(converter, times(1)).toInventoryDTO(createdInventory);
    }

    @Test
    void shouldUpdateInventorySuccessfully() {
        var id = 1L;
        var updatedInventoryDTO = mockUpdatedInventoryDTO();
        var existingInventory = mockExistingInventory();
        var updatedInventory = mockUpdatedInventory();

        when(repository.findById(id)).thenReturn(Optional.of(existingInventory));
        when(repository.save(existingInventory)).thenReturn(updatedInventory);
        when(converter.toInventoryDTO(updatedInventory)).thenReturn(updatedInventoryDTO);

        var returnedInventory = inventoryService.updateInventory(id, updatedInventoryDTO);

        assertThat(returnedInventory, samePropertyValuesAs(updatedInventoryDTO));
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(existingInventory);
        verify(converter, times(1)).toInventoryDTO(updatedInventory);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingInventory() {
        var id = 1L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> inventoryService.updateInventory(id, mockExistingInventoryDTO()));
        verify(repository, times(1)).findById(id);
    }

    private Inventory mockExistingInventory() {
        return new Inventory(1L, 1L, 5);
    }

    private InventoryDTO mockExistingInventoryDTO() {
        return new InventoryDTO(1L, 1L, 5);
    }

    private Inventory mockNewInventory() {
        return new Inventory(null, 1L, 5);
    }

    private InventoryDTO mockNewInventoryDTO() {
        return new InventoryDTO(null, 1L, 5);
    }

    private Inventory mockUpdatedInventory() {
        return new Inventory(1L, 1L, 20);
    }

    private InventoryDTO mockUpdatedInventoryDTO() {
        return new InventoryDTO(1L, 1L, 20);
    }
}
