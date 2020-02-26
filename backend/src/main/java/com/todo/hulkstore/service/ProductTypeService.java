package com.todo.hulkstore.service;

import com.todo.hulkstore.constants.FileType;
import com.todo.hulkstore.dto.ProductTypeDTO;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public interface ProductTypeService {

    List<ProductTypeDTO> getAllProductTypes();

    void exportToFile(PrintWriter writer, FileType fileType) throws IOException;

    ProductTypeDTO getProductTypeById(Long id);

    ProductTypeDTO createProductType(ProductTypeDTO newProductTypeDTO);

    ProductTypeDTO updateProductType(Long id, ProductTypeDTO updatedProductTypeDTO);

    void deleteProductTypeById(Long id);
}
