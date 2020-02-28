package com.herostore.products.io.impl;

import com.herostore.products.io.CSVWriter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import static com.herostore.products.utils.ClassUtils.extractMethod;
import static com.herostore.products.utils.ClassUtils.invokeMethod;
import static java.util.stream.Collectors.toList;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CSVWriterImpl implements CSVWriter {

    @Override
    public <T> void write(OutputStream os, String[] headers, String[] fieldNames, List<T> elements) throws IOException {
        var writer = new PrintWriter(os);

        try (var printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers))) {
            var clazz = elements.get(0).getClass();

            for (T element : elements) {
                var values = Arrays.stream(fieldNames)
                        .map(fieldName -> extractMethod(clazz, fieldName))
                        .map(method -> invokeMethod(method, element))
                        .collect(toList());

                printer.printRecord(values);
            }
        }
    }
}
