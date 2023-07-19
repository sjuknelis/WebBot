package com.webbot.client;

public class Hooks {
    public static int[] call(String name,int[] params) {
        StringBuilder paramText = new StringBuilder();
        for ( int i = 0; i < params.length; i++ ) {
            paramText.append(params[i]);
            if ( i < params.length - 1 ) paramText.append(" ");
        }

        String[] outputTexts = RobotClient.getInstance().sendTcp(String.format("runhook %s %s",name,paramText)).split(" ");

        int[] outputs = new int[outputTexts.length];
        for ( int i = 0; i < outputTexts.length; i++ ) outputs[i] = Integer.parseInt(outputTexts[i]);

        return outputs;
    }
}
