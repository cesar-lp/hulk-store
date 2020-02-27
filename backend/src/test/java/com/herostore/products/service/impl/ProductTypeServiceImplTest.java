package com.herostore.products.service.impl;

import com.herostore.products.domain.ProductType;
import com.herostore.products.dto.ProductTypeDTO;
import com.herostore.products.exception.ResourceNotFoundException;
import com.herostore.products.mapper.ProductTypeMapper;
import com.herostore.products.repository.ProductTypeRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static java.util.Collections.singletonList;
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
    ProductTypeMapper productTypeMapper;

    @InjectMocks
    ProductTypeServiceImpl productTypeService;

    @AfterEach
    void runAfterEach() {
        verifyNoMoreInteractions(productTypeMapper, productTypeRepository);
    }

    @Test
    void shouldGetAllProductTypesSuccessfully() {
        var shirtProductType = ProductType.builder()
                .id(1L)
                .name("Shirt")
                .build();

        var existingProductTypes = singletonList(shirtProductType);
        var expectedProductTypesRetrieved = singletonList(new ProductTypeDTO(2L, "Shirt"));

        when(productTypeRepository.findAll())
                .thenReturn(existingProductTypes);

        when(productTypeMapper.toProductTypeDTOList(existingProductTypes))
                .thenReturn(expectedProductTypesRetrieved);

        var actualProductTypesRetrieved = productTypeService.getAllProductTypes();

        assertThat(actualProductTypesRetrieved, samePropertyValuesAs(expectedProductTypesRetrieved));

        verify(productTypeRepository, times(1)).findAll();
        verify(productTypeMapper, times(1)).toProductTypeDTOList(existingProductTypes);
    }

    @Test
    void shouldGetProductTypeByIdSuccessfully() {
        var id = 1L;

        var existingProductType = ProductType.builder()
                .id(id)
                .name("Cup")
                .build();

        var expectedProductTypeRetrieved = new ProductTypeDTO(id, "Cup");

        when(productTypeRepository.findById(id))
                .thenReturn(Optional.of(existingProductType));

        when(productTypeMapper.toProductTypeDTO(existingProductType))
                .thenReturn(expectedProductTypeRetrieved);

        var actualProductTypeRetrieved = productTypeService.getProductTypeById(id);

        assertThat(actualProductTypeRetrieved, samePropertyValuesAs(expectedProductTypeRetrieved));

        verify(productTypeRepository, times(1)).findById(id);
        verify(productTypeMapper, times(1)).toProductTypeDTO(existingProductType);
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
        var productTypeToCreate = ProductType.builder().name("name").build();
        var newProductType = ProductType.builder().id(1L).name("Cup").build();
        var expectedProductTypeCreated = new ProductTypeDTO(1L, "Cup");

        when(productTypeMapper.toProductType(productTypeToCreateDTO))
                .thenReturn(productTypeToCreate);

        when(productTypeRepository.save(productTypeToCreate))
                .thenReturn(newProductType);

        when(productTypeMapper.toProductTypeDTO(newProductType))
                .thenReturn(expectedProductTypeCreated);

        var actualProductTypeCreated = productTypeService.createProductType(productTypeToCreateDTO);

        assertThat(actualProductTypeCreated, samePropertyValuesAs(expectedProductTypeCreated));

        verify(productTypeMapper, times(1)).toProductType(productTypeToCreateDTO);
        verify(productTypeRepository, times(1)).save(productTypeToCreate);
        verify(productTypeMapper, times(1)).toProductTypeDTO(newProductType);
    }

    @Test
    void shouldUpdateProductTypeSuccessfully() {
        var id = 1L;
        var productTypeToUpdateDTO = new ProductTypeDTO(id, "Cups");
        var productTypeToUpdate = ProductType.builder().id(id).name("Cups").build();
        var existingProductType = ProductType.builder().id(id).name("Cup").build();
        var expectedProductTypeUpdated = new ProductTypeDTO(id, "Cups");

        when(productTypeMapper.toProductType(productTypeToUpdateDTO))
                .thenReturn(productTypeToUpdate);

        when(productTypeRepository.findById(id))
                .thenReturn(Optional.of(existingProductType));

        when(productTypeRepository.save(existingProductType))
                .thenReturn(existingProductType);

        when(productTypeMapper.toProductTypeDTO(existingProductType))
                .thenReturn(expectedProductTypeUpdated);

        var actualProductTypeUpdated = productTypeService.updateProductType(id, productTypeToUpdateDTO);

        assertThat(actualProductTypeUpdated, samePropertyValuesAs(expectedProductTypeUpdated));

        verify(productTypeMapper, times(1)).toProductType(productTypeToUpdateDTO);
        verify(productTypeRepository, times(1)).findById(id);
        verify(productTypeRepository, times(1)).save(existingProductType);
        verify(productTypeMapper, times(1)).toProductTypeDTO(existingProductType);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingProductType() {
        var id = 99L;
        var productTypeToUpdateDTO = new ProductTypeDTO(id, "Cups");
        var productTypeToUpdate = ProductType.builder().id(id).name("Cups").build();

        when(productTypeMapper.toProductType(productTypeToUpdateDTO))
                .thenReturn(productTypeToUpdate);

        when(productTypeRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productTypeService.updateProductType(id, productTypeToUpdateDTO));

        verify(productTypeMapper, times(1)).toProductType(productTypeToUpdateDTO);
        verify(productTypeRepository, times(1)).findById(id);
    }

    @Test
    void shouldDeleteProductTypeByIdSuccessfully() {
        var id = 1L;
        var existingProductType = ProductType.builder().id(id).name("Cup").build();

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
