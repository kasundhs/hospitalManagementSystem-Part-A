package org.example;

import java.util.Random;

public class Consumer implements Runnable {
    private final IntakeQueueMonitor queue;
    private final SystemStateMonitor state;
    private volatile boolean running = true;
    private final Random rnd = new Random();
    private final String name;
    private Thread thread;

    public Consumer(IntakeQueueMonitor queue, SystemStateMonitor state, String name) {
        this.name = name;
        this.queue = queue;
        this.state = state;
    }

    @Override
    @SuppressWarnings("BusyWait")
    public void run() {
        try {
            while (running) {
                boolean emergencyFirst = state.isEmergencyPriorityEnabled();
                TestOrder order = queue.consume(emergencyFirst);
                if(order.priority == TestOrder.Priority.EMERGENCY)
                    state.decrementEmergencyPatientCount();
                // System.out.println(getName() + " processing " + order);
                LogWriter.log(name + " processing " + order);
                Thread.sleep(200 + rnd.nextInt(500));
                state.incrementProcessed();

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
}

