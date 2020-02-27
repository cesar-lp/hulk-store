package com.herostore.products.utils;

import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletResponse;

public class HttpUtils {

    private HttpUtils() {
    }

    public static void adaptHttpResponseForFileDownload(HttpServletResponse response, String fileName) {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
    }
}
