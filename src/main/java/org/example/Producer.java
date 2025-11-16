package org.example;

import java.util.Random;

public class Producer extends Thread {
    private final IntakeQueueMonitor queue;
    private volatile boolean running = true;
    private final Random rnd = new Random();

    public Producer(IntakeQueueMonitor q, String name) {
        super(name);
        this.queue = q;
    }

    @Override
    @SuppressWarnings("BusyWait")
    public void run() {
        String[] types = {"PCR", "Blood Test", "Histopathology)"};
        try {
            while (running) {
                TestOrder order = new TestOrder(types[rnd.nextInt(types.length)]);
                queue.produce(order);
                System.out.println(getName() + " produced " + order);
                Thread.sleep(300 + rnd.nextInt(300));
            }
        } catch (InterruptedException e) {
            if (running) {
                System.out.println(getName() + " interrupted unexpectedly");
            }
        }
    }

    public void shutdown() {
        running = false;
        interrupt();
    }
}
