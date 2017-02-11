package person;

import java.util.Observable;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Aschur on 08.12.2016.
 */
public class ConsumersQueueTeller extends Observable{

    private ArrayBlockingQueue<Consumer> consumers;

    public ConsumersQueueTeller(int countConsumers) {
        this.consumers = new ArrayBlockingQueue<Consumer>(countConsumers);
    }

    public void add(Consumer consumer){

        consumers.add(consumer);

        setChanged();
        notifyObservers();



    }

    public Consumer poll(){

        Consumer consumer = consumers.poll();

        setChanged();
        notifyObservers();


        return consumer;

    }

    public Object[] getConsumersArray(){

        return consumers.toArray();

    }

    public int size(){

        return consumers.size();

    }

    public boolean isEmpty(){
        return consumers.isEmpty();
    }



}
