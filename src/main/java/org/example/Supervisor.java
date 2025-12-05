package org.example;

import java.util.Random;

public class Supervisor implements Runnable {
    private final SystemStateMonitor state;
    private volatile boolean running = true;
    private final Random rnd = new Random();
    private final String name;
    private Thread thread;

    public Supervisor(SystemStateMonitor state, String name) {
        this.name = name;
        this.state = state;
    }

    @Override
    @SuppressWarnings("BusyWait")
    public void run() {
        try {
            while (running) {
                state.lockWrite();
                if(state.getEmergencyPatientCount() < 2){
                    state.setEmergencyPriorityEnabled(false);
                }
                else
                    state.setEmergencyPriorityEnabled(true);
                LogWriter.log(name + "Update Emergency Prioritization as " + state.isEmergencyPriorityEnabled());
                state.unlockWrite();

                Thread.sleep(rnd.nextInt(1000));
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
        if(thread != null){
            thread.interrupt();
        }
    }
}

