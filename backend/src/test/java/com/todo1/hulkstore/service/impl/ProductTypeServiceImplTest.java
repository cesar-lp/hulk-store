package com.todo1.hulkstore.service.impl;

import com.todo1.hulkstore.converter.ProductConverter;
import com.todo1.hulkstore.domain.ProductType;
import com.todo1.hulkstore.dto.ProductTypeDTO;
import com.todo1.hulkstore.repository.ProductTypeRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void shouldGetAllProductTypes() {
        List<ProductType> existingProductTypeList = Arrays.asList(
                new ProductType(1L, "Cup"),
                new ProductType(2L, "Shirt"));
        List<ProductTypeDTO> existingProductTypeDTOList = Arrays.asList(
                new ProductTypeDTO(1L, "Cup"),
                new ProductTypeDTO(2L, "Shirt"));

        when(productTypeRepository.findAll()).thenReturn(existingProductTypeList);
        when(productConverter.toProductTypeDTOList(existingProductTypeList)).thenReturn(existingProductTypeDTOList);

        List<ProductTypeDTO> productTypesFound = productTypeService.getAllProductTypes();

        assertEquals(existingProductTypeDTOList, productTypesFound);
        verify(productTypeRepository, times(1)).findAll();
        verify(productConverter, times(1)).toProductTypeDTOList(existingProductTypeList);
    }

    @Test
    void shouldGetProductTypeById() {
        Long id = 1L;
        ProductType existingProductType = new ProductType(id, "Cup");
        ProductTypeDTO existingProductTypeDTO = new ProductTypeDTO(id, "Cup");

        when(productTypeRepository.findById(id)).thenReturn(Optional.of(existingProductType));
        when(productConverter.toProductTypeDTO(existingProductType)).thenReturn(existingProductTypeDTO);

        ProductTypeDTO foundProductType = productTypeService.getProductTypeById(id);

        assertEquals(existingProductTypeDTO, foundProductType);
        verify(productTypeRepository, times(1)).findById(id);
        verify(productConverter, times(1)).toProductTypeDTO(existingProductType);
    }

    @Test
    void shouldCreateProductType() {
        ProductTypeDTO toCreateDTO = ProductTypeDTO.builder().name("Cup").build();
        ProductType toCreate = ProductType.builder().name("Cup").build();
        ProductTypeDTO newProductTypeDTO = new ProductTypeDTO(1L, "Cup");
        ProductType newProductType = new ProductType(1L, "Cup");

        when(productConverter.toProductType(toCreateDTO)).thenReturn(toCreate);
        when(productTypeRepository.save(toCreate)).thenReturn(newProductType);
        when(productConverter.toProductTypeDTO(newProductType)).thenReturn(newProductTypeDTO);

        ProductTypeDTO productTypeCreated = productTypeService.createProductType(toCreateDTO);

        assertEquals(newProductTypeDTO, productTypeCreated);
        verify(productConverter, times(1)).toProductType(toCreateDTO);
        verify(productTypeRepository, times(1)).save(toCreate);
        verify(productConverter, times(1)).toProductTypeDTO(newProductType);
    }

    @Test
    void shouldUpdateProductType() {
        Long id = 1L;
        ProductTypeDTO productTypeDTOToUpdate = new ProductTypeDTO(id, "Cups");
        ProductType productTypeToUpdate = new ProductType(id, "Cups");
        ProductType existingProductType = new ProductType(id, "Cup");
        ProductTypeDTO updatedProductTypeDTO = new ProductTypeDTO(id, "Cups");

        when(productConverter.toProductType(productTypeDTOToUpdate)).thenReturn(productTypeToUpdate);
        when(productTypeRepository.findById(id)).thenReturn(Optional.of(existingProductType));
        when(productTypeRepository.save(existingProductType)).thenReturn(existingProductType);
        when(productConverter.toProductTypeDTO(existingProductType)).thenReturn(updatedProductTypeDTO);
        when(productConverter.toProductType(productTypeDTOToUpdate)).thenReturn(productTypeToUpdate);

        ProductTypeDTO returnedProductTypeDTO = productTypeService.updateProductType(id, productTypeDTOToUpdate);

        assertEquals(updatedProductTypeDTO, returnedProductTypeDTO);
        verify(productConverter, times(1)).toProductType(productTypeDTOToUpdate);
        verify(productTypeRepository, times(1)).findById(id);
        verify(productTypeRepository, times(1)).save(existingProductType);
        verify(productConverter, times(1)).toProductTypeDTO(existingProductType);
    }

    @Test
    void shouldDeleteProductTypeById() {
        Long id = 1L;
        productTypeService.deleteProductTypeById(id);
        verify(productTypeRepository, times(1)).deleteById(id);
    }
}
