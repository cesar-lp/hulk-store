package com.todo.hulkstore.utils;

import com.todo.hulkstore.constants.FileType;

import javax.servlet.http.HttpServletResponse;

public class FileUtils {

    private FileUtils() {}

    public static void adaptHttpResponse(HttpServletResponse response, String fileName, FileType fileType) {
        response.setContentType(fileType.getContentType());
        response.setHeader("Content-Disposition", "attachment; filename=" + buildFileName(fileName, fileType));
    }

    private static String buildFileName(String fileName, FileType fileType) {
        return fileName + "." + fileType;
    }
}
