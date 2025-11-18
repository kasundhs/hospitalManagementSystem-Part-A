package org.example;

import java.util.Random;

public class Producer implements Runnable {
    private final IntakeQueueMonitor queue;
    private volatile boolean running = true;
    private final Random rnd = new Random();
    private final String name;
    private Thread thread;

    public Producer(IntakeQueueMonitor queue, String name) {
        this.name = name;
        this.queue = queue;
    }

    @Override
    @SuppressWarnings("BusyWait")
    public void run() {
        String[] types = {"PCR", "Blood Test", "Histopathology)"};
        try {
            while (running) {
                TestOrder order = new TestOrder(types[rnd.nextInt(types.length)]);
                queue.produce(order);
                // System.out.println(getName() + " produced " + order);
                LogWriter.log(name + " produced " + order);
                Thread.sleep(300 + rnd.nextInt(300));
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
        if(thread != null)
            thread.interrupt();
    }
}
