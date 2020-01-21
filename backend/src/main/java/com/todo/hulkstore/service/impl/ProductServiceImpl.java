package com.todo.hulkstore.service.impl;

import com.todo.hulkstore.converter.ProductConverter;
import com.todo.hulkstore.domain.Product;
import com.todo.hulkstore.dto.request.ProductRequestDTO;
import com.todo.hulkstore.dto.response.ProductResponseDTO;
import com.todo.hulkstore.exception.ResourceNotFoundException;
import com.todo.hulkstore.exception.ServiceException;
import com.todo.hulkstore.repository.ProductRepository;
import com.todo.hulkstore.service.ProductService;
import com.todo.hulkstore.service.ProductTypeService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductServiceImpl implements ProductService {

    Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class.getName());

    ProductRepository productRepository;
    ProductTypeService productTypeService;
    ProductConverter converter;

    /**
     * Retrieves all existing products.
     *
     * @return all existing products.
     */
    @Override
    public List<ProductResponseDTO> getAllProducts(Optional<Boolean> inStock) {
        try {
            List<Product> productsFound;

            if (inStock.isPresent()) {
                productsFound = inStock.get()
                        ? productRepository.retrieveProductsInStock()
                        : productRepository.retrieveProductsWithoutStock();
            } else {
                productsFound = productRepository.findAll();
            }

            return converter.toProductResponseDTOList(productsFound);
        } catch (Exception e) {
            logger.error("getAllProducts(): Couldn't retrieve all products.", e);
            throw new ServiceException("Couldn't retrieve all products", e);
        }
    }

    /**
     * Retrieves a product by a given id.
     *
     * @param id product's id.
     * @return the product found.
     */
    @Override
    public ProductResponseDTO getProductById(Long id) {
        try {
            var product = productRepository
                    .findById(id)
                    .orElseThrow(() -> {
                        throw new ResourceNotFoundException("Product not found for id " + id);
                    });

            return converter.toProductResponseDTO(product);
        } catch (ResourceNotFoundException rnfExc) {
            logger.error(rnfExc.getMessage());
            throw rnfExc;
        } catch (Exception e) {
            logger.error("getProductById({}): Couldn't retrieve product", id, e);
            throw new ServiceException("Couldn't retrieve product", e);
        }
    }

    /**
     * Creates a product.
     *
     * @param productRequest product to create.
     * @return the created product.
     */
    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO productRequest) {
        try {
            var productTypeId = productRequest.getProductTypeId();
            var productType = converter
                    .toProductType(productTypeService.getProductTypeById(productTypeId));
            var newProduct = converter.toProduct(productRequest, productType);

            return converter.toProductResponseDTO(productRepository.save(newProduct));
        } catch (Exception e) {
            logger.error("createProduct({}): Couldn't create Product", productRequest, e);
            throw new ServiceException("Couldn't create Product", e);
        }
    }

    /**
     * Updates a product
     *
     * @param id                product's id
     * @param updatedProductReq product to update
     * @return the updated product.
     */
    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO updatedProductReq) {
        try {
            var productTypeId = updatedProductReq.getProductTypeId();
            var productType = converter
                    .toProductType(productTypeService.getProductTypeById(productTypeId));
            var updatedProduct = converter.toProduct(updatedProductReq, productType);

            var product = productRepository.findById(id)
                    .orElseThrow(() -> {
                        throw new ResourceNotFoundException("Product not found for id " + id);
                    });

            updatedProduct = updateProductDetails(product, updatedProduct);
            return converter.toProductResponseDTO(productRepository.save(updatedProduct));
        } catch (ResourceNotFoundException rnfExc) {
            logger.error(rnfExc.getMessage());
            throw rnfExc;
        } catch (Exception e) {
            logger.error("updateProduct({}, {}): Couldn't update product", id, updatedProductReq, e);
            throw new ServiceException("Couldn't update product", e);
        }
    }

    /**
     * Deletes a product by a given id.
     *
     * @param id product's id.
     */
    @Override
    public void deleteProductById(Long id) {
        try {
            var productToDelete = productRepository
                    .findById(id)
                    .orElseThrow(() -> {
                        throw new ResourceNotFoundException("Product not found for id " + id);
                    });
            productRepository.delete(productToDelete);
        } catch (ResourceNotFoundException rnfExc) {
            logger.error(rnfExc.getMessage());
            throw rnfExc;
        } catch (Exception e) {
            logger.error("deleteProductById({}): couldn't delete product", id, e);
            throw new ServiceException("Couldn't delete product", e);
        }
    }

    private Product updateProductDetails(Product old, Product updated) {
        return Product.builder()
                .id(old.getId())
                .name(updated.getName())
                .productType(updated.getProductType())
                .price(updated.getPrice())
                .stock(updated.getStock())
                .build();
    }
}
