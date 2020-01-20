package com.todo.hulkstore.service.impl;

import com.todo.hulkstore.converter.ProductConverter;
import com.todo.hulkstore.domain.ProductType;
import com.todo.hulkstore.dto.ProductTypeDTO;
import com.todo.hulkstore.exception.ResourceNotFoundException;
import com.todo.hulkstore.repository.ProductTypeRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
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
        var existingProductTypeList = asList(
                new ProductType(1L, "Cup"),
                new ProductType(2L, "Shirt"));

        var expectedProductTypesRetrieved = asList(
                new ProductTypeDTO(1L, "Cup"),
                new ProductTypeDTO(2L, "Shirt"));

        when(productTypeRepository.findAll())
                .thenReturn(existingProductTypeList);

        when(productConverter.toProductTypeDTOList(existingProductTypeList))
                .thenReturn(expectedProductTypesRetrieved);

        var actualProductTypesRetrieved = productTypeService.getAllProductTypes();

        assertThat(actualProductTypesRetrieved, samePropertyValuesAs(expectedProductTypesRetrieved));

        verify(productTypeRepository, times(1)).findAll();
        verify(productConverter, times(1)).toProductTypeDTOList(existingProductTypeList);
    }

    @Test
    void shouldGetProductTypeByIdSuccessfully() {
        var id = 1L;
        var existingProductType = new ProductType(id, "Cup");
        var expectedProductTypeRetrieved = new ProductTypeDTO(id, "Cup");

        when(productTypeRepository.findById(id))
                .thenReturn(Optional.of(existingProductType));

        when(productConverter.toProductTypeDTO(existingProductType))
                .thenReturn(expectedProductTypeRetrieved);

        var actualProductTypeRetrieved = productTypeService.getProductTypeById(id);

        assertThat(actualProductTypeRetrieved, samePropertyValuesAs(expectedProductTypeRetrieved));

        verify(productTypeRepository, times(1)).findById(id);
        verify(productConverter, times(1)).toProductTypeDTO(existingProductType);
    }

    @Test
    void shouldThrowExceptionWhenRetrievingNonExistingProductTypeById() {
        var id = 99L;

        when(productTypeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productTypeService.getProductTypeById(id));

        verify(productTypeRepository, times(1)).findById(id);
    }

    @Test
    void shouldCreateProductTypeSuccessfully() {
        var productTypeToCreateDTO = ProductTypeDTO.builder().name("Cup").build();
        var productTypeToCreate = new ProductType(null, "name");
        var newProductType = new ProductType(1L, "Cup");
        var expectedProductTypeCreated = new ProductTypeDTO(1L, "Cup");

        when(productConverter.toProductType(productTypeToCreateDTO))
                .thenReturn(productTypeToCreate);

        when(productTypeRepository.save(productTypeToCreate))
                .thenReturn(newProductType);

        when(productConverter.toProductTypeDTO(newProductType))
                .thenReturn(expectedProductTypeCreated);

        var actualProductTypeCreated = productTypeService.createProductType(productTypeToCreateDTO);

        assertThat(actualProductTypeCreated, samePropertyValuesAs(expectedProductTypeCreated));

        verify(productConverter, times(1)).toProductType(productTypeToCreateDTO);
        verify(productTypeRepository, times(1)).save(productTypeToCreate);
        verify(productConverter, times(1)).toProductTypeDTO(newProductType);
    }

    @Test
    void shouldUpdateProductTypeSuccessfully() {
        var id = 1L;
        var productTypeToUpdateDTO = new ProductTypeDTO(id, "Cups");
        var productTypeToUpdate = new ProductType(id, "Cups");
        var existingProductType = new ProductType(id, "Cup");
        var expectedProductTypeUpdated = new ProductTypeDTO(id, "Cups");

        when(productConverter.toProductType(productTypeToUpdateDTO))
                .thenReturn(productTypeToUpdate);

        when(productTypeRepository.findById(id))
                .thenReturn(Optional.of(existingProductType));

        when(productTypeRepository.save(existingProductType))
                .thenReturn(existingProductType);

        when(productConverter.toProductTypeDTO(existingProductType))
                .thenReturn(expectedProductTypeUpdated);

        var actualProductTypeUpdated = productTypeService.updateProductType(id, productTypeToUpdateDTO);

        assertThat(actualProductTypeUpdated, samePropertyValuesAs(expectedProductTypeUpdated));

        verify(productConverter, times(1)).toProductType(productTypeToUpdateDTO);
        verify(productTypeRepository, times(1)).findById(id);
        verify(productTypeRepository, times(1)).save(existingProductType);
        verify(productConverter, times(1)).toProductTypeDTO(existingProductType);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingProduct() {
        var id = 99L;
        var productTypeToUpdateDTO = new ProductTypeDTO(id, "Cups");
        var productTypeToUpdate = new ProductType(id, "Cups");

        when(productConverter.toProductType(productTypeToUpdateDTO))
                .thenReturn(productTypeToUpdate);

        when(productTypeRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productTypeService.updateProductType(id, productTypeToUpdateDTO));

        verify(productConverter, times(1)).toProductType(productTypeToUpdateDTO);
        verify(productTypeRepository, times(1)).findById(id);
    }

    @Test
    void shouldDeleteProductTypeByIdSuccessfully() {
        var id = 1L;
        var existingProductType = new ProductType(id, "Cup");

        when(productTypeRepository.findById(id))
                .thenReturn(Optional.of(existingProductType));

        productTypeService.deleteProductTypeById(id);

        verify(productTypeRepository, times(1)).findById(id);
        verify(productTypeRepository, times(1)).delete(existingProductType);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingProductTypeById() {
        var id = 99L;

        when(productTypeRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productTypeService.deleteProductTypeById(id));

        verify(productTypeRepository, times(1)).findById(id);
    }
}
