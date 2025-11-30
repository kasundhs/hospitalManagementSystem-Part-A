package org.example;

import java.util.LinkedList;
import java.util.Queue;
import org.example.TestOrder.Priority;

public class IntakeQueueMonitor {
    private final Queue<TestOrder> emergencyQueue = new LinkedList<>();
    private final Queue<TestOrder> normalQueue = new LinkedList<>();
    private final int capacity;
    private boolean isForNormalPatients = true;

    public IntakeQueueMonitor(int capacity) {
        this.capacity = capacity;
    }

    private int totalSize() {
        return emergencyQueue.size() + normalQueue.size();
    }

    public synchronized void produce(TestOrder order) throws InterruptedException {
        while (totalSize() == capacity) {
            wait();
        }
        if (order.priority == Priority.EMERGENCY)
            emergencyQueue.add(order);
        else
            normalQueue.add(order);
        notifyAll();
    }

    public synchronized TestOrder consume(boolean emergencyFirst) throws InterruptedException {
        while (totalSize() == 0) {
            wait();
        }
        TestOrder order;
        if(emergencyFirst && !emergencyQueue.isEmpty()){
            order = emergencyQueue.poll();
        }

        /* While emergencyFirst is disabled emergency patients have to wait
        * until normal queue is empty. To prevent that use isForNormalPatients variable.*/
        else if(isForNormalPatients && !normalQueue.isEmpty()){
            order = normalQueue.poll();
            isForNormalPatients = false;
        }
        else {
            order = emergencyQueue.poll();
            isForNormalPatients = true;
        }
        notifyAll();
        return order;
    }

}
