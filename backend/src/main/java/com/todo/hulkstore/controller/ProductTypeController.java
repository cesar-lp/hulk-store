package com.todo.hulkstore.controller;

import com.todo.hulkstore.constants.FileType;
import com.todo.hulkstore.dto.ProductTypeDTO;
import com.todo.hulkstore.service.ProductTypeService;
import com.todo.hulkstore.utils.FileUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping(
        value = "/api/product-types",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ProductTypeController {

    ProductTypeService productTypeService;

    static String FILE_NAME = "product_types";

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductTypeDTO> getAllProductTypes() {
        return productTypeService.getAllProductTypes();
    }

    @GetMapping(value = "/export")
    @ResponseStatus(HttpStatus.OK)
    public void exportToFile(@RequestParam("format") FileType fileType, HttpServletResponse response) throws IOException {
        productTypeService.exportToFile(response.getWriter(), fileType);
        FileUtils.adaptHttpResponse(response, FILE_NAME, fileType);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductTypeDTO getProductTypeById(@PathVariable Long id) {
        return productTypeService.getProductTypeById(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductTypeDTO createProductType(@Valid @RequestBody ProductTypeDTO newProductTypeDTO) {
        return productTypeService.createProductType(newProductTypeDTO);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductTypeDTO updateProductType(
            @Valid @RequestBody ProductTypeDTO updatedProductTypeDTO, @PathVariable Long id) {
        return productTypeService.updateProductType(id, updatedProductTypeDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductTypeById(@PathVariable Long id) {
        productTypeService.deleteProductTypeById(id);
    }
}
