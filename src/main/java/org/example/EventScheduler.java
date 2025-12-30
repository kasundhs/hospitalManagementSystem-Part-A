package org.example;

import java.util.LinkedList;
import java.util.Queue;

public class EventScheduler{
    private final IntakeQueueMonitor queue;
    private final SystemStateMonitor state;
    private final ProcessedOrderQueueMonitor processedOrderQueue;
    private Queue<Producer> producerThreads = new LinkedList<>();
    private Queue<Consumer> consumerThreads = new LinkedList<>();

    // To keep ids without duplicating and reuse
    private Queue<Integer> producerIds = new LinkedList<>();
    private Queue<Integer> consumerIds = new LinkedList<>();

    public EventScheduler(IntakeQueueMonitor queue, SystemStateMonitor state, ProcessedOrderQueueMonitor processedOrderQueue){
        this.queue = queue;
        this.state = state;
        this.processedOrderQueue = processedOrderQueue;
    }
    private synchronized String percentageCalculator(){
        double capacityPercentage = ((double) queue.totalQueueSize() /Constants.MAXIMUM_QUEUE_SIZE);
        if(capacityPercentage < 0.3){
            return "LOW";
        }
        else if (capacityPercentage < 0.7) {
            return "MEDIUM";
        }
        return "HIGH";
    }

    private synchronized void addProducers(){
        int newProducerNumber = 0;
        if(producerIds.isEmpty()) newProducerNumber = (state.getNumberOfProducerThreads())+1;
        else newProducerNumber = producerIds.peek();

        if(newProducerNumber <= Constants.MAXIMUM_PRODUCER_SIZE && (percentageCalculator().equals("LOW"))) {
            String name = "Clinic counter -" + String.valueOf(newProducerNumber);
            Producer producer = new Producer(queue, state, name);
            producerThreads.add(producer);
            state.setNumberOfProducerThreads();
            producer.start();
            LogWriter.threadWriterLog(name + " is Created due to Slowness of Producing.");
            producerIds.poll();
        }
    }

    private synchronized void addConsumer(){
        int newConsumerNumber = 0;
        if(consumerIds.isEmpty()) newConsumerNumber = (state.getNumberOfConsumerThreads())+1;
        else newConsumerNumber = consumerIds.peek();

        if(newConsumerNumber <= Constants.MAXIMUM_CONSUMER_SIZE && (!percentageCalculator().equals("LOW"))) {
            String name = "Doctor -"+String.valueOf(newConsumerNumber);
            Consumer consumer = new Consumer(queue,state,processedOrderQueue, name);
            consumerThreads.add(consumer);
            state.setNumberOfConsumerThreads();
            consumer.start();
            LogWriter.threadWriterLog(name + " is Created due to Slowness of the Consuming.");
            consumerIds.poll();
        }
    }

    public synchronized void shutdownAllProducers(){
        while(!producerThreads.isEmpty()){
            Producer producer = producerThreads.poll();
            LogWriter.threadWriterLog(producer.toString()+" is Shutdown");
            producer.shutdown();
            state.reduceProducersCount();
        }
    }

    public synchronized void shutdownAllConsumers(){
        while ((!consumerThreads.isEmpty())){
            Consumer consumer = consumerThreads.poll();
            LogWriter.threadWriterLog(consumer.toString()+" is Shutdown");
            consumer.shutdown();
            state.reduceConsumersCount();
        }
    }

    /* In here implement reduce one thread from either producer or consumer at one time. */
    private synchronized void graceFullShutdownProducer(){
        if(percentageCalculator().equals("HIGH") && (!producerThreads.isEmpty())){
            Producer producer = producerThreads.poll();
            producer.gracefulShutdown();
            state.reduceProducersCount();
            String name = producer.toString();
            producerIds.add(Integer.parseInt(name.split("-")[1]));
            LogWriter.threadWriterLog(name+" is shutdown gracefully");
        }
    }

    private synchronized void graceFullShutdownConsumer(){
        if(percentageCalculator().equals("LOW") && (!consumerThreads.isEmpty())){
            Consumer consumer = consumerThreads.poll();
            consumer.gracefulShutdown();
            state.reduceConsumersCount();
            String name = consumer.toString();
            consumerIds.add(Integer.parseInt(name.split("-")[1]));
            LogWriter.threadWriterLog(name+" is shutdown gracefully");
        }
    }

    public synchronized void producerThreadMaintainer(){
        addProducers();
        graceFullShutdownProducer();
    }

    public synchronized void consumerThreadMaintainer(){
        addConsumer();
        graceFullShutdownConsumer();
    }
}
