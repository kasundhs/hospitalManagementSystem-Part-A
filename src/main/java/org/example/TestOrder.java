package org.example;

public class TestOrder {
    private static int counter = 0;
    public final int id;
    public final String type;

    public TestOrder(String type) {
        this.type = type;
        this.id = ++counter;
    }

    @Override
    public String toString() {
        return "Order#" + id + " (" + type + ")";
    }
}

