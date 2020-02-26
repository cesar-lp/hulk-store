package com.herostore.products.io.impl;

import com.herostore.products.io.ExcelWriter;
import com.herostore.products.io.WorkbookData;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;

import static com.herostore.products.utils.ClassUtils.extractMethod;
import static com.herostore.products.utils.ClassUtils.invokeMethod;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExcelWriterImpl implements ExcelWriter {

    WorkbookData workbookData;

    public ExcelWriter withData(WorkbookData workbookData) {
        this.workbookData = workbookData;
        return new ExcelWriterImpl();
    }

    @Override
    public <T> void writeWorkbook(OutputStream os, List<T> elements) throws IOException {
        try (var workbook = new XSSFWorkbook()) {
            var sheet = workbook.createSheet(workbookData.getSheetName());

            defineColumns(sheet);

            var header = createHeader(sheet);
            var headerStyle = createHeaderStyle(workbook);
            var headerFont = createHeaderFont(workbook);
            headerStyle.setFont(headerFont);

            addHeaders(header, headerStyle);

            writeData(workbook, sheet, elements);

            workbook.write(os);
        }
    }

    private void defineColumns(XSSFSheet sheet) {
        for (var i = 0; i < workbookData.getColumnWidths().length; i++) {
            sheet.setColumnWidth(i, workbookData.getColumnWidths()[i]);
        }
    }

    private XSSFRow createHeader(XSSFSheet sheet) {
        return sheet.createRow(1);
    }

    private XSSFCellStyle createHeaderStyle(XSSFWorkbook workbook) {
        var headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return headerStyle;
    }

    private XSSFFont createHeaderFont(XSSFWorkbook workbook) {
        var font = workbook.createFont();
        font.setFontName(workbookData.getHeaderFontName());
        font.setFontHeightInPoints((short) workbookData.getHeaderFontHeight());
        font.setBold(workbookData.isBoldHeaderFont());
        return font;
    }

    private void addHeaders(XSSFRow header, XSSFCellStyle headerStyle) {
        for (var i = 0; i < workbookData.getHeaders().length; i++) {
            var headerCell = header.createCell(i);
            headerCell.setCellValue(workbookData.getHeaders()[i]);
            headerCell.setCellStyle(headerStyle);
        }
    }

    private <T> void writeData(XSSFWorkbook workbook, XSSFSheet sheet, List<T> data) {
        var style = workbook.createCellStyle();
        style.setWrapText(true);

        var clazz = data.get(0).getClass();

        var rowCounter = 2;

        for (var item : data) {
            var columnCounter = 0;
            var row = sheet.createRow(rowCounter);

            for (var fieldName : workbookData.getFields()) {
                var cell = row.createCell(columnCounter);
                cell.setCellStyle(style);

                var method = extractMethod(clazz, fieldName);
                var value = invokeMethod(method, item);
                writeValueToCell(cell, value);

                columnCounter++;
            }

            rowCounter++;
        }
    }

    private void writeValueToCell(XSSFCell cell, Object value) {
        if (value != null) {
            if (value instanceof String) {
                cell.setCellValue((String) value);
            } else if (value instanceof Long) {
                cell.setCellValue((Long) value);
            } else if (value instanceof Integer) {
                cell.setCellValue((Integer) value);
            } else if (value instanceof Double) {
                cell.setCellValue((Double) value);
            } else if (value instanceof BigDecimal) {
                cell.setCellValue(((BigDecimal) value).doubleValue());
            }
        }
    }
}
