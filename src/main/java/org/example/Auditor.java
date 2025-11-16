package org.example;

public class Auditor extends Thread {
    private final SystemStateMonitor state;
    private volatile boolean running = true;

    public Auditor(SystemStateMonitor s, String name) {
        super(name);
        this.state = s;
    }

    @Override
    @SuppressWarnings("BusyWait")
    public void run() {
        try {
            while (running) {
                state.lockRead();
                int processed = state.getTotalProcessed();
                state.unlockRead();

                System.out.println(getName() + " read totalProcessed=" + processed);

                Thread.sleep(400);
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
