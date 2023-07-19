package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TeleOp(name="WebBot")
public class WebBot extends LinearOpMode {
    private final int PORT = 7123;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    private List<DcMotor> motors = new ArrayList<>();
    private List<Servo> servos = new ArrayList<>();
    private List<DistanceSensor> distanceSensors = new ArrayList<>();
    private List<ColorSensor> colorSensors = new ArrayList<>();
    private List<TouchSensor> touchSensors = new ArrayList<>();
    private BNO055IMU imu;

    private HookManager hookManager = new HookManager();

    @Override
    public void runOpMode() {
        WebBotHooks.registerHooks(hookManager);

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = false;
        imu = hardwareMap.get(BNO055IMU.class,"imu");
        imu.initialize(parameters);
        while ( ! imu.isGyroCalibrated() ) {
            sleep(50);
            idle();
        }

        try {
            serverSocket = new ServerSocket(PORT);
            new StopRequestThread().start();
            waitForStart();

            while ( opModeIsActive() ) {
                telemetry.addData("Status","Waiting for client");
                telemetry.update();
                clientSocket = serverSocket.accept();
                telemetry.addData("Status","Client connected");
                telemetry.update();

                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                while ( opModeIsActive() ) {
                    String commandText = reader.readLine();
                    if ( commandText == null ) {
                        // client disconnected
                        resetDevices();
                        break;
                    }

                    String outputText = processCommand(commandText);
                    telemetry.addData("Status","Client connected");
                    telemetry.addData("Last command",commandText);
                    telemetry.addData("Last response",outputText);
                    telemetry.update();
                    writer.println(outputText);
                    writer.flush();
                }
            }
        } catch ( IOException e ) {
            resetDevices();
            if ( isStopRequested() ) return;
            telemetry.addData("Status","Socket failure");
            telemetry.addData("Failure message",e.getMessage());
            telemetry.update();
            waitForStart();
            while ( opModeIsActive() ) idle();
        } finally {
            closeSockets();
        }
    }

    private class StopRequestThread extends Thread {
        public void run() {
            while ( ! isStopRequested() ) idle();
            closeSockets();
        }
    }

    private void closeSockets() {
        try {
            if ( serverSocket != null && ! serverSocket.isClosed() ) serverSocket.close();
            if ( clientSocket != null && ! clientSocket.isClosed() ) clientSocket.close();
        } catch ( IOException ignored ) {}
    }

    private void resetDevices() {
        for ( DcMotor motor : motors ) {
            motor.setPower(0);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }
        motors.clear();
        servos.clear();
        distanceSensors.clear();
        colorSensors.clear();
        touchSensors.clear();
    }

    private String processCommand(String commandText) {
        String[] parts = commandText.split(" ");
        String command = parts[0];

        String name;
        int deviceIndex;
        ColorSensor colorSensor;
        try {
            switch ( command ) {
                case "getm":
                    name = parts[1];
                    DcMotor motor = hardwareMap.get(DcMotor.class,name);
                    motors.add(motor);
                    return Integer.toString(motors.size() - 1);

                case "gets":
                    name = parts[1];
                    Servo servo = hardwareMap.get(Servo.class,name);
                    servos.add(servo);
                    return Integer.toString(servos.size() - 1);

                case "getd":
                    name = parts[1];
                    DistanceSensor distanceSensor = hardwareMap.get(DistanceSensor.class,name);
                    distanceSensors.add(distanceSensor);
                    return Integer.toString(distanceSensors.size() - 1);

                case "getc":
                    name = parts[1];
                    colorSensor = hardwareMap.get(ColorSensor.class,name);
                    colorSensors.add(colorSensor);
                    return Integer.toString(colorSensors.size() - 1);

                case "gett":
                    name = parts[1];
                    TouchSensor touchSensor = hardwareMap.get(TouchSensor.class,name);
                    touchSensors.add(touchSensor);
                    return Integer.toString(touchSensors.size() - 1);

                case "powerm":
                    deviceIndex = Integer.parseInt(parts[1]);
                    double motorPower = (double) Integer.parseInt(parts[2]) / 1000;
                    motors.get(deviceIndex).setPower(motorPower);
                    return "ok";

                case "readm":
                    deviceIndex = Integer.parseInt(parts[1]);
                    return Integer.toString(motors.get(deviceIndex).getCurrentPosition());

                case "targetm":
                    deviceIndex = Integer.parseInt(parts[1]);
                    int motorTarget = Integer.parseInt(parts[2]);
                    motors.get(deviceIndex).setTargetPosition(motorTarget);
                    return "ok";

                case "modem":
                    deviceIndex = Integer.parseInt(parts[1]);
                    int mode = Integer.parseInt(parts[2]);
                    if (mode == 0)
                        motors.get(deviceIndex).setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    else if (mode == 1)
                        motors.get(deviceIndex).setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    else return "err data";
                    return "ok";

                case "brakem":
                    deviceIndex = Integer.parseInt(parts[1]);
                    int shouldBrake = Integer.parseInt(parts[2]);
                    if (shouldBrake == 0)
                        motors.get(deviceIndex).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                    else if (shouldBrake == 1)
                        motors.get(deviceIndex).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                    else return "err data";
                    return "ok";

                case "sets":
                    deviceIndex = Integer.parseInt(parts[1]);
                    double servoPosition = (double) Integer.parseInt(parts[2]) / 1000;
                    servos.get(deviceIndex).setPosition(servoPosition);
                    return "ok";

                case "reads":
                    deviceIndex = Integer.parseInt(parts[1]);
                    return Integer.toString((int) (servos.get(deviceIndex).getPosition() * 1000));

                case "readd":
                    deviceIndex = Integer.parseInt(parts[1]);
                    return Integer.toString((int) (distanceSensors.get(deviceIndex).getDistance(DistanceUnit.INCH) * 1000));

                case "readc":
                    deviceIndex = Integer.parseInt(parts[1]);
                    colorSensor = colorSensors.get(deviceIndex);
                    return colorSensor.red() + " " + colorSensor.green() + " " + colorSensor.blue();

                case "readt":
                    deviceIndex = Integer.parseInt(parts[1]);
                    return touchSensors.get(deviceIndex).isPressed() ? "1" : "0";

                case "readi":
                    double angle = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
                    return Integer.toString((int) (angle * 1000));

                case "runhook":
                    name = parts[1];
                    int[] params = new int[parts.length - 2];
                    for ( int i = 0; i < params.length; i++ ) params[i] = Integer.parseInt(parts[i + 2]);

                    int[] outputs = hookManager.runHook(name,params);
                    if ( outputs == null ) return "err name";

                    StringBuilder outputText = new StringBuilder();
                    for ( int i = 0; i < outputs.length; i++ ) {
                        outputText.append(outputs[i]);
                        if ( i < outputs.length - 1 ) outputText.append(" ");
                    }
                    return outputText.toString();
            }
        } catch ( IllegalArgumentException e ) {
            return "err name";
        } catch ( IndexOutOfBoundsException e ) {
            return "err index";
        }
        return "err cmd";
    }

    public class HookManager {
        private Map<String,Hook> hooks = new HashMap<>();

        public void registerHook(String name,Hook hook) {
            hooks.put(name,hook);
        }

        public int[] runHook(String name,int[] params) {
            if ( ! hooks.containsKey(name) ) return null;
            return hooks.get(name).run(params);
        }
    }

    public interface Hook {
        int[] run(int[] params);
    }
}
