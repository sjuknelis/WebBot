package com.webbot.client.hardware;

import com.webbot.client.RobotClient;

public class DistanceSensor extends HardwareDevice {
    public DistanceSensor(int index) {
        super(index);
    }

    public double getDistance(DistanceUnit unit) {
        double inches = Integer.parseInt(RobotClient.getInstance().sendTcp(String.format("readd %d", index))) / 1000.0;

        if (unit == DistanceUnit.INCH) {
            return inches;
        } else {
            double meters = inches * 0.0254;
            if (unit == DistanceUnit.METER) return meters;
            else if (unit == DistanceUnit.CM) return meters * 100;
            else if (unit == DistanceUnit.MM) return meters * 1000;
        }
        return 10000;
    }
}
