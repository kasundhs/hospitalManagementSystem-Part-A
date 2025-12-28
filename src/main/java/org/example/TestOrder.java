package org.example;

enum Priority{
    EMERGENCY,
    NORMAL
};

enum IsSpecialTest{
    YES,
    NO
};

public class TestOrder {
    private static int counter = 0;
    public final int id;
    public final String type;
    public Priority priority;
    public final IsSpecialTest isSpecialTest;
    private static final Object idLocker = new Object();

    public TestOrder(String type, Priority priority, IsSpecialTest isSpecialTest) {
        this.type = type;
        this.isSpecialTest = isSpecialTest;
        synchronized (idLocker) {
            this.id = ++counter;
        }
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Order#" + id + " (" + type + ")";
    }
}

