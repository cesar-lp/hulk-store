package com.todo.hulkstore.service.impl;

import com.todo.hulkstore.service.ProductTypeService;
import com.todo.hulkstore.converter.ProductConverter;
import com.todo.hulkstore.domain.ProductType;
import com.todo.hulkstore.dto.ProductTypeDTO;
import com.todo.hulkstore.exception.ResourceNotFoundException;
import com.todo.hulkstore.exception.ServiceException;
import com.todo.hulkstore.repository.ProductTypeRepository;
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
public class ProductTypeServiceImpl implements ProductTypeService {

    Logger logger = LoggerFactory.getLogger(ProductTypeServiceImpl.class.getName());

    ProductTypeRepository productTypeRepository;
    ProductConverter productConverter;

    @Override
    public List<ProductTypeDTO> getAllProductTypes() {
        try {
            return productConverter.toProductTypeDTOList(productTypeRepository.findAll());
        } catch (Exception e) {
            logger.error("getAllProductTypes()", e);
            throw new ServiceException("Couldn't retrieve all product types", e);
        }
    }

    @Override
    public ProductTypeDTO getProductTypeById(Long id) {
        try {
            var productType = productTypeRepository
                    .findById(id)
                    .orElseThrow(() -> {
                        logger.error("getProductTypeById({}): Product type not found.", id);
                        throw new ResourceNotFoundException("Product type not found for id " + id);
                    });

            return productConverter.toProductTypeDTO(productType);
        } catch (Exception e) {
            if (e instanceof ResourceNotFoundException) throw e;

            logger.error("getProductTypeById({}): couldn't retrieve product type", id, e);
            throw new ServiceException("Couldn't retrieve product type", e);
        }
    }

    @Override
    public ProductTypeDTO createProductType(ProductTypeDTO newProductTypeDTO) {
        try {
            var productType = productConverter.toProductType(newProductTypeDTO);
            return productConverter
                    .toProductTypeDTO(productTypeRepository.save(productType));
        } catch (Exception e) {
            logger.error("createProductType({}): couldn't create product type", newProductTypeDTO, e);
            throw new ServiceException("Couldn't create product type", e);
        }
    }

    @Override
    public ProductTypeDTO updateProductType(Long id, ProductTypeDTO updatedProductTypeDTO) {
        try {
            var updatedProductType = productConverter.toProductType(updatedProductTypeDTO);
            var existingProduct = productTypeRepository
                    .findById(id)
                    .orElseThrow(() -> {
                        logger.error("updateProductType({}, {}): Product type not found.", id, updatedProductTypeDTO);
                        throw new ResourceNotFoundException("Product type not found for id " + id);
                    });

            updateProductTypeDetails(existingProduct, updatedProductType);

            return productConverter.toProductTypeDTO(productTypeRepository.save(existingProduct));
        } catch (Exception e) {
            if (e instanceof ResourceNotFoundException) throw e;

            logger.error("updateProductType({}, {}): couldn't update product type", id, updatedProductTypeDTO, e);
            throw new ServiceException("Couldn't update product type", e);
        }
    }

    @Override
    public void deleteProductTypeById(Long id) {
        try {
            var productTypeToDelete = productTypeRepository
                    .findById(id)
                    .orElseThrow(() -> {
                        logger.error("deleteProductTypeById({}): Product type not found.", id);
                        throw new ResourceNotFoundException("Product type not found for id " + id);
                    });

            productTypeRepository.delete(productTypeToDelete);
        } catch (Exception e) {
            if (e instanceof ResourceNotFoundException) throw e;

            logger.error("deleteProductTypeById({}): couldn't delete product type", id, e);
            throw new ServiceException("Couldn't delete product type", e);
        }
    }

    private void updateProductTypeDetails(ProductType old, ProductType updated) {
        old.setName(updated.getName());
    }
}
