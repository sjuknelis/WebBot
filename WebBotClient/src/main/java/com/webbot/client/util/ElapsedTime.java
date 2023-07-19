package com.webbot.client.util;

public class ElapsedTime {
    private long startTime;

    public ElapsedTime() {
        reset();
    }

    public void reset() {
        startTime = System.currentTimeMillis();
    }

    public long milliseconds() {
        return System.currentTimeMillis() - startTime;
    }

    public long seconds() {
        return milliseconds() / 1000;
    }
}
