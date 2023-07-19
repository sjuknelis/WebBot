# WebBot - FTC Rapid Development Platform

WebBot is a rapid development platform for FTC programming. It uses JShell to provide a REPL where you can write Java code which will be executed on your robot as soon as you hit Enter, saving you the need to write testing op modes and giving you the freedom to quickly experiment with different hardware devices, movement patterns, control methods, etc. You can also write complete opmodes with WebBot, in which case you can deploy changes in just a few seconds without the need to wait for your Control Hub to receive your uploaded code and restart.

WebBot works by running your code on your computer and streaming commands to your robot over WiFi. As such, using WebBot is obviously not legal in FTC because you would need your computer with you. However, code written for WebBot is designed to mirror code you would write for a normal FTC opmode, so generally you should be able to copy and paste WebBot code to use it as normal FTC code.

## Getting started

1. Download this repository and open it in Android Studio (just as you would with the official FTC SDK).
2. Build the project (and rebuild it each time you change your code).
3. Use the TeamCode run configuration to deploy the WebBot opmode to your robot. You only need to do this once (unless you add/make changes to hooks).
4. Optional: Change the motor names in WebBotClient/src/main/java/com/webbot/teamcode/SampleMecanum.java to match your robot’s hardware configuration.
5. Optional: Connect a Logitech F310 controller to your computer and follow [these instructions](https://gist.github.com/jackblk/8138827afd986f30cf9d26647e8448e1) if you are on Mac.
6. Make sure you are connected to your robot’s WiFi network.
7. Run the WebBot run configuration; the Android Studio terminal will pop up.
8. Use the arrow keys to select whether you want to run the SampleMecanum opmode or the REPL.

## Gamepad support

Currently, WebBot only supports one Logitech F310 gamepad at a time and uses the PureJavaHIDAPI to communicate with it. This API should allow WebBot to access your gamepad using nothing more than the Java libraries WebBot already comes with; however, if you are using a Mac, please follow [these instructions](https://gist.github.com/jackblk/8138827afd986f30cf9d26647e8448e1) (and of course, no matter what OS you're using, remember to hit Start+A).

## Hooks

You cannot directly access library (e.g. OpenCV, RoadRunner) methods from WebBot (although I’m planning to add native RoadRunner bindings soon). If you have written library code through the normal FTC method though, you can integrate it into WebBot using hooks.

To create a hook, go into the WebBotHooks file in TeamCode and create a class implementing the following interface:

java```interface WebBot.Hook {
    public int[] run(int[] params):
}```

Then register the hook in the WebBotHooks class:

java```hookManager.registerHook(“hook name”,new MyHook());```

The provided SampleHook provides an example of a hook that returns its first parameter plus 1.

When you have changed the behavior of a hook, you will need to manually redeploy TeamCode to the robot. Afterward you will be able to call your hook from WebBot (there is an example of this in SampleMecanum):

java```Hooks.call(“sampleHook”,new int[]{3});```

## Speed of execution

As WebBot code is run on your computer with commands being streamed live to your robot, latency could be a concern. However, in our tests, a robot using WebBot can respond to inputs from the controller just as quickly as one using a normal FTC opmode and is also capable of performing complex calculations (including IMU, encoder, and sensor readings) at sufficient speed. A DcMotor `setPower` call uses only ~4.25ms of latency. (And of course, during tele-op, your Driver Station is streaming gamepad inputs to the Control Hub via WiFi, so the )    