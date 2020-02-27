package com.herostore.products.service;

import com.herostore.products.constants.FileType;
import com.herostore.products.dto.ProductTypeDTO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface ProductTypeService {

    List<ProductTypeDTO> getAllProductTypes();

    void exportToFile(HttpServletResponse response, FileType fileType) throws IOException;

    ProductTypeDTO getProductTypeById(Long id);

    ProductTypeDTO createProductType(ProductTypeDTO newProductTypeDTO);

    ProductTypeDTO updateProductType(Long id, ProductTypeDTO updatedProductTypeDTO);

    void deleteProductTypeById(Long id);
}
