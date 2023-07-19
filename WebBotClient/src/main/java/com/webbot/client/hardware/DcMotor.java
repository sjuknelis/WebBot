package com.webbot.client.hardware;

import com.webbot.client.RobotClient;

public class DcMotor extends HardwareDevice {
    public DcMotor(int index) {
        super(index);
    }

    private int directionSign = 1;

    public void setPower(double power) {
        RobotClient.getInstance().sendTcp(String.format("powerm %d %d", index, (int) (power * directionSign * 1000)));
    }

    public enum Direction {
        FORWARD,
        REVERSE
    }

    public void setDirection(DcMotor.Direction direction) {
        if (direction == DcMotor.Direction.FORWARD) directionSign = 1;
        else if (direction == DcMotor.Direction.REVERSE) directionSign = -1;
    }

    public int getCurrentPosition() {
        return Integer.parseInt(RobotClient.getInstance().sendTcp(String.format("readm %d", index)));
    }

    public void setTargetPosition(int value) {
        RobotClient.getInstance().sendTcp(String.format("targetm %d %d", index, value));
    }

    public enum Mode {
        RUN_WITHOUT_ENCODER,
        RUN_TO_POSITION
    }

    public void setMode(DcMotor.Mode mode) {
        int modeNumber;
        if (mode == DcMotor.Mode.RUN_WITHOUT_ENCODER) modeNumber = 0;
        else if (mode == DcMotor.Mode.RUN_TO_POSITION) modeNumber = 1;
        else throw new IllegalArgumentException();
        RobotClient.getInstance().sendTcp(String.format("modem %d %d", index, modeNumber));
    }

    public enum ZeroPowerBehavior {
        FLOAT,
        BRAKE
    }

    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior behavior) {
        int shouldBrake;
        if (behavior == DcMotor.ZeroPowerBehavior.FLOAT) shouldBrake = 0;
        else if (behavior == DcMotor.ZeroPowerBehavior.BRAKE) shouldBrake = 1;
        else throw new IllegalArgumentException();
        RobotClient.getInstance().sendTcp(String.format("brakem %d %d", index, shouldBrake));
    }
}
