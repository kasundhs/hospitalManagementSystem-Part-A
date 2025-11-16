package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws InterruptedException {
        IntakeQueueMonitor queue = new IntakeQueueMonitor(10);
        SystemStateMonitor state = new SystemStateMonitor();

        Producer prod1 = new Producer(queue, "Clinic-1");
        Producer prod2 = new Producer(queue, "Clinic-2");

        Consumer consumer1 = new Consumer(queue, state, "Analyzer-1");
        Consumer consumer2 = new Consumer(queue, state, "Analyzer-2");

        Auditor auditor1 = new Auditor(state, "Auditor-1");
        Auditor auditor2 = new Auditor(state, "Auditor-2");

        Supervisor supervisor = new Supervisor(state, "Supervisor");

        prod1.start();
        prod2.start();
        consumer1.start();
        consumer2.start();
        auditor1.start();
        auditor2.start();
        supervisor.start();

        Thread.sleep(15000);

        prod1.shutdown();
        prod2.shutdown();
        consumer1.shutdown();
        consumer2.shutdown();
        auditor1.shutdown();
        auditor2.shutdown();
        supervisor.shutdown();
    }
}