package org.example;

import java.util.LinkedList;
import java.util.Queue;

public class IntakeQueueMonitor {
    private final Queue<TestOrder> emergencyQueue = new LinkedList<>();
    private final Queue<TestOrder> normalQueue = new LinkedList<>();
    private final int capacity;
    private boolean isForNormalPatients = true;
    private volatile boolean isShuttingDown = false;

    public IntakeQueueMonitor(int capacity) {
        this.capacity = capacity;
    }

    public int totalQueueSize() {
        return emergencyQueue.size() + normalQueue.size();
    }

    public synchronized void produce(TestOrder order) throws InterruptedException {
        if(isShuttingDown)
            throw new InterruptedException("Cannot produce System is proceeding for shut down");
        while (totalQueueSize() == capacity && !isShuttingDown) {
            wait();
        }
        if(isShuttingDown)
            throw new InterruptedException("Cannot Continue producing, due to System is to shut down");
        if (order.priority == Priority.EMERGENCY)
            emergencyQueue.add(order);
        else
            normalQueue.add(order);
        notifyAll();
    }

    public synchronized TestOrder consume(boolean emergencyFirst) throws InterruptedException {
        while (totalQueueSize() == 0 && !isShuttingDown) {
            wait();
        }
        if(totalQueueSize() == 0 && isShuttingDown) {
            return null;
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
        isShuttingDown = true;
        LogWriter.log("============= System set to ShutDown =============");
        notifyAll();
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
