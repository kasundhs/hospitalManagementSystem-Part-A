package org.example;

public class Main {
    public static void main(String[] args){
        IntakeQueueMonitor queue = new IntakeQueueMonitor(Constants.MAXIMUM_QUEUE_SIZE);
        SystemStateMonitor state = new SystemStateMonitor();
        EventScheduler event = new EventScheduler(queue,state);
        ProcessedOrderQueueMonitor processedOrderQueue = new ProcessedOrderQueueMonitor();

        /* Initially it has only one Producer and one Consumers.
        Assume: Maximum number of producers are 3 and maximum number of consumers are 3.
         */
        Producer prod1 = new Producer(queue, state, "Clinic counter -1");
        Consumer consumer1 = new Consumer(queue, state, processedOrderQueue, event, "Doctor -1");

        Auditor auditor1 = new Auditor(state, processedOrderQueue, "Auditor -1");
        Auditor auditor2 = new Auditor(state, processedOrderQueue, "Auditor -2");

        Supervisor supervisor = new Supervisor(state, "Supervisor");

        prod1.start();
        supervisor.start();
        consumer1.start();
        auditor1.start();
        auditor2.start();


        System.out.println("System Started... Waiting 15 seconds before stopping...");

        try {
            Thread.sleep(15000); // wait for 15 seconds
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted.");
        }

        prod1.shutdown();
        queue.setExpiration();
        System.out.println("System is Shutting Down....");
        consumer1.shutdown();
        processedOrderQueue.setExpiration();
        auditor1.shutdown();
        auditor2.shutdown();
        supervisor.shutdown();
    }
}
