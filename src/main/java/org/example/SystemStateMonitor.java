package org.example;

public class SystemStateMonitor {
    private int activeReadersCount = 0;
    private int waitingWritersCount = 0;
    private int activeWritersCount = 0;

    private int totalProcessedReportCount = 0;
    private int emergencyPatientCount = 0;
    private boolean emergencyPriorityEnabled = true;
    private int totalReportGeneratecount = 0;
    private int numberOfProducerThreads = 1;
    private int numberOfConsumerThreads = 1;
    
    // Performance metrics
    private int totalRegisteredCount = 0;
    private long producerTotalTimeConsumption = 0;
    private long consumerTotalTimeConsumption = 0;
    private long auditorTotalTimeConsumption = 0;

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
    public synchronized void decrementProceed(){
        totalProcessedReportCount--;
    }
    public int getTotalProcessed() {
        return totalProcessedReportCount;
    }
    public synchronized void setEmergencyPriorityEnabled(boolean enabled) {
        emergencyPriorityEnabled = enabled;
    }
    public boolean isEmergencyPriorityEnabled() {
        return emergencyPriorityEnabled;
    }
    public synchronized void setEmergencyPatientCount() {
        emergencyPatientCount++;
    }
    public int getEmergencyPatientCount() {
        return emergencyPatientCount;
    }
    public synchronized void decrementEmergencyPatientCount() { emergencyPatientCount--;}
    public int getTotalReportGeneratecount() { return totalReportGeneratecount;}
    public synchronized void setTotalReportGeneratecount() {totalReportGeneratecount++;}
    public int getNumberOfProducerThreads() {
        return numberOfProducerThreads;
    }
    public void setNumberOfProducerThreads() {
        numberOfProducerThreads++;
    }
    public int getNumberOfConsumerThreads() {
        return numberOfConsumerThreads;
    }
    public void setNumberOfConsumerThreads() {
        numberOfConsumerThreads++;
    }
    public void reduceProducersCount(){
        numberOfProducerThreads--;
    }
    public void reduceConsumersCount(){
        numberOfConsumerThreads--;
    }
    
    // Performance metrics methods
    public synchronized void incrementTotalRegisteredCount() {
        totalRegisteredCount++;
    }
    
    public int getTotalRegisteredCount() {
        return totalRegisteredCount;
    }
    
    public synchronized void addProducerTimeConsumption(long time) {
        producerTotalTimeConsumption += time;
    }
    
    public long getProducerTotalTimeConsumption() {
        return producerTotalTimeConsumption;
    }
    
    public synchronized void addConsumerTimeConsumption(long time) {
        consumerTotalTimeConsumption += time;
    }
    
    public long getConsumerTotalTimeConsumption() {
        return consumerTotalTimeConsumption;
    }
    
    public synchronized void addAuditorTimeConsumption(long time) {
        auditorTotalTimeConsumption += time;
    }
    
    public long getAuditorTotalTimeConsumption() {
        return auditorTotalTimeConsumption;
    }

}
