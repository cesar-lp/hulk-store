package com.herostore.products.io;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WorkbookData {

    String sheetName;
    int[] columnWidths;
    String[] headers;
    String[] fields;
    boolean boldHeaderFont;
    int headerFontHeight;
    String headerFontName;

    private WorkbookData(WorkbookBuilder builder) {
        this.sheetName = builder.sheetName;
        this.columnWidths = builder.columnWidths;
        this.headers = builder.headers;
        this.fields = builder.fields;
        this.boldHeaderFont = builder.boldHeaderFont;
        this.headerFontHeight = builder.headerFontHeight;
        this.headerFontName = builder.headerFontName;
    }

    public static WorkbookBuilder builder() {
        return new WorkbookBuilder();
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class WorkbookBuilder {

        String sheetName;
        int[] columnWidths;
        String[] headers;
        String[] fields;
        boolean boldHeaderFont = true;
        int headerFontHeight = 12;
        String headerFontName = "Arial";

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

        public WorkbookData build() {
            return new WorkbookData(this);
        }
    }
}
