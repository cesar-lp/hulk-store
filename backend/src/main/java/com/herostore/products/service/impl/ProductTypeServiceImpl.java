package com.herostore.products.service.impl;

import com.herostore.products.exception.ResourceNotFoundException;
import com.herostore.products.exception.ServiceException;
import com.herostore.products.io.CSVHandler;
import com.herostore.products.repository.ProductTypeRepository;
import com.herostore.products.service.ProductTypeService;
import com.herostore.products.constants.FileType;
import com.herostore.products.dto.ProductTypeDTO;
import com.herostore.products.mapper.ProductTypeMapper;
import com.herostore.products.io.ExcelHandler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductTypeServiceImpl implements ProductTypeService {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    ProductTypeRepository productTypeRepository;
    ProductTypeMapper productTypeMapper;

    /**
     * Retrieves all existing product types.
     *
     * @return the product types found.
     */
    @Override
    public List<ProductTypeDTO> getAllProductTypes() {
        try {
            return productTypeMapper.toProductTypeDTOList(productTypeRepository.findAll());
        } catch (Exception e) {
            logger.error("getAllProductTypes()", e);
            throw new ServiceException("Couldn't retrieve all product types", e);
        }
    }

    @Override
    public void exportToFile(HttpServletResponse response, FileType fileType) {
        var productTypes = getAllProductTypes();

        try {
            switch (fileType) {
                case CSV:
                    exportToCSV(response.getWriter(), productTypes);
                    break;
                case EXCEL:
                    exportToExcel(response.getOutputStream(), productTypes);
                    break;
                default:
                    throw new IllegalArgumentException("Format type " + fileType.name() + " not valid");
            }
        } catch (IOException ioExc) {
            logger.error("exportToFile({})", fileType.name(), ioExc);
            throw new ServiceException("Couldn't export product types to " + fileType.name() + " format", ioExc);
        }
    }

    /**
     * Retrieves a product type for a given id.
     *
     * @param id product type id.
     * @return the product type found.
     */
    @Override
    public ProductTypeDTO getProductTypeById(Long id) {
        try {
            var productType = productTypeRepository
                    .findById(id)
                    .orElseThrow(() -> {
                        throw new ResourceNotFoundException(getProductTypeNotFoundMessage(id));
                    });

            return productTypeMapper.toProductTypeDTO(productType);
        } catch (ResourceNotFoundException rnfExc) {
            logger.error(rnfExc.getMessage());
            throw rnfExc;
        } catch (Exception e) {
            logger.error("getProductTypeById({}): ", id, e);
            throw new ServiceException("Couldn't retrieve product type", e);
        }
    }

    /**
     * Creates a product type.
     *
     * @param newProductType product type to create.
     * @return the created product type.
     */
    @Override
    public ProductTypeDTO createProductType(ProductTypeDTO newProductType) {
        try {
            var productType = productTypeMapper.toProductType(newProductType);
            return productTypeMapper
                    .toProductTypeDTO(productTypeRepository.save(productType));
        } catch (Exception e) {
            logger.error("createProductType({}): ", newProductType, e);
            throw new ServiceException("Couldn't create product type", e);
        }
    }

    /**
     * Updates a product type.
     *
     * @param id                  product type id
     * @param productTypeToUpdate product type to update.
     * @return the updated product type.
     */
    @Override
    public ProductTypeDTO updateProductType(Long id, ProductTypeDTO productTypeToUpdate) {
        try {
            var updatedProductType = productTypeMapper.toProductType(productTypeToUpdate);
            var existingProductType = productTypeRepository
                    .findById(id)
                    .orElseThrow(() -> {
                        throw new ResourceNotFoundException(getProductTypeNotFoundMessage(id));
                    });

            existingProductType.updateName(updatedProductType.getName());

            return productTypeMapper.toProductTypeDTO(productTypeRepository.save(existingProductType));
        } catch (ResourceNotFoundException rnfExc) {
            logger.error(rnfExc.getMessage());
            throw rnfExc;
        } catch (Exception e) {
            logger.error("updateProductType({}, {}): ", id, productTypeToUpdate, e);
            throw new ServiceException("Couldn't update product type", e);
        }
    }

    /**
     * Deletes a product type for a given id.
     *
     * @param id product type id.
     */
    @Override
    public void deleteProductTypeById(Long id) {
        try {
            var productTypeToDelete = productTypeRepository
                    .findById(id)
                    .orElseThrow(() -> {
                        throw new ResourceNotFoundException(getProductTypeNotFoundMessage(id));
                    });

            productTypeRepository.delete(productTypeToDelete);
        } catch (ResourceNotFoundException rfnExc) {
            logger.error(rfnExc.getMessage());
            throw rfnExc;
        } catch (Exception e) {
            logger.error("deleteProductTypeById({}): ", id, e);
            throw new ServiceException("Couldn't delete product type", e);
        }
    }

    private void exportToCSV(PrintWriter writer, List<ProductTypeDTO> productTypes) throws IOException {
        var headers = new String[]{"ID", "Name"};

        try (var printer = CSVHandler.getPrinter(writer, headers)) {
            for (ProductTypeDTO productType : productTypes) {
                printer.printRecord(productType.getId(), productType.getName());
            }
        }
    }

    private void exportToExcel(OutputStream outputStream, List<ProductTypeDTO> productTypes) throws IOException {
        var columnWidths = new int[]{2000, 7000};
        var headers = new String[]{"ID", "Name"};
        var fields = new String[]{"id", "name"};

        var excelHandler = ExcelHandler.workbookBuilder()
                .sheetName("Product Types")
                .columnWidths(columnWidths)
                .headers(headers)
                .fields(fields)
                .build();

        excelHandler.writeWorkbook(productTypes, outputStream);
    }

    private String getProductTypeNotFoundMessage(Long id) {
        return "Product type not found for id " + id;
    }
}
