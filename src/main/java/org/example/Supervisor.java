package org.example;

import java.util.Random;

public class Supervisor extends Thread {
    private final SystemStateMonitor state;
    private volatile boolean running = true;
    private final Random rnd = new Random();

    public Supervisor(SystemStateMonitor s, String name) {
        super(name);
        this.state = s;
    }

    @Override
    @SuppressWarnings("BusyWait")
    public void run() {
        try {
            while (running) {
                state.lockWrite();
                int newCap = 2 + rnd.nextInt(4);
                state.setCapacity(newCap);
                System.out.println(getName() + " updated analyzer capacity to " + newCap);
                state.unlockWrite();

                Thread.sleep(2000);
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

