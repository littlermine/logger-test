package com.littlemine.test.disruptor.hw;

import com.lmax.disruptor.EventHandler;

public class LongEventHandler implements EventHandler<LongEvent> {

    @Override
    public void onEvent(LongEvent event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println(String.format("%s   %d   %d   %s", Thread.currentThread().getName(), event.getVal(), sequence, endOfBatch));
        try {
            // Thread.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
