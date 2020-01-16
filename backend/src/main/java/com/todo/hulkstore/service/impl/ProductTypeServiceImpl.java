package com.todo.hulkstore.service.impl;

import com.todo.hulkstore.converter.ProductConverter;
import com.todo.hulkstore.dto.ProductTypeDTO;
import com.todo.hulkstore.exception.ResourceNotFoundException;
import com.todo.hulkstore.exception.ServiceException;
import com.todo.hulkstore.repository.ProductTypeRepository;
import com.todo.hulkstore.service.ProductTypeService;
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

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    ProductTypeRepository productTypeRepository;
    ProductConverter productConverter;

    /**
     * Retrieves all existing product types.
     *
     * @return the product types found.
     */
    @Override
    public List<ProductTypeDTO> getAllProductTypes() {
        try {
            return productConverter.toProductTypeDTOList(productTypeRepository.findAll());
        } catch (Exception e) {
            logger.error("getAllProductTypes()", e);
            throw new ServiceException("Couldn't retrieve all product types", e);
        }
    }

    /**
     * Retrieves a product type for a given id.
     *
     * @param id product type id.
     * @return the product type found.
     */
    @Override
    public ProductTypeDTO getProductTypeById(Long id) {
        try {
            var productType = productTypeRepository
                    .findById(id)
                    .orElseThrow(() -> {
                        throw new ResourceNotFoundException("Product type not found for id " + id);
                    });

            return productConverter.toProductTypeDTO(productType);
        } catch (ResourceNotFoundException rnfExc) {
            logger.error(rnfExc.getMessage());
            throw rnfExc;
        } catch (Exception e) {
            logger.error("getProductTypeById({}): couldn't retrieve product type", id, e);
            throw new ServiceException("Couldn't retrieve product type", e);
        }
    }

    /**
     * Creates a product type.
     *
     * @param newProductType product type to create.
     * @return the created product type.
     */
    @Override
    public ProductTypeDTO createProductType(ProductTypeDTO newProductType) {
        try {
            var productType = productConverter.toProductType(newProductType);
            return productConverter
                    .toProductTypeDTO(productTypeRepository.save(productType));
        } catch (Exception e) {
            logger.error("createProductType({}): couldn't create product type", newProductType, e);
            throw new ServiceException("Couldn't create product type", e);
        }
    }

    /**
     * Updates a product type.
     *
     * @param id                  product type id
     * @param productTypeToUpdate product type to update.
     * @return the updated product type.
     */
    @Override
    public ProductTypeDTO updateProductType(Long id, ProductTypeDTO productTypeToUpdate) {
        try {
            var updatedProductType = productConverter.toProductType(productTypeToUpdate);
            var existingProductType = productTypeRepository
                    .findById(id)
                    .orElseThrow(() -> {
                        throw new ResourceNotFoundException("Product type not found for id " + id);
                    });

            existingProductType.setName(updatedProductType.getName());

            return productConverter.toProductTypeDTO(productTypeRepository.save(existingProductType));
        } catch (ResourceNotFoundException rnfExc) {
            logger.error(rnfExc.getMessage());
            throw rnfExc;
        } catch (Exception e) {
            logger.error("updateProductType({}, {}): couldn't update product type", id, productTypeToUpdate, e);
            throw new ServiceException("Couldn't update product type", e);
        }
    }

    /**
     * Deletes a product type for a given id.
     *
     * @param id product type id.
     */
    @Override
    public void deleteProductTypeById(Long id) {
        try {
            var productTypeToDelete = productTypeRepository
                    .findById(id)
                    .orElseThrow(() -> {
                        throw new ResourceNotFoundException("Product type not found for id " + id);
                    });

            productTypeRepository.delete(productTypeToDelete);
        } catch (ResourceNotFoundException rfnExc) {
            logger.error(rfnExc.getMessage());
            throw rfnExc;
        } catch (Exception e) {
            logger.error("deleteProductTypeById({}): couldn't delete product type", id, e);
            throw new ServiceException("Couldn't delete product type", e);
        }
    }
}
