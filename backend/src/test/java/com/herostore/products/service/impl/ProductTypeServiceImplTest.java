package com.herostore.products.service.impl;

import com.herostore.products.constants.FileType;
import com.herostore.products.domain.ProductType;
import com.herostore.products.dto.ProductTypeDTO;
import com.herostore.products.exception.ResourceNotFoundException;
import com.herostore.products.io.CSVWriter;
import com.herostore.products.io.ExcelWriter;
import com.herostore.products.io.WorkbookData;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
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
    ProductTypeMapper productTypeMapper;

    @Mock
    CSVWriter csvWriter;

    @Mock
    ExcelWriter excelWriter;

    @InjectMocks
    ProductTypeServiceImpl productTypeService;

    @AfterEach
    void runAfterEach() {
        verifyNoMoreInteractions(productTypeMapper, productTypeRepository, csvWriter, excelWriter);
    }

    @Test
    void shouldGetAllProductTypesSuccessfully() {
        var shirtProductType = getProductType(1L, "Shirt");

        var existingProductTypes = singletonList(shirtProductType);
        var expectedProductTypesRetrieved = singletonList(getProductTypeDTO(2L, "Shirt"));

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
        var existingProductType = getProductType(id, "Cups");
        var expectedProductTypeRetrieved = getProductTypeDTO(id, "Cups");

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
        var newProductType = getProductType(1L, "Cup");
        var expectedProductTypeCreated = getProductTypeDTO(1L, "Cup");

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
        var productTypeToUpdateDTO = getProductTypeDTO(id, "Cups");
        var productTypeToUpdate = getProductType(id, "Cups");
        var existingProductType = getProductType(id, "Cup");
        var expectedProductTypeUpdated = getProductTypeDTO(id, "Cups");

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
        var productTypeToUpdateDTO = getProductTypeDTO(id, "Cups");
        var productTypeToUpdate = getProductType(id, "Cups");

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
        var existingProductType = getProductType(id, "Cups");

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

    @Test
    void shouldExportToCSVSuccessfully() throws IOException {
        var existingProductTypes = singletonList(getProductType(1L, "Cups"));
        var existingProductTypesDTOs = singletonList(getProductTypeDTO(1L, "Cups"));

        when(productTypeRepository.findAll())
                .thenReturn(existingProductTypes);

        when(productTypeMapper.toProductTypeDTOList(existingProductTypes))
                .thenReturn(existingProductTypesDTOs);

        var os = new ByteArrayOutputStream();

        productTypeService.exportProductTypesToFile(os, FileType.CSV);

        var headers = new String[]{"ID", "Name"};
        var fields = new String[]{"id", "name"};

        verify(productTypeRepository, times(1)).findAll();
        verify(productTypeMapper, times(1)).toProductTypeDTOList(existingProductTypes);
        verify(csvWriter, times(1)).write(os, headers, fields, existingProductTypesDTOs);
    }

    @Test
    void shouldExportToExcelSuccessfully() throws IOException {
        var existingProductTypes = singletonList(getProductType(1L, "Cups"));
        var existingProductTypesDTOs = singletonList(getProductTypeDTO(1L, "Cups"));

        when(productTypeRepository.findAll())
                .thenReturn(existingProductTypes);

        when(productTypeMapper.toProductTypeDTOList(existingProductTypes))
                .thenReturn(existingProductTypesDTOs);

        var outputStream = new ByteArrayOutputStream();

        var columnWidths = new int[]{2000, 7000};
        var headers = new String[]{"ID", "Name"};
        var fields = new String[]{"id", "name"};

        var workbookData = WorkbookData.builder()
                .sheetName("Product Types")
                .columnWidths(columnWidths)
                .headers(headers)
                .fields(fields)
                .build();

        productTypeService.exportProductTypesToFile(outputStream, FileType.EXCEL);

        verify(productTypeRepository, times(1)).findAll();
        verify(productTypeMapper, times(1)).toProductTypeDTOList(existingProductTypes);
        verify(excelWriter, times(1)).withData(workbookData);
        verify(excelWriter, times(1)).writeWorkbook(outputStream, existingProductTypesDTOs);
    }

    @Test
    void shouldThrowExceptionWhenExportingToInvalidFileFormat() {
        var existingProductTypes = singletonList(getProductType(1L, "Cups"));
        var existingProductTypesDTOs = singletonList(getProductTypeDTO(1L, "Cups"));
        var expectedError = "Format type PDF not valid";

        when(productTypeRepository.findAll())
                .thenReturn(existingProductTypes);

        when(productTypeMapper.toProductTypeDTOList(existingProductTypes))
                .thenReturn(existingProductTypesDTOs);

        var exc = assertThrows(IllegalArgumentException.class,
                () -> productTypeService.exportProductTypesToFile(new PipedOutputStream(), FileType.PDF));

        assertEquals(expectedError, exc.getMessage());

        verify(productTypeRepository, times(1)).findAll();
        verify(productTypeMapper, times(1)).toProductTypeDTOList(existingProductTypes);
    }

    private ProductType getProductType(long id, String name) {
        return ProductType.builder()
                .id(id)
                .name(name)
                .build();
    }

    private ProductTypeDTO getProductTypeDTO(long id, String name) {
        return ProductTypeDTO.builder()
                .id(id)
                .name(name)
                .build();
    }
}
