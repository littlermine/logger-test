package com.littlemine.test.disruptor.hw;

import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class Demo1 {

    public static void main(String[] args) {
        ThreadFactory tf = Executors.defaultThreadFactory();
        // tf = Executors.defaultThreadFactory();

        LongEventFactory factory = new LongEventFactory();
        int ring = 32;

        // System.out.println(LongEventFactory.x);
        Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(factory, ring, tf, ProducerType.MULTI, new YieldingWaitStrategy());
        // System.out.println(LongEventFactory.x);
        // 通过观察LongEventFactory.x能发现disruptor创建时就会调用EventFactory的newInstance方法把所有的ring buffer中的所有元素都创建出来，后续直接使用
        // 而且是直接使用创建Disruptor对象的线程调用EventFactory.newInstance()
        // 对应代码在RingBuffer.fill()

        disruptor.handleEventsWith(new LongEventHandler());

        disruptor.start();

        LongEventProducer producer = new LongEventProducer(disruptor.getRingBuffer());

        // 使用ByteBuffer创建一块缓冲内存
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        for (long l = 1L; l < 68L; l++) {
            byteBuffer.putLong(0, l);
            producer.onData(byteBuffer);
        }

        disruptor.shutdown();
    }

}



























