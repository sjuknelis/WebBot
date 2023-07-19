package com.webbot.teamcode;

import com.webbot.client.Hooks;
import com.webbot.client.LinearOpMode;
import com.webbot.client.hardware.DcMotor;
import com.webbot.client.hardware.DistanceSensor;
import com.webbot.client.hardware.DistanceUnit;
import com.webbot.client.hardware.IMU;

public class SampleMecanum extends LinearOpMode {
    @Override
    public void runOpMode()  {
        DcMotor frontLeft = hardwareMap.get(DcMotor.class,"ma");
        DcMotor frontRight = hardwareMap.get(DcMotor.class,"md");
        DcMotor backLeft = hardwareMap.get(DcMotor.class,"mb");
        DcMotor backRight = hardwareMap.get(DcMotor.class,"mc");

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);

        DistanceSensor distanceSensor = hardwareMap.get(DistanceSensor.class,"da");

        IMU imu = hardwareMap.get(IMU.class,"imu");

        while ( opModeIsActive() ) {
            double drive = gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double turn = gamepad1.right_stick_x;

            frontLeft.setPower(drive + strafe + turn);
            frontRight.setPower(drive - strafe - turn);
            backLeft.setPower(drive - strafe + turn);
            backRight.setPower(drive + strafe - turn);

            telemetry.addData("drive",drive);
            telemetry.addData("strafe",strafe);
            telemetry.addData("turn",turn);
            //telemetry.addData("distance",distanceSensor.getDistance(DistanceUnit.INCH));
            //telemetry.addData("angle",imu.getAngle());
            //telemetry.addData("3 + 1", Hooks.call("sampleHook",new int[]{3})[0]);
            telemetry.addData("cycle",getCycleTime());
            telemetry.update();
        }
    }
}
