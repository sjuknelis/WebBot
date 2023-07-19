package org.firstinspires.ftc.teamcode;

public class WebBotHooks {
    public static void registerHooks(WebBot.HookManager manager) {
        manager.registerHook("sampleHook",new SampleHook());
    }
}

class SampleHook implements WebBot.Hook {
    private int amountToIncrease;

    public SampleHook() {
        // Runs on runOpMode(), before waitForStart()
        amountToIncrease = 1;
    }

    public int[] run(int[] params) {
        return new int[]{params[0] + amountToIncrease};
    }
}