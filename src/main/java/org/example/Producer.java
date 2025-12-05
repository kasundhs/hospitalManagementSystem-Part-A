package org.example;

import java.util.Random;
import org.example.TestOrder.*;

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
                IsSpecialTest testSpeciality = rnd.nextInt(10) < 2 ? IsSpecialTest.YES : IsSpecialTest.NO;
                TestOrder order = new TestOrder(types[rnd.nextInt(types.length)],priority, testSpeciality);

                if(order.isSpecialTest == IsSpecialTest.YES){ // Rejected Special tests
                    LogWriter.log(order.toString() + " is Rejected Due to Unavailability of Special Test");
                    continue;
                }
                if(priority == Priority.EMERGENCY){
                    state.setEmergencyPatientCount();
                }
                queue.produce(order);   // If test is not a special one then start the registering process
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
