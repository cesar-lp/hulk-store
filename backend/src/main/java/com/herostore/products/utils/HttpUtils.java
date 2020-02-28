package com.herostore.products.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;

public class HttpUtils {

    private HttpUtils() {
    }

    public static void adaptHttpResponseForFileDownload(HttpServletResponse response, String fileName) {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }
}
