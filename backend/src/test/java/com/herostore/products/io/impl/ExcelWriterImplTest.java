package com.herostore.products.io.impl;

import com.herostore.products.exception.ServiceException;
import com.herostore.products.io.ExcelWriter;
import com.herostore.products.io.WorkbookData;
import com.herostore.products.mocks.TestClass;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@FieldDefaults(level = AccessLevel.PRIVATE)
class ExcelWriterImplTest {

    ExcelWriter excelWriter = new ExcelWriterImpl();

    @Test
    void shouldWriteSuccessfully() throws IOException {
        var os = new ByteArrayOutputStream();
        var columnWidths = new int[]{2000, 4000};
        var headers = new String[]{"ID", "Name"};
        var fields = new String[]{"id", "name"};
        var elements = asList(new TestClass(1L, "Elon"), new TestClass(2L, "Musk"));

        var workbookData = getWorkbookData(columnWidths, headers, fields);

        excelWriter.withData(workbookData);
        excelWriter.writeWorkbook(os, elements);
    }

    @Test
    void shouldThrowServiceExceptionWhenExtractingNonExistentMethod() throws IOException {
        var os = new ByteArrayOutputStream();
        var columnWidths = new int[]{2000};
        var headers = new String[]{"ID"};
        var fields = new String[]{"zz"};
        var elements = asList(new TestClass(1L, "Elon"), new TestClass(2L, "Musk"));
        var expectedError = "Couldn't extract method for field named zz";

        var workbookData = getWorkbookData(columnWidths, headers, fields);

        excelWriter.withData(workbookData);

        var exc = assertThrows(ServiceException.class,
                () -> excelWriter.writeWorkbook(os, elements));

        assertEquals(expectedError, exc.getMessage());
    }

    @Test
    void shouldThrowsServiceExceptionWhenInvokingPrivateMethod() throws IOException {
        var os = new ByteArrayOutputStream();
        var columnWidths = new int[]{2000, 4000};
        var headers = new String[]{"Invalid Number"};
        var fields = new String[]{"invalidNumber"};
        var elements = asList(new TestClass(1L, "Elon"), new TestClass(2L, "Musk"));
        var expectedError = "Couldn't invoke method getInvalidNumber";

        var workbookData = getWorkbookData(columnWidths, headers, fields);

        excelWriter.withData(workbookData);

        var exc = assertThrows(ServiceException.class,
                () -> excelWriter.writeWorkbook(os, elements));

        assertEquals(expectedError, exc.getMessage());
    }

    private WorkbookData getWorkbookData(int[] columnWidths, String[] headers, String[] fields) {
        return WorkbookData.builder()
                .sheetName("Sheet Name")
                .columnWidths(columnWidths)
                .headers(headers)
                .fields(fields)
                .build();
    }
}