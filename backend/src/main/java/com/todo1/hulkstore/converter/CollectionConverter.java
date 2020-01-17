package com.todo1.hulkstore.converter;

import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;

public class CollectionConverter {

    public static <T, U> Converter<List<T>, List<U>> toArrayList(Class<T> originType, Class<U> targetType) {
        return source -> {
            List<U> destinationList = new ArrayList<>();

            if (source == null || (source != null && source.isEmpty())) {
                return destinationList;
            }

            return new ArrayList<U>();
        };
    }
}
