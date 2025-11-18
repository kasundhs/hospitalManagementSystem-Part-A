package org.example;

public class Auditor implements Runnable {
    private final SystemStateMonitor state;
    private volatile boolean running = true;
    private final String name;
    private Thread thread;

    public Auditor(SystemStateMonitor state, String name) {
        this.name = name;
        this.state = state;
    }

    @Override
    @SuppressWarnings("BusyWait")
    public void run() {
        try {
            while (running) {
                state.lockRead();
                int processed = state.getTotalProcessed();
                state.unlockRead();

                // System.out.println(getName() + " read totalProcessed=" + processed);
                LogWriter.log(name + " read totalProcessed=" + processed);

                Thread.sleep(400);
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
