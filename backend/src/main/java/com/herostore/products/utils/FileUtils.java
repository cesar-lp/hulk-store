package com.herostore.products.utils;

import com.herostore.products.constants.FileType;

public class FileUtils {

    private FileUtils() {
    }

    public static String buildFileName(String fileName, FileType fileType) {
        return fileName + fileType.getExtension();
    }
}
