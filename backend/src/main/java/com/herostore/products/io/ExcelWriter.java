package com.herostore.products.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface ExcelWriter {

    ExcelWriter withData(WorkbookData workbookData);

    <T> void writeWorkbook(OutputStream os, List<T> elements) throws IOException;
}
