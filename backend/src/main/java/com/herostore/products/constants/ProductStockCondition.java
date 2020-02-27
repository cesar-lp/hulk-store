package com.herostore.products.constants;

public enum ProductStockCondition {
    ALL("all"), AVAILABLE("available"), UNAVAILABLE("unavailable");

    private String value;

    ProductStockCondition(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
