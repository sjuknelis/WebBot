package com.webbot.client.hardware;

import com.webbot.client.RobotClient;

public class ColorSensor extends HardwareDevice {
    public ColorSensor(int index) {
        super(index);
    }

    public int red() {
        return getChannel(0);
    }

    public int green() {
        return getChannel(1);
    }

    public int blue() {
        return getChannel(2);
    }

    private int getChannel(int index) {
        return Integer.parseInt(RobotClient.getInstance().sendTcp(String.format("readc %d", index)).split(" ")[index]);
    }
}
