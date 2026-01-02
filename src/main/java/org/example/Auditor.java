package org.example;

public class Auditor implements Runnable {
    private final SystemStateMonitor state;
    private final ProcessedOrderQueueMonitor processedOrderQueue;
    private volatile boolean running = true;
    private final String name;
    private Thread thread;

    public Auditor(SystemStateMonitor state, ProcessedOrderQueueMonitor processedOrderQueue, String name) {
        this.name = name;
        this.state = state;
        this.processedOrderQueue = processedOrderQueue;
    }

    @Override
    @SuppressWarnings("BusyWait")
    public void run() {
        try {
            while (running) {
                long startTime = System.currentTimeMillis();
                // Consume processed order from queue (with wait/notify synchronization)
                TestOrder order = processedOrderQueue.consumeForReport();
                
                try {
                    if (order != null) {
                        // Generate report using ReportGenerator
                        LogWriter.log(name + " generating report for " + order);
                        ReportGenrator reportGenerator = new ReportGenrator(order);
                        reportGenerator.reportDetails(order);
                        
                        // Update report count
                        state.setTotalReportGeneratecount();
                        
                        long endTime = System.currentTimeMillis();
                        long timeConsumed = endTime - startTime;
                        state.addAuditorTimeConsumption(timeConsumed);
                        
                        LogWriter.log(name + " completed report generation for " + order);

                        try{
                            state.lockRead();
                            LogWriter.log("Total Proceed by Doctors :"+state.getTotalProcessed()+" and Total Report Generated :"+state.getTotalReportGeneratecount());
                            state.unlockRead();
                        }
                        catch (InterruptedException e){
                            LogWriter.log(name + " interrupted unexpectedly");
                        }
                    }
                } finally {
                    // Always release the processing lock after generating report (or if order was null)
                    processedOrderQueue.releaseProcessingLock();
                }
                Thread.sleep(300);
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
