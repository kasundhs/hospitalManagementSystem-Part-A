package org.example;

import java.util.LinkedList;
import java.util.Queue;

public class IntakeQueueMonitor {
    private final Queue<TestOrder> queue = new LinkedList<>();
    private final int capacity;

    public IntakeQueueMonitor(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void produce(TestOrder order) throws InterruptedException {
        while (queue.size() == capacity) {
            wait();
        }
        queue.add(order);
        notifyAll();
    }

    public synchronized TestOrder consume() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        TestOrder order = queue.poll();
        notifyAll();
        return order;
    }

}
