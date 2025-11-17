package org.example;

public class SystemStateMonitor {
    private int activeReaders = 0;
    private int waitingWriters = 0;
    private int activeWriters = 0;

    private int totalProcessed = 0;
    private int currentCapacity = 3;

    public synchronized void lockRead() throws InterruptedException {
        while (waitingWriters > 0 || activeWriters > 0) {
            wait();
        }
        activeReaders++;
    }

    public synchronized void unlockRead() {
        activeReaders--;
        if (activeReaders == 0)
            notifyAll();
    }

    public synchronized void lockWrite() throws InterruptedException {
        waitingWriters++;
        while (activeReaders > 0 || activeWriters > 0) {
            wait();
        }
        waitingWriters--;
        activeWriters = 1;
    }

    public synchronized void unlockWrite() {
        activeWriters = 0;
        notifyAll();
    }

    public synchronized void incrementProcessed() {
        totalProcessed++;
    }

    public synchronized int getTotalProcessed() {
        return totalProcessed;
    }

    public synchronized void setCapacity(int cap) {
        currentCapacity = cap;
    }

}
