package com.webbot.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.InputReportListener;
import purejavahidapi.PureJavaHidApi;

public class Gamepad {
    private final String CONTROLLER_NAME = "Logitech Dual Action";
    private final double JOYSTICK_CLIP = 0.1;

    public double left_stick_x,
            left_stick_y,
            right_stick_x,
            right_stick_y;
    public boolean dpad_up,
            dpad_down,
            dpad_left,
            dpad_right,
            x,
            a,
            b,
            y,
            left_trigger,
            right_trigger,
            left_bumper,
            right_bumper,
            back,
            start,
            left_stick_button,
            right_stick_button;

    private Gamepad() {
        List<HidDeviceInfo> deviceList = PureJavaHidApi.enumerateDevices();
        HidDeviceInfo controllerInfo = null;
        for ( HidDeviceInfo deviceInfo : deviceList ) {
            if ( deviceInfo.getProductString() != null && deviceInfo.getProductString().equals(CONTROLLER_NAME) ) {
                controllerInfo = deviceInfo;
                break;
            }
        }

        if ( controllerInfo == null ) {
            System.out.println("\033[0;103m\033[1;30m WebBot: WARNING, NO CONTROLLER DETECTED. \033[0m");
            return;
        }

        HidDevice controller = null;
        try {
            controller = PureJavaHidApi.openDevice(controllerInfo);
        } catch ( IOException e ) {
            RobotClient.exceptionCrash(e);
        }

        controller.setInputReportListener(new InputReportListener() {
            @Override
            public void onInputReport(HidDevice source,byte id,byte[] data,int length) {
                left_stick_x        = joystickValue(data[0] & 0xff,128);
                left_stick_y        = joystickValue(data[1] & 0xff,127);
                right_stick_x       = joystickValue(data[2] & 0xff,128);
                right_stick_y       = joystickValue(data[3] & 0xff,127);

                dpad_up             = new HashSet<>(Arrays.asList(7,0,1)).contains((data[4] & 0x0f));
                dpad_right          = new HashSet<>(Arrays.asList(1,2,3)).contains((data[4] & 0x0f));
                dpad_down           = new HashSet<>(Arrays.asList(3,4,5)).contains((data[4] & 0x0f));
                dpad_left           = new HashSet<>(Arrays.asList(5,6,7)).contains((data[4] & 0x0f));

                x                   = (data[4] & (1 << 4)) != 0;
                a                   = (data[4] & (1 << 5)) != 0;
                b                   = (data[4] & (1 << 6)) != 0;
                y                   = (data[4] & (1 << 7)) != 0;

                left_trigger        = (data[5] & (1 << 0)) != 0;
                right_trigger       = (data[5] & (1 << 1)) != 0;
                left_bumper         = (data[5] & (1 << 2)) != 0;
                right_bumper        = (data[5] & (1 << 3)) != 0;
                back                = (data[5] & (1 << 4)) != 0;
                start               = (data[5] & (1 << 5)) != 0;
                left_stick_button   = (data[5] & (1 << 6)) != 0;
                right_stick_button  = (data[5] & (1 << 7)) != 0;
            }
        });
    }

    private double joystickValue(int value,int origin) {
        int span;
        if ( value <= origin ) span = origin;
        else span = 255 - origin;

        double result = ((double) value - origin) / span;
        if ( Math.abs(result) >= JOYSTICK_CLIP ) return result;
        else return 0.0;
    }

    private static Gamepad instance = null;

    public static Gamepad getInstance() {
        if ( instance == null ) instance = new Gamepad();
        return instance;
    }
}