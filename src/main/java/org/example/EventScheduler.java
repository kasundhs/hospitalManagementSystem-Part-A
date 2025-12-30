package org.example;

import java.util.LinkedList;

public class EventScheduler{
    private final IntakeQueueMonitor queue;
    private final SystemStateMonitor state;
    LinkedList<Producer> producerThreads = new LinkedList<>();
    LinkedList<Consumer> consumerThreads = new LinkedList<>();
    public EventScheduler(IntakeQueueMonitor queue, SystemStateMonitor state){
        this.queue = queue;
        this.state = state;
    }
    public void addProducers(){
        int newProducerNumber = (state.getNumberOfProducerThreads())+1;
        if(newProducerNumber <= Constants.MAXIMUM_PRODUCER_SIZE) {
            Producer producer = new Producer(queue, state, ("Clinic counter -" + String.valueOf(newProducerNumber)));
            producerThreads.add(producer);
            state.setNumberOfProducerThreads();
            producer.start();
        }
    }
}
