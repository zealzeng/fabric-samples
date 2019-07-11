package com.github.zealzeng.disruptor;

import com.lmax.disruptor.EventHandler;

public class LongEventHandler implements EventHandler<LongEvent>
{
    public void onEvent(LongEvent event, long sequence, boolean endOfBatch)
    {
        int total = 0;
        for (int i = 1; i <= event.get(); ++i) {
            total += i;
        }
        System.out.println("Event: " + total);
    }
}
