package com.todo.hulkstore.service.impl;

import com.todo.hulkstore.converter.ProductConverter;
import com.todo.hulkstore.dto.ProductDTO;
import com.todo.hulkstore.exception.ResourceNotFoundException;
import com.todo.hulkstore.exception.ServiceException;
import com.todo.hulkstore.repository.ProductRepository;
import com.todo.hulkstore.service.ProductService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductServiceImpl implements ProductService {

    Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class.getName());

    ProductRepository productRepository;
    ProductConverter converter;

    @Override
    public List<ProductDTO> getAllProducts() {
        try {
            return converter.toProductDTOList(productRepository.findAll());
        } catch (Exception e) {
            logger.error("getAllProducts(): Couldn't retrieve all products.", e);
            throw new ServiceException("Couldn't retrieve all products", e);
        }
    }

    @Override
    public ProductDTO getProductById(Long id) {
        try {
            var product = productRepository
                    .findById(id)
                    .orElseThrow(() -> {
                        logger.error("getProductById({}): Product not found.", id);
                        throw new ResourceNotFoundException("Product not found for id " + id);
                    });

            return converter.toProductDTO(product);
        } catch (Exception e) {
            if (e instanceof ResourceNotFoundException) throw e;

            logger.error("getProductById({}): Couldn't retrieve product", id, e);
            throw new ServiceException("Couldn't retrieve product", e);
        }
    }

    @Override
    public ProductDTO createProduct(ProductDTO newProductDTO) {
        try {
            var newProduct = converter.toProduct(newProductDTO);
            return converter.toProductDTO(productRepository.save(newProduct));
        } catch (Exception e) {
            logger.error("createProduct({}): Couldn't create Product", newProductDTO, e);
            throw new ServiceException("Couldn't create Product", e);
        }
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO updatedProductDTO) {
        try {
            var updated = converter.toProduct(updatedProductDTO);
            productRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("updateProduct({}, {}): Product not found.", id, updatedProductDTO);
                        return new ResourceNotFoundException("Product not found for id " + id);
                    });

            return converter.toProductDTO(productRepository.save(updated));
        } catch (Exception e) {
            if (e instanceof ResourceNotFoundException) throw e;

            logger.error("updateProduct({}, {}): Couldn't update product", id, updatedProductDTO, e);
            throw new ServiceException("Couldn't update product", e);
        }
    }

    /**
     * Verification mode will prepend the specified failure message if verification fails with the given implementation.
     * @param mode Implementation used for verification
     * @param description The custom failure message
     * @return VerificationMode
     */
    @Override
    public void deleteProductById(Long id) {
        try {
            var productToDelete = productRepository
                    .findById(id)
                    .orElseThrow(() -> {
                        logger.error("deleteProductById({}): Product not found.", id);
                        return new ResourceNotFoundException("Product not found for id " + id);
                    });
            productRepository.delete(productToDelete);
        } catch (Exception e) {
            if (e instanceof ResourceNotFoundException) throw e;

            logger.error("deleteProductById({}): couldn't delete product", id, e);
            throw new ServiceException("Couldn't delete product", e);
        }
    }
}

