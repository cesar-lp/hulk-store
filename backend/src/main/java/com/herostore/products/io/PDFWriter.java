package com.herostore.products.io;

import java.io.OutputStream;

public interface PDFWriter {

    void createDocument(OutputStream outputStream);

    void openDocument();

    boolean isDocumentOpen();

    void writeDocument();

    void closeDocument();
}
