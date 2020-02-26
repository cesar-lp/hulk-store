package com.herostore.products.service;

import com.herostore.products.constants.FileType;
import com.herostore.products.dto.ProductTypeDTO;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface ProductTypeService {

    List<ProductTypeDTO> getAllProductTypes();

    void exportProductTypesToFile(OutputStream os, FileType fileType) throws IOException;

    ProductTypeDTO getProductTypeById(Long id);

    ProductTypeDTO createProductType(ProductTypeDTO newProductTypeDTO);

    ProductTypeDTO updateProductType(Long id, ProductTypeDTO updatedProductTypeDTO);

    void deleteProductTypeById(Long id);
}
