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
        List<Product> existingProducts = getExistingProductsList();
        List<ProductDTO> existingProductsDTOs = getExistingProductsDTOList();

        when(productRepository.findAll()).thenReturn(existingProducts);
        when(productConverter.toProductDTOList(existingProducts)).thenReturn(existingProductsDTOs);

        List<ProductDTO> foundProducts = productService.getAllProducts();

        assertEquals(existingProductsDTOs, foundProducts);
        verify(productRepository, times(1)).findAll();
        verify(productConverter, times(1)).toProductDTOList(existingProducts);
    }

    @Test
    void shouldGetProductByIdSuccessfully() {
        Product existingProduct = getExistingProductMock();
        Long id = existingProduct.getId();
        ProductDTO expectedProduct = getExistingProductDTOMock();

        when(productRepository.findById(id)).thenReturn(Optional.of((existingProduct)));
        when(productConverter.toProductDTO(existingProduct)).thenReturn(expectedProduct);

        ProductDTO foundProduct = productService.getProductById(id);

        assertEquals(expectedProduct, foundProduct);
        verify(productRepository, times(1)).findById(1L);
        verify(productConverter, times(1)).toProductDTO(existingProduct);
    }

    @Test
    void shouldThrowExceptionWhenRetrievingNonExistingProductById() {
        Long id = 99L;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(id));
        verify(productRepository, times(1)).findById(id);
    }

    @Test
    void shouldCreateProductSuccessfully() {
        Product newProduct = getNewProductMock();
        ProductDTO newProductDTO = getNewProductDTOMock();
        Product createdProduct = getExistingProductMock();
        ProductDTO expectedProductDTO = getExistingProductDTOMock();

        when(productConverter.toProduct(newProductDTO)).thenReturn(newProduct);
        when(productRepository.save(newProduct)).thenReturn(createdProduct);
        when(productConverter.toProductDTO(createdProduct)).thenReturn(expectedProductDTO);

        ProductDTO createdProductDTO = productService.createProduct(newProductDTO);

        assertEquals(expectedProductDTO, createdProductDTO);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productConverter, times(1)).toProduct(any(ProductDTO.class));
        verify(productConverter, times(1)).toProductDTO(any(Product.class));
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        Long id = 1L;
        Product existingProduct = getExistingProductMock();
        ProductDTO updatedProductDTO = ProductDTO.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(new ProductTypeDTO(1L, "Cup"))
                .price(BigDecimal.valueOf(25.00))
                .stock(5)
                .build();

        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(new ProductType(1L, "Cup"))
                .price(BigDecimal.valueOf(25.00))
                .stock(5)
                .build();

        when(productConverter.toProduct(updatedProductDTO)).thenReturn(updatedProduct);
        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(updatedProduct);
        when(productConverter.toProductDTO(updatedProduct)).thenReturn(updatedProductDTO);

        ProductDTO returnedProductDTO = productService.updateProduct(id, updatedProductDTO);

        assertEquals(updatedProductDTO, returnedProductDTO);
        verify(productConverter, times(1)).toProduct(updatedProductDTO);
        verify(productRepository, times(1)).findById(id);
        verify(productRepository, times(1)).save(existingProduct);
        verify(productConverter, times(1)).toProductDTO(updatedProduct);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingProduct() {
        Long id = 99L;
        ProductDTO productDTOToUpdate = getExistingProductDTOMock();
        Product productToUpdate = getExistingProductMock();

        when(productConverter.toProduct(productDTOToUpdate)).thenReturn(productToUpdate);
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct(id, productDTOToUpdate));
        verify(productConverter, times(1)).toProduct(productDTOToUpdate);
        verify(productRepository, times(1)).findById(id);
    }

    @Test
    void shouldDeleteProductByIdSuccessfully() {
        Product productToDelete = getExistingProductMock();
        Long id = productToDelete.getId();

        when(productRepository.findById(id)).thenReturn(Optional.of(productToDelete));

        productService.deleteProductById(id);

        verify(productRepository, times(1)).findById(id);
        verify(productRepository, times(1)).delete(productToDelete);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingProductById() {
        Long id = 99L;

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
        ProductType shirtProductType = new ProductType(1L, "Shirt");
        return Arrays.asList(
                new Product(1L, "Iron Man Shirt", shirtProductType, BigDecimal.valueOf(50.00), 5),
                new Product(2L, "Spiderman Shirt", shirtProductType, BigDecimal.valueOf(50.00), 5),
                new Product(3L, "Batman Shirt", shirtProductType, BigDecimal.valueOf(50.00), 5)
        );
    }

    private List<ProductDTO> getExistingProductsDTOList() {
        ProductTypeDTO shirtProductTypeDTO = new ProductTypeDTO(1L, "Shirt");
        return Arrays.asList(
                new ProductDTO(1L, "Iron Man Shirt", shirtProductTypeDTO, BigDecimal.valueOf(50.00), 5),
                new ProductDTO(2L, "Spiderman Shirt", shirtProductTypeDTO, BigDecimal.valueOf(50.00), 5),
                new ProductDTO(3L, "Batman Shirt", shirtProductTypeDTO, BigDecimal.valueOf(50.00), 5)
        );
    }
}