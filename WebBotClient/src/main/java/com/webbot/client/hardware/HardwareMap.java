package com.webbot.client.hardware;

import com.webbot.client.RobotClient;

public class HardwareMap {
    private HardwareMap() {}

    public <T extends HardwareDevice> T get(Class<T> tClass,String name) {
        if ( tClass == DcMotor.class ) {
            return (T) new DcMotor(getIndex("m",name));
        } else if ( tClass == Servo.class ) {
            return (T) new Servo(getIndex("s",name));
        } else if ( tClass == DistanceSensor.class ) {
            return (T) new DistanceSensor(getIndex("d",name));
        } else if ( tClass == ColorSensor.class ) {
            return (T) new ColorSensor(getIndex("c",name));
        } else if ( tClass == TouchSensor.class ) {
            return (T) new TouchSensor(getIndex("t",name));
        } else if ( tClass == IMU.class ) {
            return (T) IMU.getInstance();
        }
        return null;
    }

    private int getIndex(String code,String name) {
        return Integer.parseInt(RobotClient.getInstance().sendTcp(String.format("get%s %s",code,name)));
    }

    private static HardwareMap instance = null;

    public static HardwareMap getInstance() {
        if ( instance == null ) instance = new HardwareMap();
        return instance;
    }
}

