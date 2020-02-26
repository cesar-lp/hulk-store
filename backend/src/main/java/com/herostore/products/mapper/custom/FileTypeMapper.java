package com.herostore.products.mapper.custom;

import com.herostore.products.constants.FileType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class FileTypeMapper implements Converter<String, FileType> {

    @Override
    public FileType convert(String source) {
        var fileTypeOpt = Arrays.stream(FileType.values())
                .filter(type -> type.getDesc().equals(source))
                .findFirst();

        if (fileTypeOpt.isEmpty()) {
            return null;
        }

        return fileTypeOpt.get();
    }
}
