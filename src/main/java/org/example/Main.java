package org.example;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args){
        IntakeQueueMonitor queue = new IntakeQueueMonitor(20);
        SystemStateMonitor state = new SystemStateMonitor();
        ProcessedOrderQueueMonitor processedOrderQueue = new ProcessedOrderQueueMonitor();

        Producer prod1 = new Producer(queue, state, "Clinic counter -1");
        Producer prod2 = new Producer(queue, state, "Clinic counter -2");

        Consumer consumer1 = new Consumer(queue, state, processedOrderQueue, "Doctor -1");
        Consumer consumer2 = new Consumer(queue, state, processedOrderQueue, "Doctor -2");

        Auditor auditor1 = new Auditor(state, processedOrderQueue, "Auditor -1");
        Auditor auditor2 = new Auditor(state, processedOrderQueue, "Auditor -2");

        Supervisor supervisor = new Supervisor(state, "Supervisor");

        prod1.start();
        prod2.start();
        supervisor.start();
        consumer1.start();
        consumer2.start();
        auditor1.start();
        auditor2.start();


        System.out.println("System running... Waiting 15 seconds before stopping...");

        try {
            Thread.sleep(15000); // wait for 15 seconds
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted.");
        }

        prod1.shutdown();
        prod2.shutdown();
        queue.setExpiration();
        if(queue.totalQueueSize() == 0){
            System.out.println("System is Shutting Down....");
            consumer1.shutdown();
            consumer2.shutdown();
            processedOrderQueue.setExpiration();
            auditor1.shutdown();
            auditor2.shutdown();
            supervisor.shutdown();
        }
    }
}