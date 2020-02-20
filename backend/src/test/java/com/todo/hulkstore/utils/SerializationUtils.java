package com.todo.hulkstore.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class SerializationUtils {

    private SerializationUtils() {}

    public static ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
}
