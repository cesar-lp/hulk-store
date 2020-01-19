package com.todo.hulkstore.service.impl;

import com.todo.hulkstore.converter.ProductConverter;
import com.todo.hulkstore.domain.Product;
import com.todo.hulkstore.domain.ProductType;
import com.todo.hulkstore.dto.ProductTypeDTO;
import com.todo.hulkstore.dto.request.ProductRequestDTO;
import com.todo.hulkstore.dto.response.ProductResponseDTO;
import com.todo.hulkstore.exception.ResourceNotFoundException;
import com.todo.hulkstore.exception.ServiceException;
import com.todo.hulkstore.repository.ProductRepository;
import com.todo.hulkstore.service.ProductTypeService;
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
    ProductConverter productConverter;

    @Mock
    ProductTypeService productTypeService;

    @InjectMocks
    ProductServiceImpl productService;

    @AfterEach
    void runAfterEach() {
        verifyNoMoreInteractions(productConverter, productRepository);
    }

    @Test
    void shouldGetAllProductsSuccessfully() {
        var existingProducts = mockExistingProducts();
        var expectedProductsToRetrieve = mockExistingProductsResponseDTO();

        when(productRepository.findAll())
                .thenReturn(existingProducts);

        when(productConverter.toProductResponseDTOList(existingProducts))
                .thenReturn(expectedProductsToRetrieve);

        var actualProductsRetrieved = productService.getAllProducts();

        assertThat(actualProductsRetrieved, samePropertyValuesAs(expectedProductsToRetrieve));

        verify(productRepository, times(1)).findAll();
        verify(productConverter, times(1)).toProductResponseDTOList(existingProducts);
    }

    @Test
    void shouldGetProductByIdSuccessfully() {
        var id = 1L;
        var existingProduct = mockExistingProduct();
        var expectedProductToRetrieve = mockExistingProductResponseDTO();

        when(productRepository.findById(id))
                .thenReturn(Optional.of((existingProduct)));

        when(productConverter.toProductResponseDTO(existingProduct))
                .thenReturn(expectedProductToRetrieve);

        var actualProductRetrieved = productService.getProductById(id);

        assertThat(actualProductRetrieved, samePropertyValuesAs(expectedProductToRetrieve));

        verify(productRepository, times(1)).findById(1L);
        verify(productConverter, times(1)).toProductResponseDTO(existingProduct);
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
        var newProduct = getNewProductMock();

        var productTypeId = 1L;
        var productType = new ProductType(1L, "Cups");
        var productTypeDTO = new ProductTypeDTO(1L, "Cups");

        var createdProduct = mockExistingProduct();
        var expectedProductToBeCreated = mockExistingProductResponseDTO();

        when(productTypeService.getProductTypeById(productTypeId))
                .thenReturn(productTypeDTO);

        when(productConverter.toProductType(productTypeDTO))
                .thenReturn(productType);

        when(productConverter.toProduct(newProductRequest, productType))
                .thenReturn(newProduct);

        when(productRepository.save(newProduct))
                .thenReturn(createdProduct);

        when(productConverter.toProductResponseDTO(createdProduct))
                .thenReturn(expectedProductToBeCreated);

        var actualProductCreated = productService.createProduct(newProductRequest);

        assertThat(actualProductCreated, samePropertyValuesAs(expectedProductToBeCreated));

        verify(productTypeService, times(1)).getProductTypeById(productTypeId);
        verify(productConverter, times(1)).toProductType(productTypeDTO);
        verify(productConverter, times(1)).toProduct(newProductRequest, productType);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productConverter, times(1)).toProductResponseDTO(any(Product.class));
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
        var productType = new ProductType(1L, "Cups");
        var productTypeDTO = new ProductTypeDTO(1L, "Cups");

        var existingProduct = mockExistingProduct();
        var expectedProductToBeUpdated = mockUpdatedProductResponseDTO();

        when(productTypeService.getProductTypeById(productTypeId))
                .thenReturn(productTypeDTO);

        when(productConverter.toProductType(productTypeDTO))
                .thenReturn(productType);

        when(productConverter.toProduct(updateProductRequest, productType))
                .thenReturn(productToUpdate);

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(existingProduct));

        when(productRepository.save(any(Product.class)))
                .thenReturn(existingProduct);

        when(productConverter.toProductResponseDTO(any(Product.class)))
                .thenReturn(expectedProductToBeUpdated);

        var actualProductUpdated = productService.updateProduct(productId, updateProductRequest);

        assertThat(actualProductUpdated, samePropertyValuesAs(expectedProductToBeUpdated));

        verify(productTypeService, times(1)).getProductTypeById(productTypeId);
        verify(productConverter, times(1)).toProductType(productTypeDTO);
        verify(productConverter, times(1)).toProduct(updateProductRequest, productType);
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productConverter, times(1)).toProductResponseDTO(any(Product.class));
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
        var productType = new ProductType(productTypeId, "Cups");
        var productTypeDTO = new ProductTypeDTO(productTypeId, "Cups");
        var updateProductRequest = mockUpdateProductRequest();
        var productToUpdate = mockExistingProduct();

        when(productTypeService.getProductTypeById(productTypeId))
                .thenReturn(productTypeDTO);

        when(productConverter.toProductType(productTypeDTO))
                .thenReturn(productType);

        when(productConverter.toProduct(updateProductRequest, productType))
                .thenReturn(productToUpdate);

        when(productRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct(id, updateProductRequest));

        verify(productTypeService, times(1)).getProductTypeById(productTypeId);
        verify(productConverter, times(1)).toProductType(productTypeDTO);
        verify(productConverter, times(1)).toProduct(updateProductRequest, productType);
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

    private Product getNewProductMock() {
        return Product.builder()
                .name("Iron Man Cup")
                .productType(new ProductType(1L, "Cup"))
                .price(BigDecimal.valueOf(25.00))
                .build();
    }

    private Product mockExistingProduct() {
        return Product.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(new ProductType(1L, "Cup"))
                .stock(25)
                .price(BigDecimal.valueOf(25.00))
                .build();
    }

    private ProductRequestDTO mockUpdateProductRequest() {
        return ProductRequestDTO.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productTypeId(1L)
                .stock(100)
                .price(BigDecimal.valueOf(50.00))
                .build();
    }

    private Product mockProductToUpdate() {
        return Product.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(new ProductType(1L, "Cup"))
                .stock(100)
                .price(BigDecimal.valueOf(50.00))
                .build();
    }

    private ProductResponseDTO mockUpdatedProductResponseDTO() {
        return ProductResponseDTO.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(new ProductTypeDTO(1L, "Cup"))
                .stock(100)
                .price(BigDecimal.valueOf(50.00))
                .build();
    }

    private ProductResponseDTO mockExistingProductResponseDTO() {
        return ProductResponseDTO.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(new ProductTypeDTO(1L, "Cup"))
                .stock(25)
                .price(BigDecimal.valueOf(25.00))
                .build();
    }

    private ProductRequestDTO mockNewProductRequest() {
        return ProductRequestDTO.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productTypeId(1L)
                .stock(25)
                .price(BigDecimal.valueOf(25.00))
                .build();
    }

    private List<Product> mockExistingProducts() {
        var shirtProductType = new ProductType(1L, "Shirt");

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

    private List<ProductResponseDTO> mockExistingProductsResponseDTO() {
        var shirtProductType = new ProductTypeDTO(1L, "Shirt");

        var ironManShirt = ProductResponseDTO.builder()
                .id(1L)
                .name("Iron Man Shirt")
                .productType(shirtProductType)
                .stock(25)
                .price(BigDecimal.valueOf(60.00))
                .build();

        var spiderManShirt = ProductResponseDTO.builder()
                .id(2L)
                .name("Spider Man Shirt")
                .productType(shirtProductType)
                .stock(25)
                .price(BigDecimal.valueOf(45.00))
                .build();

        var batmanShirt = ProductResponseDTO.builder()
                .id(3L)
                .name("Batman Shirt")
                .productType(shirtProductType)
                .stock(25)
                .price(BigDecimal.valueOf(40.00))
                .build();

        return asList(ironManShirt, spiderManShirt, batmanShirt);
    }
}