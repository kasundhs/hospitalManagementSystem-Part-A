package org.example;

import java.util.LinkedList;
import java.util.Queue;

public class EventScheduler{
    private final IntakeQueueMonitor queue;
    private final SystemStateMonitor state;
    private final ProcessedOrderQueueMonitor processedOrderQueue;
    Queue<Producer> producerThreads = new LinkedList<>();
    Queue<Consumer> consumerThreads = new LinkedList<>();
    public EventScheduler(IntakeQueueMonitor queue, SystemStateMonitor state, ProcessedOrderQueueMonitor processedOrderQueue){
        this.queue = queue;
        this.state = state;
        this.processedOrderQueue = processedOrderQueue;
    }
    private synchronized String percentageCalculator(){
        double capacityPercentage = ((double) queue.totalQueueSize() /Constants.MAXIMUM_QUEUE_SIZE);
        if(capacityPercentage < 0.3){
            return "LOW";
        } else if (capacityPercentage < 0.7) {
            return "MEDIUM";
        }
        return "HIGH";
    }

    public synchronized void addProducers(){
        int newProducerNumber = (state.getNumberOfProducerThreads())+1;
        if(newProducerNumber <= Constants.MAXIMUM_PRODUCER_SIZE && (percentageCalculator().equals("LOW"))) {
            String name = "Clinic counter -" + String.valueOf(newProducerNumber);
            Producer producer = new Producer(queue, state, name);
            producerThreads.add(producer);
            state.setNumberOfProducerThreads();
            producer.start();
            LogWriter.threadWriterLog(name + " is Created due to Slowness of Producing.");
        }
    }

    public synchronized void addConsumer(){
        int newConsumerNumber = (state.getNumberOfConsumerThreads())+1;
        if(newConsumerNumber <= Constants.MAXIMUM_CONSUMER_SIZE && (!percentageCalculator().equals("LOW"))) {
            String name = "Doctor -"+String.valueOf(newConsumerNumber);
            Consumer consumer = new Consumer(queue,state,processedOrderQueue, name);
            consumerThreads.add(consumer);
            state.setNumberOfConsumerThreads();
            consumer.start();
            LogWriter.threadWriterLog(name + " is Created due to Slowness of the Consuming.");
        }
    }

    public void reduceProducers(){
        while(!producerThreads.isEmpty()){
            Producer producer = producerThreads.poll();
            LogWriter.threadWriterLog(producer.toString()+" is Shutdown");
            producer.shutdown();
            state.reduceProducersCount();
        }
    }

    public void reduceConsumers(){
        while ((!consumerThreads.isEmpty())){
            Consumer consumer = consumerThreads.poll();
            LogWriter.threadWriterLog(consumer.toString()+" is Shutdown");
            consumer.shutdown();
            state.reduceConsumersCount();
        }
    }
}
