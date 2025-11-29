package org.example;

public class TestOrder {
    public enum Priority{
        EMERGENCY,
        NORMAL
    };

    private static int counter = 0;
    public final int id;
    public final String type;
    public Priority priority;

    public TestOrder(String type, Priority priority) {
        this.type = type;
        this.id = ++counter;
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Order#" + id + " (" + type + ")";
    }
}

