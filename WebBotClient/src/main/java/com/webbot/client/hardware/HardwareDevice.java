package com.webbot.client.hardware;

abstract class HardwareDevice {
    protected int index;

    protected HardwareDevice(int index) {
        this.index = index;
    }
}
