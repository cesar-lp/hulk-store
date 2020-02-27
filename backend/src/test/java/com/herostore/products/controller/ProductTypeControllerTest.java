package com.herostore.products.controller;

import com.herostore.products.utils.ResponseBodyMatchers;
import com.herostore.products.utils.SerializationUtils;
import com.herostore.products.dto.ProductTypeDTO;
import com.herostore.products.exception.ResourceNotFoundException;
import com.herostore.products.exception.error.FieldValidationError;
import com.herostore.products.service.ProductTypeService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductTypeController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductTypeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProductTypeService productTypeService;

    static final String BASE_URI = "/api/product-types";

    @Test
    void getAllProductTypes() throws Exception {
        var expectedProductTypesFound = asList(mockCupsProductType(), mockShirtsProductType());

        when(productTypeService.getAllProductTypes())
                .thenReturn(expectedProductTypesFound);

        mockMvc.perform(
                get(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(ResponseBodyMatchers.responseContainsJsonCollection(expectedProductTypesFound, ProductTypeDTO.class));
    }

    @Test
    void getProductTypeById() throws Exception {
        var id = 1L;
        var expectedProductType = mockCupsProductType();

        when(productTypeService.getProductTypeById(id))
                .thenReturn(expectedProductType);

        mockMvc.perform(
                get("{base-uri}/{id}", BASE_URI, id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(ResponseBodyMatchers.responseContainsJsonObject(expectedProductType, ProductTypeDTO.class));
    }

    @Test
    void createProductType() throws Exception {
        var newProductType = ProductTypeDTO.builder().name("Cups").build();
        var expectedProductTypeCreated = ProductTypeDTO.builder().id(1L).name("Cups").build();

        when(productTypeService.createProductType(newProductType))
                .thenReturn(expectedProductTypeCreated);

        mockMvc.perform(
                post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SerializationUtils.objectMapper.writeValueAsBytes(newProductType)))
                .andExpect(status().isCreated())
                .andExpect(ResponseBodyMatchers.responseContainsJsonObject(expectedProductTypeCreated, ProductTypeDTO.class));
    }

    @Test
    void createInvalidProductTypeThrowsValidationError() throws Exception {
        var newProductType = ProductTypeDTO.builder().build();

        var expectedErrors = new FieldValidationError[]{
                new FieldValidationError("name", "Name is required", null)
        };

        mockMvc.perform(
                post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SerializationUtils.objectMapper.writeValueAsBytes(newProductType)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(ResponseBodyMatchers.response().containsValidationErrors(expectedErrors))
                .andExpect(jsonPath("$.path", is(BASE_URI)))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void updateProductType() throws Exception {
        var id = 1L;
        var productTypeToUpdate = ProductTypeDTO.builder().id(id).name("Cup").build();
        var updatedProductType = ProductTypeDTO.builder().id(id).name("Cups").build();

        when(productTypeService.updateProductType(id, productTypeToUpdate))
                .thenReturn(updatedProductType);

        mockMvc.perform(
                put("{base-uri}/{id}", BASE_URI, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SerializationUtils.objectMapper.writeValueAsBytes(productTypeToUpdate)))
                .andExpect(status().isOk())
                .andExpect(ResponseBodyMatchers.responseContainsJsonObject(updatedProductType, ProductTypeDTO.class));
    }

    /*@Test
    void updateInvalidProductTypeThrowsValidationError() throws Exception {
        var id = 1L;
        var productTypeToUpdate = ProductTypeDTO.builder().id(id).build();

        var requiredNameValidationError =
                matchValidationError(0, "name", "Name is required.", nullValue());

        mockMvc.perform(
                put("{base-uri}/{id}", BASE_URI, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(productTypeToUpdate)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(responseContainsValidationErrors(1))
                .andExpect(requiredNameValidationError)
                .andExpect(jsonPath("$.path", is(BASE_URI + "/" + id)))
                .andExpect(jsonPath("$.timestamp").exists());
    }*/

    @Test
    void deleteProductTypeById() throws Exception {
        var id = 1L;

        mockMvc.perform(
                delete("{base-uri}/{id}", BASE_URI, id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldThrowExceptionWhenRequestingNonExistingProductType() throws Exception {
        var id = 99L;
        var errorMessage = "Product type not found for id " + id;
        var targetURI = BASE_URI + "/" + id;

        doThrow(new ResourceNotFoundException(errorMessage))
                .when(productTypeService).getProductTypeById(id);

        mockMvc.perform(
                get(targetURI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Resource Not Found")))
                .andExpect(jsonPath("$.statusCode", is(404)))
                .andExpect(jsonPath("$.message", is(errorMessage)))
                .andExpect(jsonPath("$.path", is(targetURI)))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    private ProductTypeDTO mockCupsProductType() {
        return ProductTypeDTO.builder()
                .id(1L)
                .name("Cups")
                .build();
    }

    private ProductTypeDTO mockShirtsProductType() {
        return ProductTypeDTO.builder()
                .id(2L)
                .name("Shirts")
                .build();
    }
}
