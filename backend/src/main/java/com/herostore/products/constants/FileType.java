package com.herostore.products.constants;

public enum FileType {

    CSV("csv", ".csv"),
    EXCEL("xlsx", ".xlsx");

    private String desc;
    private String extension;

    FileType(String desc, String extension) {
        this.desc = desc;
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public String getDesc() {
        return desc;
    }
}

