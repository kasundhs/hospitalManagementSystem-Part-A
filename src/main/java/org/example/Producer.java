package org.example;

import java.util.Random;
import org.example.TestOrder.Priority;

public class Producer implements Runnable {
    private final IntakeQueueMonitor queue;
    private final SystemStateMonitor state;
    private volatile boolean running = true;
    private final Random rnd = new Random();
    private final String name;
    private Thread thread;

    public Producer(IntakeQueueMonitor queue, SystemStateMonitor state, String name) {
        this.name = name;
        this.queue = queue;
        this.state = state;
    }

    @Override
    @SuppressWarnings("BusyWait")
    public void run() {
        String[] types = {"PCR", "Blood Test", "Histopathology)"};
        try {
            while (running) {
                Priority priority = rnd.nextInt(10) < 5 ? Priority.EMERGENCY : Priority.NORMAL;
                TestOrder order = new TestOrder(types[rnd.nextInt(types.length)],priority);
                if(priority == Priority.EMERGENCY){
                    state.setEmergencyPatientCount();
                }
                queue.produce(order);
                LogWriter.log(name + " Registering " + order +"With Priority " +priority);
                Thread.sleep(100 + rnd.nextInt(300));
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
