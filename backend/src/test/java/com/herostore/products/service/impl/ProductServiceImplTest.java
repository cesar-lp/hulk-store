package com.herostore.products.service.impl;

import com.herostore.products.constants.ProductStockCondition;
import com.herostore.products.domain.Product;
import com.herostore.products.domain.ProductType;
import com.herostore.products.dto.ProductTypeDTO;
import com.herostore.products.dto.request.ProductRequest;
import com.herostore.products.dto.response.ProductResponse;
import com.herostore.products.exception.ResourceNotFoundException;
import com.herostore.products.exception.ServiceException;
import com.herostore.products.mapper.ProductMapper;
import com.herostore.products.mapper.ProductTypeMapper;
import com.herostore.products.repository.ProductRepository;
import com.herostore.products.service.ProductTypeService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ProductServiceImplTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    ProductMapper productMapper;

    @Mock
    ProductTypeMapper productTypeMapper;

    @Mock
    ProductTypeService productTypeService;

    @InjectMocks
    ProductServiceImpl productService;

    @AfterEach
    void runAfterEach() {
        verifyNoMoreInteractions(productMapper, productTypeMapper, productTypeService, productRepository);
    }

    @Test
    void shouldGetAllProductsSuccessfully() {
        var existingProducts = mockExistingProducts();
        var expectedProductsToRetrieve = mockExistingProductsResponse();

        when(productRepository.findAll())
                .thenReturn(existingProducts);

        when(productMapper.toProductResponseList(existingProducts))
                .thenReturn(expectedProductsToRetrieve);

        var actualProductsRetrieved = productService.getAllProducts(ProductStockCondition.ALL);

        assertThat(actualProductsRetrieved, samePropertyValuesAs(expectedProductsToRetrieve));

        verify(productRepository, times(1)).findAll();
        verify(productMapper, times(1)).toProductResponseList(existingProducts);
    }

    @Test
    void shouldGetProductsByStockConditionSuccessfully() {
        var productsInStock = mockProductsInStock();
        var expectedProductsInStockToRetrieve = mockProductsInStockResponse();

        when(productRepository.retrieveProductsByStockCondition(true))
                .thenReturn(productsInStock);

        when(productMapper.toProductResponseList(productsInStock))
                .thenReturn(expectedProductsInStockToRetrieve);

        var actualProductsRetrieved = productService.getAllProducts(ProductStockCondition.AVAILABLE);

        assertThat(actualProductsRetrieved, samePropertyValuesAs(expectedProductsInStockToRetrieve));

        verify(productRepository, times(1)).retrieveProductsByStockCondition(true);
        verify(productMapper, times(1)).toProductResponseList(productsInStock);
    }

    @Test
    void shouldGetProductByIdSuccessfully() {
        var id = 1L;
        var existingProduct = mockExistingProduct();
        var expectedProductToRetrieve = mockExistingProductResponse();

        when(productRepository.findById(id))
                .thenReturn(Optional.of((existingProduct)));

        when(productMapper.toProductResponse(existingProduct))
                .thenReturn(expectedProductToRetrieve);

        var actualProductRetrieved = productService.getProductById(id);

        assertThat(actualProductRetrieved, samePropertyValuesAs(expectedProductToRetrieve));

        verify(productRepository, times(1)).findById(1L);
        verify(productMapper, times(1)).toProductResponse(existingProduct);
    }

    @Test
    void shouldThrowExceptionWhenRetrievingNonExistingProductById() {
        var id = 99L;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductById(id));

        verify(productRepository, times(1)).findById(id);
    }

    @Test
    void shouldCreateProductSuccessfully() {
        var newProductRequest = mockNewProductRequest();
        var newProduct = mockNewProduct();

        var productTypeId = 1L;

        var productType = ProductType.builder()
                .id(1L)
                .name("Cups")
                .build();

        var productTypeDTO = new ProductTypeDTO(1L, "Cups");

        var createdProduct = mockExistingProduct();
        var expectedProductToBeCreated = mockExistingProductResponse();

        when(productTypeService.getProductTypeById(productTypeId))
                .thenReturn(productTypeDTO);

        when(productTypeMapper.toProductType(productTypeDTO))
                .thenReturn(productType);

        when(productMapper.toProduct(newProductRequest, productType))
                .thenReturn(newProduct);

        when(productRepository.save(newProduct))
                .thenReturn(createdProduct);

        when(productMapper.toProductResponse(createdProduct))
                .thenReturn(expectedProductToBeCreated);

        var actualProductCreated = productService.createProduct(newProductRequest);

        assertThat(actualProductCreated, samePropertyValuesAs(expectedProductToBeCreated));

        verify(productTypeService, times(1)).getProductTypeById(productTypeId);
        verify(productTypeMapper, times(1)).toProductType(productTypeDTO);
        verify(productMapper, times(1)).toProduct(newProductRequest, productType);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productMapper, times(1)).toProductResponse(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductForNonExistingProductType() {
        var expectedErrorMessage = "Product type not found for id 1";
        var newProductRequest = mockNewProductRequest();
        var productTypeId = 1L;

        when(productTypeService.getProductTypeById(productTypeId))
                .thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        var ex = assertThrows(ServiceException.class,
                () -> productService.createProduct(newProductRequest));

        assertEquals(expectedErrorMessage, ex.getCause().getMessage());
        verify(productTypeService, times(1)).getProductTypeById(productTypeId);
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        var productId = 1L;
        var updateProductRequest = mockUpdateProductRequest();
        var productToUpdate = mockProductToUpdate();

        var productTypeId = 1L;
        var productType = ProductType.builder()
                .id(1L)
                .name("Cups")
                .build();
        var productTypeDTO = new ProductTypeDTO(1L, "Cups");

        var existingProduct = mockExistingProduct();
        var expectedProductToBeUpdated = mockUpdatedProductResponseDTO();

        when(productTypeService.getProductTypeById(productTypeId))
                .thenReturn(productTypeDTO);

        when(productTypeMapper.toProductType(productTypeDTO))
                .thenReturn(productType);

        when(productMapper.toProduct(updateProductRequest, productType))
                .thenReturn(productToUpdate);

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(existingProduct));

        when(productRepository.save(any(Product.class)))
                .thenReturn(existingProduct);

        when(productMapper.toProductResponse(any(Product.class)))
                .thenReturn(expectedProductToBeUpdated);

        var actualProductUpdated = productService.updateProduct(productId, updateProductRequest);

        assertThat(actualProductUpdated, samePropertyValuesAs(expectedProductToBeUpdated));

        verify(productTypeService, times(1)).getProductTypeById(productTypeId);
        verify(productTypeMapper, times(1)).toProductType(productTypeDTO);
        verify(productMapper, times(1)).toProduct(updateProductRequest, productType);
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productMapper, times(1)).toProductResponse(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingProductForNonExistingProductType() {
        var productId = 1L;
        var productTypeId = 1L;
        var expectedErrorMessage = "Product type not found for id 1";
        var updateProductRequest = mockUpdateProductRequest();

        when(productTypeService.getProductTypeById(productTypeId))
                .thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        var ex = assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct(productId, updateProductRequest));

        assertEquals(expectedErrorMessage, ex.getMessage());
        verify(productTypeService, times(1)).getProductTypeById(productTypeId);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingProduct() {
        var id = 99L;
        var productTypeId = 1L;
        var productType = ProductType.builder()
                .id(productTypeId)
                .name("Cups")
                .build();

        var productTypeDTO = new ProductTypeDTO(productTypeId, "Cups");
        var updateProductRequest = mockUpdateProductRequest();
        var productToUpdate = mockExistingProduct();

        when(productTypeService.getProductTypeById(productTypeId))
                .thenReturn(productTypeDTO);

        when(productTypeMapper.toProductType(productTypeDTO))
                .thenReturn(productType);

        when(productMapper.toProduct(updateProductRequest, productType))
                .thenReturn(productToUpdate);

        when(productRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct(id, updateProductRequest));

        verify(productTypeService, times(1)).getProductTypeById(productTypeId);
        verify(productTypeMapper, times(1)).toProductType(productTypeDTO);
        verify(productMapper, times(1)).toProduct(updateProductRequest, productType);
        verify(productRepository, times(1)).findById(id);
    }

    @Test
    void shouldDeleteProductByIdSuccessfully() {
        var productToDelete = mockExistingProduct();
        var id = productToDelete.getId();

        when(productRepository.findById(id)).thenReturn(Optional.of(productToDelete));

        productService.deleteProductById(id);

        verify(productRepository, times(1)).findById(id);
        verify(productRepository, times(1)).delete(productToDelete);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingProductById() {
        var id = 99L;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProductById(id));
        verify(productRepository, times(1)).findById(id);
    }

    private Product mockNewProduct() {
        var cup = ProductType.builder()
                .id(1L)
                .name("Cup")
                .build();

        return Product.builder()
                .name("Iron Man Cup")
                .productType(cup)
                .price(BigDecimal.valueOf(25.00))
                .stock(25)
                .build();
    }

    private Product mockExistingProduct() {
        var cup = ProductType.builder()
                .id(1L)
                .name("Cup")
                .build();

        return Product.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(cup)
                .stock(25)
                .price(BigDecimal.valueOf(25.00))
                .build();
    }

    private ProductRequest mockUpdateProductRequest() {
        return ProductRequest.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productTypeId(1L)
                .stock(100)
                .price(BigDecimal.valueOf(50.00))
                .build();
    }

    private Product mockProductToUpdate() {
        var cup = ProductType.builder()
                .id(1L)
                .name("Cup")
                .build();

        return Product.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(cup)
                .stock(100)
                .price(BigDecimal.valueOf(50.00))
                .build();
    }

    private ProductResponse mockUpdatedProductResponseDTO() {
        return ProductResponse.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(new ProductTypeDTO(1L, "Cup"))
                .stock(100)
                .price(BigDecimal.valueOf(50.00))
                .build();
    }

    private ProductResponse mockExistingProductResponse() {
        return ProductResponse.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(new ProductTypeDTO(1L, "Cup"))
                .stock(25)
                .price(BigDecimal.valueOf(25.00))
                .build();
    }

    private ProductRequest mockNewProductRequest() {
        return ProductRequest.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productTypeId(1L)
                .stock(25)
                .price(BigDecimal.valueOf(25.00))
                .build();
    }

    private List<Product> mockExistingProducts() {
        var shirtProductType = ProductType.builder().id(1L).name("Shirt").build();

        var ironManShirt = Product.builder()
                .id(1L)
                .name("Iron Man Shirt")
                .productType(shirtProductType)
                .stock(25)
                .price(BigDecimal.valueOf(60.00))
                .build();

        var spiderManShirt = Product.builder()
                .id(2L)
                .name("Spider Man Shirt")
                .productType(shirtProductType)
                .stock(25)
                .price(BigDecimal.valueOf(45.00))
                .build();

        var batmanShirt = Product.builder()
                .id(3L)
                .name("Batman Shirt")
                .productType(shirtProductType)
                .stock(25)
                .price(BigDecimal.valueOf(40.00))
                .build();

        return asList(ironManShirt, spiderManShirt, batmanShirt);
    }

    private List<ProductResponse> mockExistingProductsResponse() {
        var shirtProductType = new ProductTypeDTO(1L, "Shirt");

        var ironManShirt = ProductResponse.builder()
                .id(1L)
                .name("Iron Man Shirt")
                .productType(shirtProductType)
                .stock(25)
                .price(BigDecimal.valueOf(60.00))
                .build();

        var spiderManShirt = ProductResponse.builder()
                .id(2L)
                .name("Spider Man Shirt")
                .productType(shirtProductType)
                .stock(25)
                .price(BigDecimal.valueOf(45.00))
                .build();

        var batmanShirt = ProductResponse.builder()
                .id(3L)
                .name("Batman Shirt")
                .productType(shirtProductType)
                .stock(25)
                .price(BigDecimal.valueOf(40.00))
                .build();

        return asList(ironManShirt, spiderManShirt, batmanShirt);
    }

    private List<Product> mockProductsInStock() {
        return singletonList(mockExistingProducts().get(1));
    }

    private List<ProductResponse> mockProductsInStockResponse() {
        return singletonList(mockExistingProductsResponse().get(1));
    }
}