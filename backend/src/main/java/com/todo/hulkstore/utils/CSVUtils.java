package com.todo.hulkstore.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.PrintWriter;

public class CSVUtils {

    private CSVUtils() {}

    public static CSVPrinter getPrinter(PrintWriter writer, String[] headers) throws IOException {
        return new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers));
    }
}
