package com.webbot.client;

import com.webbot.client.hardware.HardwareMap;

public abstract class LinearOpMode {
    protected HardwareMap hardwareMap = HardwareMap.getInstance();
    protected Gamepad gamepad1 = Gamepad.getInstance();
    protected Telemetry telemetry = Telemetry.getInstance();

    public void runOpMode() {}

    public boolean opModeIsActive() {
        idle();
        CycleTimer.nextCycle();
        return true;
    }

    public boolean isStopRequested() {
        idle();
        return false;
    }

    public boolean isStarted() {
        return true;
    }

    public void waitForStart() {}

    public void idle() {
        sleep(1);
    }

    public void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    public long getCycleTime() {
        return CycleTimer.getCycleTime();
    }
}

class CycleTimer {
    private static final int TRACKED_LENGTH = 25;
    private static long[] cycleTimes = new long[TRACKED_LENGTH];
    private static int cycleIndex = 0;
    private static long lastCycleStart = System.currentTimeMillis();

    public static void nextCycle() {
        cycleTimes[cycleIndex] = System.currentTimeMillis() - lastCycleStart;
        lastCycleStart = System.currentTimeMillis();
        cycleIndex = (cycleIndex + 1) % TRACKED_LENGTH;
    }

    public static long getCycleTime() {
        long sum = 0;
        for ( long time : cycleTimes ) sum += time;
        return sum / TRACKED_LENGTH;
    }
}