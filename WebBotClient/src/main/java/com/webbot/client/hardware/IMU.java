package com.webbot.client.hardware;

import com.webbot.client.RobotClient;

public class IMU extends HardwareDevice {
    private IMU() {
        super(0);
    }

    public double getAngle() {
        return Integer.parseInt(RobotClient.getInstance().sendTcp("readi")) / 1000.0;
    }

    private static IMU instance = null;

    public static IMU getInstance() {
        if (instance == null) instance = new IMU();
        return instance;
    }
}
