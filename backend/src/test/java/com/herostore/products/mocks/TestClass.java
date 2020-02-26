package com.herostore.products.mocks;

public class TestClass {
    private long id;
    private String name;
    private int invalidNumber;

    public TestClass(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getInvalidNumber() {
        return 1 / 0;
    }
}
