package org.example;

import java.util.LinkedList;
import java.util.Queue;

public class ProcessedOrderQueueMonitor {
    private final Queue<TestOrder> processedOrderQueue = new LinkedList<>();
    private boolean isProcessing = false; // Lock to ensure one auditor processes at a time

    public synchronized void addProcessedOrder(TestOrder order) throws InterruptedException {
        processedOrderQueue.add(order);
        notifyAll(); // Notify waiting auditors that an order is available
    }

    public synchronized TestOrder consumeForReport() throws InterruptedException {
        // Wait if queue is empty or another auditor is processing
        while (processedOrderQueue.isEmpty() || isProcessing) {
            wait(); // Lock: wait if processing order exists or queue is empty
        }
        
        // Lock the processing
        isProcessing = true;
        TestOrder order = processedOrderQueue.poll();
        notifyAll(); // Notify other waiting auditors
        return order;
    }

    public synchronized void releaseProcessingLock() {
        isProcessing = false;
        notifyAll(); // Release: notify all waiting auditors
    }

    public synchronized void setExpiration() {
        LogWriter.log("============= Processed Order Queue - Clearing remaining orders =============");
        if(processedOrderQueue.isEmpty()){
            LogWriter.log("============= All Reports are Generated Successfully. Nothing to Expire =============");
        }
        while (!processedOrderQueue.isEmpty()) {
            TestOrder order = processedOrderQueue.poll();
            LogWriter.log(order.toString() + " report generation expired due to system timeout");
        }
        notifyAll();
    }
}

