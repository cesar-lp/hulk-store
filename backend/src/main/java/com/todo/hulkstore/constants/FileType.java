package com.todo.hulkstore.constants;

public enum FileType {

    CSV("text/csv"), EXCEL("");

    private String contentType;

    FileType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }
}

