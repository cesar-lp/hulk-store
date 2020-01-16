package com.todo.hulkstore.service;

import com.todo.hulkstore.dto.ProductTypeDTO;

import java.util.List;

public interface ProductTypeService {

    List<ProductTypeDTO> getAllProductTypes();

    ProductTypeDTO getProductTypeById(Long id);

    ProductTypeDTO createProductType(ProductTypeDTO newProductTypeDTO);

    ProductTypeDTO updateProductType(Long id, ProductTypeDTO updatedProductTypeDTO);

    void deleteProductTypeById(Long id);
}
