package com.herostore.products.controller;

import com.herostore.products.constants.ProductStockCondition;
import com.herostore.products.dto.ProductTypeDTO;
import com.herostore.products.dto.request.ProductRequest;
import com.herostore.products.dto.response.ProductResponse;
import com.herostore.products.exception.ResourceNotFoundException;
import com.herostore.products.exception.error.FieldValidationError;
import com.herostore.products.service.ProductService;
import com.herostore.products.utils.ResponseBodyMatchers;
import com.herostore.products.utils.SerializationUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProductService productService;

    static final String BASE_URI = "/api/products";

    @Test
    void getAllProducts() throws Exception {
        var expectedProductsFound = asList(mockIronManCupProductResponse(), mockBatmanCupProductResponse());

        when(productService.getAllProducts(ProductStockCondition.ALL))
                .thenReturn(expectedProductsFound);

        mockMvc.perform(
                get(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(ResponseBodyMatchers.responseContainsJsonCollection(expectedProductsFound, ProductResponse.class));
    }

    @Test
    void exportProductsToFile() throws Exception {
        var response = mockMvc.perform(
                get(BASE_URI + "/export")
                        .param("format", "csv")
                        .param("stock", ProductStockCondition.ALL.getValue())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, response.getContentType());
        assertEquals("attachment; filename=products.csv",
                response.getHeaderValue("content-disposition"));
    }

    @Test
    void getProductById() throws Exception {
        var id = 1L;
        var expectedProduct = mockIronManCupProductResponse();

        when(productService.getProductById(id))
                .thenReturn(expectedProduct);

        mockMvc.perform(
                get("{base-uri}/{id}", BASE_URI, id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(ResponseBodyMatchers.responseContainsJsonObject(expectedProduct, ProductResponse.class));
    }

    @Test
    void createProduct() throws Exception {
        var newProduct = ProductRequest.builder()
                .name("Iron Man Cup")
                .productTypeId(1L)
                .stock(20)
                .price(BigDecimal.valueOf(10.00))
                .build();

        var expectedProductCreated = mockIronManCupProductResponse();

        when(productService.createProduct(newProduct))
                .thenReturn(expectedProductCreated);

        mockMvc.perform(
                post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SerializationUtils.objectMapper.writeValueAsBytes(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(ResponseBodyMatchers.responseContainsJsonObject(expectedProductCreated, ProductResponse.class));
    }

    @Test
    @Disabled
    void createInvalidProductThrowsValidationError() throws Exception {
        var newProduct = ProductRequest.builder().build();

        var validationErrorsExpected = new FieldValidationError[]{
                new FieldValidationError("name", "Name is required", nullValue()),
                new FieldValidationError("productTypeId", "Product type id is required", nullValue()),
                new FieldValidationError("stock", "Stock is required", nullValue()),
                new FieldValidationError("price", "Price is required", nullValue())
        };

        mockMvc.perform(
                post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SerializationUtils.objectMapper.writeValueAsBytes(newProduct)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(ResponseBodyMatchers.response().containsValidationErrors(validationErrorsExpected))
                .andExpect(jsonPath("$.path", is(BASE_URI)))
                .andExpect(jsonPath("$.timestamp").exists());

        validationErrorsExpected = new FieldValidationError[]{
                new FieldValidationError("stock", "Stock cannot be negative", -1),
                new FieldValidationError("price", "Price cannot be negative", -1),
        };

        newProduct = ProductRequest.builder()
                .name("Iron Man Cup")
                .productTypeId(1L)
                .stock(-1)
                .price(BigDecimal.valueOf(-1))
                .build();

        mockMvc.perform(
                post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SerializationUtils.objectMapper.writeValueAsBytes(newProduct)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(ResponseBodyMatchers.response().containsValidationErrors(validationErrorsExpected))
                .andExpect(jsonPath("$.path", is(BASE_URI)))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void updateProduct() throws Exception {
        var id = 1L;

        var productToUpdate = ProductRequest.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productTypeId(1L)
                .stock(15)
                .price(BigDecimal.valueOf(10.00))
                .build();

        var expectedProductUpdated = ProductResponse.builder()
                .id(id)
                .name("Iron Man Cup")
                .productType(mockCupsProductType())
                .stock(15)
                .price(BigDecimal.valueOf(10.00))
                .build();

        when(productService.updateProduct(id, productToUpdate))
                .thenReturn(expectedProductUpdated);

        mockMvc.perform(
                put("{base-uri}/{id}", BASE_URI, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SerializationUtils.objectMapper.writeValueAsBytes(productToUpdate)))
                .andExpect(status().isOk())
                .andExpect(ResponseBodyMatchers.responseContainsJsonObject(expectedProductUpdated, ProductResponse.class));
    }

    @Test
    @Disabled
    void updateInvalidProductThrowsValidationError() throws Exception {
        var id = 1L;
        var productToUpdate = ProductRequest.builder().build();

        var validationErrorsExpected = new FieldValidationError[]{
                new FieldValidationError("name", "Name is required", nullValue()),
                new FieldValidationError("productTypeId", "Product type id is required", nullValue()),
                new FieldValidationError("stock", "Stock is required", nullValue()),
                new FieldValidationError("price", "Price is required", nullValue())
        };

        mockMvc.perform(
                put("{base-uri}/{id}", BASE_URI, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SerializationUtils.objectMapper.writeValueAsBytes(productToUpdate)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(ResponseBodyMatchers.response().containsValidationErrors(validationErrorsExpected))
                .andExpect(jsonPath("$.path", is(BASE_URI)))
                .andExpect(jsonPath("$.timestamp").exists());

        validationErrorsExpected = new FieldValidationError[]{
                new FieldValidationError("stock", "Stock cannot be negative", -1),
                new FieldValidationError("price", "Price cannot be negative", -1),
        };

        productToUpdate = ProductRequest.builder()
                .name("Iron Man Cup")
                .productTypeId(1L)
                .stock(-1)
                .price(BigDecimal.valueOf(-1))
                .build();

        mockMvc.perform(
                put("{base-uri}/{id}", BASE_URI, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SerializationUtils.objectMapper.writeValueAsBytes(productToUpdate)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(ResponseBodyMatchers.response().containsValidationErrors(validationErrorsExpected))
                .andExpect(jsonPath("$.path", is(BASE_URI)))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void deleteProductById() throws Exception {
        var id = 1L;

        mockMvc.perform(
                delete("{base-uri}/{id}", BASE_URI, id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldThrowExceptionWhenRequestingNonExistingProduct() throws Exception {
        var id = 99L;
        var errorMessage = "Product not found for id " + id;
        var targetURI = BASE_URI + "/" + id;

        doThrow(new ResourceNotFoundException(errorMessage))
                .when(productService).getProductById(id);

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

    private ProductResponse mockIronManCupProductResponse() {
        return ProductResponse.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(mockCupsProductType())
                .price(BigDecimal.valueOf(20.00))
                .stock(10)
                .build();
    }

    private ProductResponse mockBatmanCupProductResponse() {
        return ProductResponse.builder()
                .id(2L)
                .name("Batman Cup")
                .productType(mockCupsProductType())
                .price(BigDecimal.valueOf(15.00))
                .stock(10)
                .build();
    }
}