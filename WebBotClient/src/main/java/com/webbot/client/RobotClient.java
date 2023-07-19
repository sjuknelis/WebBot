package com.webbot.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RobotClient {
    private final String ROBOT_ADDR = "192.168.43.1";
    private final int ROBOT_PORT = 7123;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    private RobotClient() {
        try {
            socket = new Socket(ROBOT_ADDR,ROBOT_PORT);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream());
        } catch ( IOException e ) {
            exceptionCrash(e);
        }
    }

    public String sendTcp(String message) {
        writer.println(message);
        writer.flush();
        try {
            String response = reader.readLine();
            if ( response == null ) throw new IOException("Robot closed connection");
            if ( response.startsWith("err") ) {
                if ( response.equals("err name") ) throw new IllegalArgumentException("Invalid hardware device or hook name");
                else throw new IOException("Invalid response from robot: " + response);
            }
            return response;
        } catch ( IOException e ) {
            exceptionCrash(e);
            return null;
        }
    }

    public boolean confirmConnection() {
        // program would've crashed in the constructor if connection failed
        System.out.println(styleTerminalText("Successfully connected to robot"));
        return true;
    }

    public static void exceptionCrash(Exception e) {
        e.printStackTrace();
        System.out.println(styleTerminalText("WebBot has crashed due to the above exception. Use /exit to exit JShell"));
        System.exit(1);
    }

    private static String styleTerminalText(String text) {
        return String.format("\033[0;103m\033[1;30mWebBot: %s\033[0m",text);
    }

    private static RobotClient instance = null;

    public static RobotClient getInstance() {
        if ( instance == null ) instance = new RobotClient();
        return instance;
    }
}