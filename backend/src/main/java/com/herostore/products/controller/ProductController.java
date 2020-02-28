package com.herostore.products.controller;

import com.herostore.products.constants.FileType;
import com.herostore.products.constants.ProductStockCondition;
import com.herostore.products.dto.request.ProductRequest;
import com.herostore.products.dto.response.ProductResponse;
import com.herostore.products.exception.ServiceException;
import com.herostore.products.service.ProductService;
import com.herostore.products.utils.FileUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static com.herostore.products.utils.HttpUtils.adaptHttpResponseForFileDownload;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping(
        value = "/api/products",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ProductController {

    ProductService productService;

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    static String FILE_NAME = "products";

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts(
            @RequestParam(name = "stock", defaultValue = "ALL") ProductStockCondition stockCondition) {
        return productService.getAllProducts(stockCondition);
    }

    @GetMapping(value = "/export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void exportToFile(@RequestParam("format") FileType fileType,
                             @RequestParam("stock") ProductStockCondition stockCondition,
                             HttpServletResponse response) {
        var fileName = FileUtils.buildFileName(FILE_NAME, fileType);
        adaptHttpResponseForFileDownload(response, fileName);

        try {
            productService.exportProductsToFile(response.getOutputStream(), fileType, stockCondition);
        } catch (IOException e) {
            logger.error("Couldn't extract output stream from response", e);
            throw new ServiceException("Couldn't extract output stream from response", e);
        }
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@Valid @RequestBody ProductRequest newProduct) {
        return productService.createProduct(newProduct);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse updateProduct(
            @PathVariable Long id, @Valid @RequestBody ProductRequest updatedProduct) {
        return productService.updateProduct(id, updatedProduct);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductById(@PathVariable Long id) {
        productService.deleteProductById(id);
    }
}
