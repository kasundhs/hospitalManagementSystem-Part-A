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

    public int totalQueueSize() {
        return emergencyQueue.size() + normalQueue.size();
    }

    public synchronized void produce(TestOrder order) throws InterruptedException {
        while (totalQueueSize() == capacity) {
            wait();
        }
        if (order.priority == Priority.EMERGENCY)
            emergencyQueue.add(order);
        else
            normalQueue.add(order);
        notifyAll();
    }

    public synchronized TestOrder consume(boolean emergencyFirst) throws InterruptedException {
        while (totalQueueSize() == 0) {
            wait();
        }
        TestOrder order = null;
        if(emergencyFirst && !emergencyQueue.isEmpty()){
            order = emergencyQueue.poll();
        }

        /* While emergencyFirst is disabled emergency patients have to wait
        * until normal queue is empty. To prevent that use isForNormalPatients variable.*/
        else if(isForNormalPatients && !normalQueue.isEmpty()){
            order = normalQueue.poll();
            isForNormalPatients = false;
        }
        else if(!emergencyQueue.isEmpty()){
            order = emergencyQueue.poll();
            isForNormalPatients = true;
        }
        notifyAll();
        return order;
    }
    public synchronized void setExpiration(){
        LogWriter.log("============= System set to ShutDown =============");
        while(!normalQueue.isEmpty()){
            TestOrder order = normalQueue.poll();
            LogWriter.log(order.toString()+ " is expired due to system timeout");
        }
        while(!emergencyQueue.isEmpty()){
            TestOrder order = emergencyQueue.poll();
            LogWriter.log(order.toString()+ " is expired due to system timeout");
        }
    }

}
