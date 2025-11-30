package org.example;

public class SystemStateMonitor {
    private int activeReadersCount = 0;
    private int waitingWritersCount = 0;
    private int activeWritersCount = 0;

    private int totalProcessedReportCount = 0;
    private int emergencyPatientCount =0;
    private boolean emergencyPriorityEnabled = true;

    public synchronized void lockRead() throws InterruptedException {
        while (waitingWritersCount > 0 || activeWritersCount > 0) {
            wait();
        }
        activeReadersCount++;
    }

    public synchronized void unlockRead() {
        activeReadersCount--;
        if (activeReadersCount == 0)
            notifyAll();
    }

    public synchronized void lockWrite() throws InterruptedException {
        waitingWritersCount++;
        while (activeReadersCount > 0 || activeWritersCount > 0) {
            wait();
        }
        waitingWritersCount--;
        activeWritersCount = 1;
    }

    public synchronized void unlockWrite() {
        activeWritersCount = 0;
        notifyAll();
    }

    public synchronized void incrementProcessed() {
        totalProcessedReportCount++;
    }

    public synchronized int getTotalProcessed() {
        return totalProcessedReportCount;
    }


    public synchronized void setEmergencyPriorityEnabled(boolean enabled) {
        emergencyPriorityEnabled = enabled;
    }

    public synchronized boolean isEmergencyPriorityEnabled() {
        return emergencyPriorityEnabled;
    }

    public void setEmergencyPatientCount() {
        emergencyPatientCount++;
    }

    public int getEmergencyPatientCount() {
        return emergencyPatientCount;
    }

    public void decrementEmergencyPatientCount() { emergencyPatientCount--;}

}
