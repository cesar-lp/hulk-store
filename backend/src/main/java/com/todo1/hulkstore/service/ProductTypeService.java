package com.todo1.hulkstore.service;

import com.todo1.hulkstore.dto.ProductTypeDTO;

import java.util.List;

public interface ProductTypeService {

    List<ProductTypeDTO> getAllProductTypes();

    ProductTypeDTO getProductTypeById(Long id);

    ProductTypeDTO createProductType(ProductTypeDTO newProductTypeDTO);

    ProductTypeDTO updateProductType(Long id, ProductTypeDTO updatedProductTypeDTO);

    void deleteProductTypeById(Long id);
}
