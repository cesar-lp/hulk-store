package com.todo.hulkstore.service.impl;

import com.todo.hulkstore.converter.ProductConverter;
import com.todo.hulkstore.domain.ProductType;
import com.todo.hulkstore.dto.ProductTypeDTO;
import com.todo.hulkstore.exception.ResourceNotFoundException;
import com.todo.hulkstore.repository.ProductTypeRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductTypeServiceImplTest {

    @Mock
    ProductTypeRepository productTypeRepository;

    @Mock
    ProductConverter productConverter;

    @InjectMocks
    ProductTypeServiceImpl productTypeService;

    @AfterEach
    void runAfterEach() {
        verifyNoMoreInteractions(productConverter, productTypeRepository);
    }

    @Test
    void shouldGetAllProductTypesSuccessfully() {
        var existingProductTypeList = Arrays.asList(
                new ProductType(1L, "Cup"),
                new ProductType(2L, "Shirt"));
        var existingProductTypeDTOList = Arrays.asList(
                new ProductTypeDTO(1L, "Cup"),
                new ProductTypeDTO(2L, "Shirt"));

        when(productTypeRepository.findAll()).thenReturn(existingProductTypeList);
        when(productConverter.toProductTypeDTOList(existingProductTypeList)).thenReturn(existingProductTypeDTOList);

        var productTypesFound = productTypeService.getAllProductTypes();

        assertEquals(existingProductTypeDTOList, productTypesFound);
        verify(productTypeRepository, times(1)).findAll();
        verify(productConverter, times(1)).toProductTypeDTOList(existingProductTypeList);
    }

    @Test
    void shouldGetProductTypeByIdSuccessfully() {
        var id = 1L;
        var existingProductType = new ProductType(id, "Cup");
        var existingProductTypeDTO = new ProductTypeDTO(id, "Cup");

        when(productTypeRepository.findById(id)).thenReturn(Optional.of(existingProductType));
        when(productConverter.toProductTypeDTO(existingProductType)).thenReturn(existingProductTypeDTO);

        var foundProductType = productTypeService.getProductTypeById(id);

        Assertions.assertEquals(existingProductTypeDTO, foundProductType);
        verify(productTypeRepository, times(1)).findById(id);
        verify(productConverter, times(1)).toProductTypeDTO(existingProductType);
    }

    @Test
    void shouldThrowExceptionWhenRetrievingNonExistingProductTypeById() {
        var id = 99L;

        when(productTypeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productTypeService.getProductTypeById(id));
        verify(productTypeRepository, times(1)).findById(id);
    }

    @Test
    void shouldCreateProductTypeSuccessfully() {
        var toCreateDTO = ProductTypeDTO.builder().name("Cup").build();
        var toCreate = new ProductType(null, "name");
        var newProductTypeDTO = new ProductTypeDTO(1L, "Cup");
        var newProductType = new ProductType(1L, "Cup");

        when(productConverter.toProductType(toCreateDTO)).thenReturn(toCreate);
        when(productTypeRepository.save(toCreate)).thenReturn(newProductType);
        when(productConverter.toProductTypeDTO(newProductType)).thenReturn(newProductTypeDTO);

        var productTypeCreated = productTypeService.createProductType(toCreateDTO);

        Assertions.assertEquals(newProductTypeDTO, productTypeCreated);
        verify(productConverter, times(1)).toProductType(toCreateDTO);
        verify(productTypeRepository, times(1)).save(toCreate);
        verify(productConverter, times(1)).toProductTypeDTO(newProductType);
    }

    @Test
    void shouldUpdateProductTypeSuccessfully() {
        var id = 1L;
        var productTypeDTOToUpdate = new ProductTypeDTO(id, "Cups");
        var productTypeToUpdate = new ProductType(id, "Cups");
        var existingProductType = new ProductType(id, "Cup");
        var updatedProductTypeDTO = new ProductTypeDTO(id, "Cups");

        when(productConverter.toProductType(productTypeDTOToUpdate)).thenReturn(productTypeToUpdate);
        when(productTypeRepository.findById(id)).thenReturn(Optional.of(existingProductType));
        when(productTypeRepository.save(existingProductType)).thenReturn(existingProductType);
        when(productConverter.toProductTypeDTO(existingProductType)).thenReturn(updatedProductTypeDTO);

        var returnedProductTypeDTO = productTypeService.updateProductType(id, productTypeDTOToUpdate);

        Assertions.assertEquals(updatedProductTypeDTO, returnedProductTypeDTO);
        verify(productConverter, times(1)).toProductType(productTypeDTOToUpdate);
        verify(productTypeRepository, times(1)).findById(id);
        verify(productTypeRepository, times(1)).save(existingProductType);
        verify(productConverter, times(1)).toProductTypeDTO(existingProductType);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingProduct() {
        var id = 99L;
        var productTypeDTOToUpdate = new ProductTypeDTO(id, "Cups");
        var productTypeToUpdate = new ProductType(id, "Cups");

        when(productConverter.toProductType(productTypeDTOToUpdate)).thenReturn(productTypeToUpdate);
        when(productTypeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                productTypeService.updateProductType(id, productTypeDTOToUpdate));
        verify(productConverter, times(1)).toProductType(productTypeDTOToUpdate);
        verify(productTypeRepository, times(1)).findById(id);
    }

    @Test
    void shouldDeleteProductTypeByIdSuccessfully() {
        var id = 1L;
        var existingProductType = new ProductType(id, "Cup");

        when(productTypeRepository.findById(id)).thenReturn(Optional.of(existingProductType));

        productTypeService.deleteProductTypeById(id);

        verify(productTypeRepository, times(1)).findById(id);
        verify(productTypeRepository, times(1)).delete(existingProductType);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingProductTypeById() {
        var id = 99L;

        when(productTypeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productTypeService.deleteProductTypeById(id));
        verify(productTypeRepository, times(1)).findById(id);
    }
}
