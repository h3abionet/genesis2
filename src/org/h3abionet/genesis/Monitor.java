package org.h3abionet.genesis;

import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class Monitor implements Runnable{
    private final Thread thread;
    private final AtomicLong startTime
            = new AtomicLong(Long.MAX_VALUE);
    private final int thresholdMS;

    public Monitor(Thread thread, int thresholdMS){
        this.thread = thread;
        this.thresholdMS = thresholdMS;
    }

    public void reset(){
        startTime.set(System.currentTimeMillis());
    }

    @Override
    public void run(){
        while(thread.isAlive()){
            long timeTaken = System.currentTimeMillis()-startTime.get();
            if(timeTaken > thresholdMS){
                System.out.println(timeTaken + "-------------------------");
                Stream.of(thread.getStackTrace())
                        .forEach(System.out::println);
            }
            try {
                Thread.sleep(thresholdMS/2);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}