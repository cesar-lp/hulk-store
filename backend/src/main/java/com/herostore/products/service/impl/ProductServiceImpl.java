package com.herostore.products.service.impl;

import com.herostore.products.constants.FileType;
import com.herostore.products.constants.ProductStockCondition;
import com.herostore.products.domain.Product;
import com.herostore.products.domain.ProductType;
import com.herostore.products.dto.request.ProductRequest;
import com.herostore.products.dto.response.ProductResponse;
import com.herostore.products.exception.ResourceNotFoundException;
import com.herostore.products.exception.ServiceException;
import com.herostore.products.io.CSVWriter;
import com.herostore.products.io.ExcelWriter;
import com.herostore.products.io.WorkbookData;
import com.herostore.products.mapper.ProductMapper;
import com.herostore.products.mapper.ProductTypeMapper;
import com.herostore.products.repository.ProductRepository;
import com.herostore.products.service.ProductService;
import com.herostore.products.service.ProductTypeService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductServiceImpl implements ProductService {

    Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class.getName());

    ProductRepository productRepository;
    ProductTypeService productTypeService;
    ProductMapper productMapper;
    ProductTypeMapper productTypeMapper;
    CSVWriter csvWriter;
    ExcelWriter excelWriter;

    /**
     * Retrieves all existing products.
     *
     * @param stockCondition whether all, available or unavailable products should be retrieved
     * @return all products found based on stock condition.
     */
    @Override
    public List<ProductResponse> getAllProducts(ProductStockCondition stockCondition) {
        try {
            if (stockCondition.equals(ProductStockCondition.ALL)) {
                return productMapper.toProductResponseList(productRepository.findAll());
            }

            var inStock = stockCondition.equals(ProductStockCondition.AVAILABLE);

            return productMapper.toProductResponseList(
                    productRepository.retrieveProductsByStockCondition(inStock));
        } catch (Exception e) {
            logger.error("getAllProducts(): Couldn't retrieve all products.", e);
            throw new ServiceException("Couldn't retrieve all products", e);
        }
    }

    /**
     * Exports products to a file.
     *
     * @param os             output stream to which the file will be written.
     * @param fileType       file type (PDF, Excel, CSV) to export.
     * @param stockCondition product stock condition filter
     */
    @Override
    public void exportProductsToFile(OutputStream os, FileType fileType, ProductStockCondition stockCondition) {
        var products = getAllProducts(stockCondition);

        try {
            switch (fileType) {
                case CSV:
                    exportToCSV(os, products);
                    break;
                case EXCEL:
                    exportToExcel(os, products);
                    break;
                default:
                    throw new IllegalArgumentException("Format type " + fileType.name() + " not valid");
            }
        } catch (IOException ioExc) {
            logger.error("exportToFile(CSV)", ioExc);
            throw new ServiceException("Couldn't export products to " + fileType.name() + " format", ioExc);
        }
    }

    /**
     * Retrieves a product by a given id.
     *
     * @param id product's id.
     * @return the product found.
     */
    @Override
    public ProductResponse getProductById(Long id) {
        try {
            var product = productRepository
                    .findById(id)
                    .orElseThrow(() -> {
                        throw new ResourceNotFoundException("Product not found for id " + id);
                    });

            return productMapper.toProductResponse(product);
        } catch (ResourceNotFoundException rnfExc) {
            logger.error(rnfExc.getMessage());
            throw rnfExc;
        } catch (Exception e) {
            logger.error("getProductById({}): Couldn't retrieve product", id, e);
            throw new ServiceException("Couldn't retrieve product", e);
        }
    }

    /**
     * Creates a product.
     *
     * @param productRequest product to create.
     * @return the created product.
     */
    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        try {
            var productType = getProductTypeById(productRequest.getProductTypeId());
            var newProduct = productMapper.toProduct(productRequest, productType);

            return productMapper.toProductResponse(productRepository.save(newProduct));
        } catch (Exception e) {
            logger.error("createProduct({}): Couldn't create Product", productRequest, e);
            throw new ServiceException("Couldn't create Product", e);
        }
    }

    /**
     * Updates a product
     *
     * @param id                product's id
     * @param updatedProductReq product to update
     * @return the updated product.
     */
    @Override
    public ProductResponse updateProduct(Long id, ProductRequest updatedProductReq) {
        try {
            var productType = getProductTypeById(updatedProductReq.getProductTypeId());
            var updatedProduct = productMapper.toProduct(updatedProductReq, productType);

            var product = productRepository.findById(id)
                    .orElseThrow(() -> {
                        throw new ResourceNotFoundException("Product not found for id " + id);
                    });

            updatedProduct = updateProductDetails(product, updatedProduct);
            return productMapper.toProductResponse(productRepository.save(updatedProduct));
        } catch (ResourceNotFoundException rnfExc) {
            logger.error(rnfExc.getMessage());
            throw rnfExc;
        } catch (Exception e) {
            logger.error("updateProduct({}, {}): Couldn't update product", id, updatedProductReq, e);
            throw new ServiceException("Couldn't update product", e);
        }
    }

    /**
     * Deletes a product by a given id.
     *
     * @param id product's id.
     */
    @Override
    public void deleteProductById(Long id) {
        try {
            var productToDelete = productRepository
                    .findById(id)
                    .orElseThrow(() -> {
                        throw new ResourceNotFoundException("Product not found for id " + id);
                    });
            productRepository.delete(productToDelete);
        } catch (ResourceNotFoundException rnfExc) {
            logger.error(rnfExc.getMessage());
            throw rnfExc;
        } catch (Exception e) {
            logger.error("deleteProductById({}): couldn't delete product", id, e);
            throw new ServiceException("Couldn't delete product", e);
        }
    }

    private void exportToCSV(OutputStream os, List<ProductResponse> products) throws IOException {
        var headers = new String[]{"ID", "Name", "Product Type", "Price", "Stock"};
        var fieldNames = new String[]{"id", "name", "productTypeName", "price", "stock"};

        csvWriter.write(os, headers, fieldNames, products);
    }

    private void exportToExcel(OutputStream outputStream, List<ProductResponse> products) throws IOException {
        var columnWidths = new int[]{2000, 7000, 7000, 3000, 2000};
        var headers = new String[]{"ID", "Name", "Product type", "Price", "Stock"};
        var fields = new String[]{"id", "name", "productTypeName", "price", "stock"};

        var workbookData = WorkbookData.builder()
                .sheetName("Products")
                .columnWidths(columnWidths)
                .headers(headers)
                .fields(fields)
                .build();

        excelWriter.withData(workbookData);
        excelWriter.writeWorkbook(outputStream, products);
    }

    private Product updateProductDetails(Product old, Product updated) {
        return Product.builder()
                .id(old.getId())
                .name(updated.getName())
                .productType(updated.getProductType())
                .price(updated.getPrice())
                .stock(updated.getStock())
                .build();
    }

    private ProductType getProductTypeById(Long id) {
        return productTypeMapper.toProductType(productTypeService.getProductTypeById(id));
    }
}
