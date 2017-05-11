package com.littlemine.test.disruptor.hw;

import com.lmax.disruptor.EventFactory;

public class LongEventFactory implements EventFactory<LongEvent> {

    public static int x = 0;

    @Override
    public LongEvent newInstance() {
        x++;
        // System.out.println(Thread.currentThread().getName());
        return new LongEvent();
    }

}
