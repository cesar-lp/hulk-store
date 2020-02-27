package com.herostore.products.io;

import com.herostore.products.exception.ServiceException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

import static org.springframework.util.StringUtils.capitalize;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExcelHandler {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    String sheetName;
    int[] columnWidths;
    String[] headers;
    String[] fields;
    boolean boldHeaderFont;
    int headerFontHeight;
    String headerFontName;

    public static WorkbookBuilder workbookBuilder() {
        return new WorkbookBuilder();
    }

    public static class WorkbookBuilder {

        private String sheetName;
        private int[] columnWidths;
        private String[] headers;
        private String[] fields;
        private boolean boldHeaderFont = true;
        private int headerFontHeight = 12;
        private String headerFontName = "Arial";

        public WorkbookBuilder sheetName(String sheetName) {
            this.sheetName = sheetName;
            return this;
        }

        public WorkbookBuilder columnWidths(int[] columnWidths) {
            this.columnWidths = columnWidths;
            return this;
        }

        public WorkbookBuilder headers(String[] headers) {
            this.headers = headers;
            return this;
        }

        public WorkbookBuilder fields(String[] fields) {
            this.fields = fields;
            return this;
        }

        public ExcelHandler build() {
            return new ExcelHandler(this);
        }
    }

    public ExcelHandler(WorkbookBuilder workbookBuilder) {
        sheetName = workbookBuilder.sheetName;
        columnWidths = workbookBuilder.columnWidths;
        headers = workbookBuilder.headers;
        fields = workbookBuilder.fields;
        boldHeaderFont = workbookBuilder.boldHeaderFont;
        headerFontHeight = workbookBuilder.headerFontHeight;
        headerFontName = workbookBuilder.headerFontName;
    }

    public <T> void writeWorkbook(List<T> data, OutputStream outputStream) throws IOException {
        try (var workbook = new XSSFWorkbook()) {
            var sheet = workbook.createSheet(sheetName);

            defineColumns(sheet);

            var header = createHeader(sheet);
            var headerStyle = createHeaderStyle(workbook);
            var headerFont = createHeaderFont(workbook);
            headerStyle.setFont(headerFont);

            addHeaders(header, headerStyle);

            writeData(workbook, sheet, data);

            workbook.write(outputStream);
        }
    }

    private void defineColumns(XSSFSheet sheet) {
        for (var i = 0; i < columnWidths.length; i++) {
            sheet.setColumnWidth(i, columnWidths[i]);
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
        font.setFontName(headerFontName);
        font.setFontHeightInPoints((short) headerFontHeight);
        font.setBold(boldHeaderFont);
        return font;
    }

    private void addHeaders(XSSFRow header, XSSFCellStyle headerStyle) {
        for (var i = 0; i < headers.length; i++) {
            var headerCell = header.createCell(i);
            headerCell.setCellValue(headers[i]);
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

            for (var fieldName : fields) {
                var cell = row.createCell(columnCounter);
                cell.setCellStyle(style);

                Method method = extractMethod(clazz, fieldName);
                var value = invokeMethod(method, item);
                writeValueToCell(cell, value);

                columnCounter++;
            }

            rowCounter++;
        }
    }

    private <T> Method extractMethod(Class<T> clazz, String fieldName) {
        try {
            return clazz.getMethod("get" + capitalize(fieldName));
        } catch (NoSuchMethodException noSuchMethodExc) {
            logger.error("Couldn't extract method for field named {} :", fieldName, noSuchMethodExc);
            throw new ServiceException("Couldn't extract method for field name " + fieldName, noSuchMethodExc);
        }
    }

    private <T> Object invokeMethod(Method method, T item) {
        try {
            return method.invoke(item, (Object[]) null);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            logger.error("Couldn't invoke method {}: ", method.getName(), exception);
            throw new ServiceException("Couldn't invoke method " + method.getName(), exception);
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
