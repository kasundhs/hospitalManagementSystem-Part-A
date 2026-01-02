package org.example;

public class Main {
    public static void main(String[] args){
        IntakeQueueMonitor queue = new IntakeQueueMonitor(Constants.MAXIMUM_QUEUE_SIZE);
        SystemStateMonitor state = new SystemStateMonitor();
        ProcessedOrderQueueMonitor processedOrderQueue = new ProcessedOrderQueueMonitor();
        EventScheduler event = new EventScheduler(queue,state,processedOrderQueue);

        /* Initially it has only one Producer and one Consumers.
        Assume: Maximum number of producers are 3 and maximum number of consumers are 3.
         */
        Producer prod1 = new Producer(queue, state, "Clinic counter -1");
        Consumer consumer1 = new Consumer(queue, state, processedOrderQueue, "Doctor -1");

        Auditor auditor1 = new Auditor(state, processedOrderQueue, "Auditor -1");
        Auditor auditor2 = new Auditor(state, processedOrderQueue, "Auditor -2");

        Supervisor supervisor = new Supervisor(state,event, "Supervisor");

        prod1.start();
        consumer1.start();
        auditor1.start();
        auditor2.start();
        supervisor.start();


        System.out.println("System Started... Waiting 15 seconds before stopping...");

        try {
            Thread.sleep(15000); // wait for 15 seconds
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted.");
        }

        LogWriter.threadWriterLog("============ System Shutdown is Started ============");
        supervisor.shutdown();
        prod1.shutdown();
        event.shutdownAllProducers();
        queue.setExpiration();
        System.out.println("System is Shutting Down....");
        consumer1.shutdown();
        event.shutdownAllConsumers();
        processedOrderQueue.setExpiration();
        auditor1.shutdown();
        auditor2.shutdown();
        
        // Calculate and display performance metrics
        System.out.println("\n============ Performance Metrics ============");
        System.out.println("Total Registered Count (without rejecting): " + state.getTotalRegisteredCount());
        System.out.println("Total Processed Count: " + state.getTotalProcessed());
        System.out.println("Total Report Generated Count: " + state.getTotalReportGeneratecount());
        
        // Calculate average time consumption
        int registeredCount = state.getTotalRegisteredCount();
        int processedCount = state.getTotalProcessed();
        int reportCount = state.getTotalReportGeneratecount();
        
        if (registeredCount > 0) {
            double avgProducerTime = (double) state.getProducerTotalTimeConsumption() / registeredCount;
            System.out.println("Average Producer Time Consumption: " + String.format("%.2f", avgProducerTime) + " ms");
        } else {
            System.out.println("Average Producer Time Consumption: N/A (no registrations)");
        }
        
        if (processedCount > 0) {
            double avgConsumerTime = (double) state.getConsumerTotalTimeConsumption() / processedCount;
            System.out.println("Average Consumer Time Consumption: " + String.format("%.2f", avgConsumerTime) + " ms");
        } else {
            System.out.println("Average Consumer Time Consumption: N/A (no processed orders)");
        }
        
        if (reportCount > 0) {
            double avgAuditorTime = (double) state.getAuditorTotalTimeConsumption() / reportCount;
            System.out.println("Average Auditor Time Consumption: " + String.format("%.2f", avgAuditorTime) + " ms");
        } else {
            System.out.println("Average Auditor Time Consumption: N/A (no reports generated)");
        }
        
        System.out.println("=============================================\n");
    }
}
