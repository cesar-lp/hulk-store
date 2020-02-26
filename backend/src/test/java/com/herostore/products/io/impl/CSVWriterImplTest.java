package com.herostore.products.io.impl;

import com.herostore.products.exception.ServiceException;
import com.herostore.products.io.CSVWriter;
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
class CSVWriterImplTest {

    CSVWriter csvWriter = new CSVWriterImpl();

    @Test
    void shouldWriteSuccessfully() throws IOException {
        var os = new ByteArrayOutputStream();
        var headers = new String[]{"ID", "Name"};
        var fields = new String[]{"id", "name"};
        var elements = asList(new TestClass(1L, "Abc"), new TestClass(2L, "Def"));

        csvWriter.write(os, headers, fields, elements);

        var contentExpected = "ID,Name\r\n1,Abc\r\n2,Def\r\n";
        assertEquals(contentExpected, new String(os.toByteArray()));
    }

    @Test
    void shouldThrowServiceExceptionWhenExtractingNonExistentMethod() throws IOException {
        var os = new ByteArrayOutputStream();
        var headers = new String[]{"ID", "Name"};
        var fields = new String[]{"zz"};
        var elements = asList(new TestClass(1L, "Abc"), new TestClass(2L, "Def"));

        var expectedError = "Couldn't extract method for field named zz";

        var exc = assertThrows(ServiceException.class,
                () -> csvWriter.write(os, headers, fields, elements));

        assertEquals(expectedError, exc.getMessage());
    }

    @Test
    void shouldThrowsServiceExceptionWhenInvokingPrivateMethod() throws IOException {
        var os = new ByteArrayOutputStream();
        var headers = new String[]{"Private field method"};
        var fields = new String[]{"invalidNumber"};
        var elements = asList(new TestClass(1L, "Abc"), new TestClass(2L, "Def"));

        var expectedError = "Couldn't invoke method getInvalidNumber";

        var exc = assertThrows(ServiceException.class,
                () -> csvWriter.write(os, headers, fields, elements));

        assertEquals(expectedError, exc.getMessage());
    }
}
