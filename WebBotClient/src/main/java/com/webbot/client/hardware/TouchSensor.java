package com.webbot.client.hardware;

import com.webbot.client.RobotClient;

public class TouchSensor extends HardwareDevice {
    public TouchSensor(int index) {
        super(index);
    }

    public boolean isPressed() {
        return Integer.parseInt(RobotClient.getInstance().sendTcp(String.format("readt %d", index))) == 1;
    }
}
