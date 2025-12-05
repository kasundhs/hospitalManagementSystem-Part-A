package org.example;

public class TestOrder {
    public enum Priority{
        EMERGENCY,
        NORMAL
    };
    public enum IsSpecialTest{
        YES,
        NO
    };

    private static int counter = 0;
    public final int id;
    public final String type;
    public Priority priority;
    public final IsSpecialTest isSpecialTest;

    public TestOrder(String type, Priority priority, IsSpecialTest isSpecialTest) {
        this.type = type;
        this.isSpecialTest = isSpecialTest;
        this.id = ++counter;
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Order#" + id + " (" + type + ")";
    }
}

