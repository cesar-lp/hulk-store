package com.todo1.hulkstore.service.impl;

import com.todo1.hulkstore.converter.ProductConverter;
import com.todo1.hulkstore.domain.ProductType;
import com.todo1.hulkstore.dto.ProductTypeDTO;
import com.todo1.hulkstore.exception.ResourceNotFoundException;
import com.todo1.hulkstore.repository.ProductTypeRepository;
import com.todo1.hulkstore.service.ProductTypeService;
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
        return productConverter.toProductTypeDTOList(productTypeRepository.findAll());
    }

    @Override
    public ProductTypeDTO getProductTypeById(Long id) {
        var productType = productTypeRepository
                .findById(id)
                .orElseThrow(() -> {
                    logger.error("getProductTypeById: Product type not found for id {}.", id);
                    return new ResourceNotFoundException("Product type not found for id " + id);
                });

        return productConverter.toProductTypeDTO(productType);
    }

    @Override
    public ProductTypeDTO createProductType(ProductTypeDTO newProductTypeDTO) {
        var productType = productConverter.toProductType(newProductTypeDTO);
        return productConverter
                .toProductTypeDTO(productTypeRepository.save(productType));
    }

    @Override
    public ProductTypeDTO updateProductType(Long id, ProductTypeDTO updatedProductTypeDTO) {
        var updatedProductType = productConverter.toProductType(updatedProductTypeDTO);
        var existingProduct = productTypeRepository
                .findById(id)
                .orElseThrow(() -> {
                    logger.error("updateProductType: Product type not found for id {}.", id);
                    return new ResourceNotFoundException("ProductType not found for id " + id);
                });

        updateProductTypeDetails(existingProduct, updatedProductType);

        return productConverter.toProductTypeDTO(productTypeRepository.save(existingProduct));
    }

    @Override
    public void deleteProductTypeById(Long id) {
        var productTypeToDelete = productTypeRepository
                .findById(id)
                .orElseThrow(() -> {
                    logger.error("deleteProductTypeById: Product type not found for id {}.", id);
                    return new ResourceNotFoundException("ProductType not found for id " + id);
                });

        productTypeRepository.delete(productTypeToDelete);
    }

    private void updateProductTypeDetails(ProductType old, ProductType updated) {
        old.setName(updated.getName());
    }
}
