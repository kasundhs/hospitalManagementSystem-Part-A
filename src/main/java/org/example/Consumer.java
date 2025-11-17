package org.example;

import java.util.Random;

public class Consumer extends Thread {
    private final IntakeQueueMonitor queue;
    private final SystemStateMonitor state;
    private volatile boolean running = true;
    private final Random rnd = new Random();

    public Consumer(IntakeQueueMonitor q, SystemStateMonitor s, String name) {
        super(name);
        this.queue = q;
        this.state = s;
    }

    @Override
    @SuppressWarnings("BusyWait")
    public void run() {
        try {
            while (running) {
                TestOrder order = queue.consume();
                // System.out.println(getName() + " processing " + order);
                LogWriter.log(getName() + " processing " + order);
                Thread.sleep(500 + rnd.nextInt(500));
                state.incrementProcessed();
            }
        } catch (InterruptedException e) {
            if (running) {
                // System.out.println(getName() + " interrupted unexpectedly");
                LogWriter.log(getName() + " interrupted unexpectedly");
            }
        }
    }

    public void shutdown() {
        running = false;
        interrupt();
    }
}

