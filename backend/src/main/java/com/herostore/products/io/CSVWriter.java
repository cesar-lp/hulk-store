package com.herostore.products.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface CSVWriter {

    <T> void write(OutputStream os, String[] headers, String[] fieldNames, List<T> elements) throws IOException;
}
