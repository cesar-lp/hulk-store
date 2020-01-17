package com.todo1.hulkstore.service.impl;

import com.todo1.hulkstore.converter.ProductConverter;
import com.todo1.hulkstore.domain.ProductType;
import com.todo1.hulkstore.dto.ProductTypeDTO;
import com.todo1.hulkstore.repository.ProductTypeRepository;
import com.todo1.hulkstore.service.ProductTypeService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductTypeServiceImpl implements ProductTypeService {

    ProductTypeRepository productTypeRepository;
    ProductConverter productConverter;

    @Override
    public List<ProductTypeDTO> getAllProductTypes() {
        return productConverter.toProductTypeDTOList(productTypeRepository.findAll());
    }

    @Override
    public ProductTypeDTO getProductTypeById(Long id) {
        return productTypeRepository
                .findById(id)
                .map(productConverter::toProductTypeDTO)
                .orElse(null);
    }

    @Override
    public ProductTypeDTO createProductType(ProductTypeDTO newProductTypeDTO) {
        ProductType productType = productConverter.toProductType(newProductTypeDTO);
        return productConverter
                .toProductTypeDTO(productTypeRepository.save(productType));
    }

    @Override
    public ProductTypeDTO updateProductType(Long id, ProductTypeDTO updatedProductTypeDTO) {
        ProductType updatedProductType = productConverter.toProductType(updatedProductTypeDTO);
        return productTypeRepository
                .findById(id)
                .flatMap(old -> updateProductTypeDetails(old, updatedProductType))
                .map(updated -> productConverter.toProductTypeDTO(productTypeRepository.save(updated)))
                .orElse(null);
    }

    @Override
    public void deleteProductTypeById(Long id) {
        productTypeRepository.deleteById(id);
    }

    private Optional<ProductType> updateProductTypeDetails(ProductType old, ProductType updated) {
        old.setName(updated.getName());
        return Optional.of(old);
    }
}
