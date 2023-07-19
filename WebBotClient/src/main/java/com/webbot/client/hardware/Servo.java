package com.webbot.client.hardware;

import com.webbot.client.RobotClient;

public class Servo extends HardwareDevice {
    public Servo(int index) {
        super(index);
    }

    public void setPosition(double value) {
        RobotClient.getInstance().sendTcp(String.format("sets %d %d", index, (int) (value * 1000)));
    }

    public double getPosition() {
        return Integer.parseInt(RobotClient.getInstance().sendTcp(String.format("reads %d", index))) / 1000.0;
    }
}
