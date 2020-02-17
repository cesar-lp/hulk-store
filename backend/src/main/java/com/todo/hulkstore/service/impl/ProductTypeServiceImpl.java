package com.todo.hulkstore.service.impl;

import com.todo.hulkstore.dto.ProductTypeDTO;
import com.todo.hulkstore.exception.ResourceNotFoundException;
import com.todo.hulkstore.exception.ServiceException;
import com.todo.hulkstore.mapper.ProductTypeMapper;
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
    ProductTypeMapper productTypeMapper;

    /**
     * Retrieves all existing product types.
     *
     * @return the product types found.
     */
    @Override
    public List<ProductTypeDTO> getAllProductTypes() {
        try {
            return productTypeMapper.toProductTypeDTOList(productTypeRepository.findAll());
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
                        throw new ResourceNotFoundException(getProductTypeNotFoundMessage(id));
                    });

            return productTypeMapper.toProductTypeDTO(productType);
        } catch (ResourceNotFoundException rnfExc) {
            logger.error(rnfExc.getMessage());
            throw rnfExc;
        } catch (Exception e) {
            logger.error("getProductTypeById({}): ", id, e);
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
            var productType = productTypeMapper.toProductType(newProductType);
            return productTypeMapper
                    .toProductTypeDTO(productTypeRepository.save(productType));
        } catch (Exception e) {
            logger.error("createProductType({}): ", newProductType, e);
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
            var updatedProductType = productTypeMapper.toProductType(productTypeToUpdate);
            var existingProductType = productTypeRepository
                    .findById(id)
                    .orElseThrow(() -> {
                        throw new ResourceNotFoundException(getProductTypeNotFoundMessage(id));
                    });

            existingProductType.updateName(updatedProductType.getName());

            return productTypeMapper.toProductTypeDTO(productTypeRepository.save(existingProductType));
        } catch (ResourceNotFoundException rnfExc) {
            logger.error(rnfExc.getMessage());
            throw rnfExc;
        } catch (Exception e) {
            logger.error("updateProductType({}, {}): ", id, productTypeToUpdate, e);
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
                        throw new ResourceNotFoundException(getProductTypeNotFoundMessage(id));
                    });

            productTypeRepository.delete(productTypeToDelete);
        } catch (ResourceNotFoundException rfnExc) {
            logger.error(rfnExc.getMessage());
            throw rfnExc;
        } catch (Exception e) {
            logger.error("deleteProductTypeById({}): ", id, e);
            throw new ServiceException("Couldn't delete product type", e);
        }
    }

    private String getProductTypeNotFoundMessage(Long id) {
        return "Product type not found for id " + id;
    }
}
