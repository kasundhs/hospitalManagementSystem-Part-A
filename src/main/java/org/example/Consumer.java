package org.example;

import java.util.Random;

public class Consumer implements Runnable {
    private final IntakeQueueMonitor queue;
    private final SystemStateMonitor state;
    private final ProcessedOrderQueueMonitor processedOrderQueue;
    private volatile boolean running = true;
    private final Random rnd = new Random();
    private final String name;
    private Thread thread;

    public Consumer(IntakeQueueMonitor queue, SystemStateMonitor state, ProcessedOrderQueueMonitor processedOrderQueue, String name) {
        this.name = name;
        this.queue = queue;
        this.state = state;
        this.processedOrderQueue = processedOrderQueue;
    }

    @Override
    @SuppressWarnings("BusyWait")
    public void run() {
        try {
            while (running) {
                TestOrder order = queue.consume(state.isEmergencyPriorityEnabled());
                if (order == null) continue;
                    // System.out.println(getName() + " processing " + order);
                try {
                    LogWriter.log(name + " processing " + order);
                    state.incrementProcessed();
                    // Add processed order to queue for auditor to generate report
                    processedOrderQueue.addProcessedOrder(order);
                    if(order.priority == Priority.EMERGENCY)
                        state.decrementEmergencyPatientCount();
                }
                catch (Exception e){
                    LogWriter.log(order.toString()+" is started to Process. But Cannot Complete due to time Exceed.");
                    if(order.priority == Priority.EMERGENCY)
                        state.setEmergencyPatientCount();
                    processedOrderQueue.removeProcessedOrder();
                    state.decrementProceed();
                }
                Thread.sleep(200 + rnd.nextInt(500));
            }
        } catch (InterruptedException e) {
            if (running) {
                // System.out.println(getName() + " interrupted unexpectedly");
                LogWriter.log(name + " interrupted unexpectedly");
            }
        }
    }

    public void start() {
        thread = new Thread(this, name);
        thread.start();
    }

    public void shutdown() {
        running = false;
        if (thread != null)
            thread.interrupt();
    }

    public String toString(){
        return name;
    }
}

