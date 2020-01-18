package com.todo1.hulkstore.service.impl;

import com.todo1.hulkstore.converter.ProductConverter;
import com.todo1.hulkstore.domain.Product;
import com.todo1.hulkstore.domain.ProductType;
import com.todo1.hulkstore.dto.ProductDTO;
import com.todo1.hulkstore.dto.ProductTypeDTO;
import com.todo1.hulkstore.exception.ResourceNotFoundException;
import com.todo1.hulkstore.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductConverter productConverter;

    @InjectMocks
    private ProductServiceImpl productService;

    @AfterEach
    void runAfterEach() {
        verifyNoMoreInteractions(productConverter, productRepository);
    }

    @Test
    void shouldGetAllProductsSuccessfully() {
        var existingProducts = getExistingProductsList();
        var existingProductsDTOs = getExistingProductsDTOList();

        when(productRepository.findAll()).thenReturn(existingProducts);
        when(productConverter.toProductDTOList(existingProducts)).thenReturn(existingProductsDTOs);

        var foundProducts = productService.getAllProducts();

        assertEquals(existingProductsDTOs, foundProducts);
        verify(productRepository, times(1)).findAll();
        verify(productConverter, times(1)).toProductDTOList(existingProducts);
    }

    @Test
    void shouldGetProductByIdSuccessfully() {
        var existingProduct = getExistingProductMock();
        var id = existingProduct.getId();
        var expectedProduct = getExistingProductDTOMock();

        when(productRepository.findById(id)).thenReturn(Optional.of((existingProduct)));
        when(productConverter.toProductDTO(existingProduct)).thenReturn(expectedProduct);

        var foundProduct = productService.getProductById(id);

        assertEquals(expectedProduct, foundProduct);
        verify(productRepository, times(1)).findById(1L);
        verify(productConverter, times(1)).toProductDTO(existingProduct);
    }

    @Test
    void shouldThrowExceptionWhenRetrievingNonExistingProductById() {
        var id = 99L;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(id));
        verify(productRepository, times(1)).findById(id);
    }

    @Test
    void shouldCreateProductSuccessfully() {
        var newProduct = getNewProductMock();
        var newProductDTO = getNewProductDTOMock();
        var createdProduct = getExistingProductMock();
        var expectedProductDTO = getExistingProductDTOMock();

        when(productConverter.toProduct(newProductDTO)).thenReturn(newProduct);
        when(productRepository.save(newProduct)).thenReturn(createdProduct);
        when(productConverter.toProductDTO(createdProduct)).thenReturn(expectedProductDTO);

        var createdProductDTO = productService.createProduct(newProductDTO);

        assertEquals(expectedProductDTO, createdProductDTO);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productConverter, times(1)).toProduct(any(ProductDTO.class));
        verify(productConverter, times(1)).toProductDTO(any(Product.class));
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        var id = 1L;
        var existingProduct = getExistingProductMock();
        var updatedProductDTO = ProductDTO.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(new ProductTypeDTO(1L, "Cup"))
                .price(BigDecimal.valueOf(25.00))
                .stock(5)
                .build();

        var updatedProduct = Product.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(new ProductType(1L, "Cup"))
                .price(BigDecimal.valueOf(25.00))
                .stock(5)
                .build();

        when(productConverter.toProduct(updatedProductDTO)).thenReturn(updatedProduct);
        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(updatedProduct)).thenReturn(updatedProduct);
        when(productConverter.toProductDTO(updatedProduct)).thenReturn(updatedProductDTO);

        var returnedProductDTO = productService.updateProduct(id, updatedProductDTO);

        assertEquals(updatedProductDTO, returnedProductDTO);
        verify(productConverter, times(1)).toProduct(updatedProductDTO);
        verify(productRepository, times(1)).findById(id);
        verify(productRepository, times(1)).save(updatedProduct);
        verify(productConverter, times(1)).toProductDTO(updatedProduct);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingProduct() {
        var id = 99L;
        var productDTOToUpdate = getExistingProductDTOMock();
        var productToUpdate = getExistingProductMock();

        when(productConverter.toProduct(productDTOToUpdate)).thenReturn(productToUpdate);
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct(id, productDTOToUpdate));
        verify(productConverter, times(1)).toProduct(productDTOToUpdate);
        verify(productRepository, times(1)).findById(id);
    }

    @Test
    void shouldDeleteProductByIdSuccessfully() {
        var productToDelete = getExistingProductMock();
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
                .stock(20)
                .build();
    }

    private Product getExistingProductMock() {
        return Product.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(new ProductType(1L, "Cup"))
                .price(BigDecimal.valueOf(25.00))
                .stock(20)
                .build();
    }

    private ProductDTO getNewProductDTOMock() {
        return ProductDTO.builder()
                .name("Iron Man Cup")
                .productType(new ProductTypeDTO(1L, "Cup"))
                .price(BigDecimal.valueOf(25.00))
                .stock(20)
                .build();
    }

    private ProductDTO getExistingProductDTOMock() {
        return ProductDTO.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(new ProductTypeDTO(1L, "Cup"))
                .price(BigDecimal.valueOf(25.00))
                .stock(20)
                .build();
    }

    private List<Product> getExistingProductsList() {
        var shirtProductType = new ProductType(1L, "Shirt");

        var ironManShirt = Product.builder()
                .id(1L)
                .name("Iron Man Shirt")
                .productType(shirtProductType)
                .price(BigDecimal.valueOf(60.00))
                .stock(10)
                .build();

        var spiderManShirt = Product.builder()
                .id(2L)
                .name("Spider Man Shirt")
                .productType(shirtProductType)
                .price(BigDecimal.valueOf(45.00))
                .stock(5)
                .build();

        var batmanShirt = Product.builder()
                .id(3L)
                .name("Batman Shirt")
                .productType(shirtProductType)
                .price(BigDecimal.valueOf(40.00))
                .stock(5)
                .build();

        return Arrays.asList(ironManShirt, spiderManShirt, batmanShirt);
    }

    private List<ProductDTO> getExistingProductsDTOList() {
        var shirtProductType = new ProductTypeDTO(1L, "Shirt");

        var ironManShirt = ProductDTO.builder()
                .id(1L)
                .name("Iron Man Shirt")
                .productType(shirtProductType)
                .price(BigDecimal.valueOf(60.00))
                .stock(10)
                .build();

        var spiderManShirt = ProductDTO.builder()
                .id(2L)
                .name("Spider Man Shirt")
                .productType(shirtProductType)
                .price(BigDecimal.valueOf(45.00))
                .stock(5)
                .build();

        var batmanShirt = ProductDTO.builder()
                .id(3L)
                .name("Batman Shirt")
                .productType(shirtProductType)
                .price(BigDecimal.valueOf(40.00))
                .stock(5)
                .build();

        return Arrays.asList(ironManShirt, spiderManShirt, batmanShirt);
    }
}