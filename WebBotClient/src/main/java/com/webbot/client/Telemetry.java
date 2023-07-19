package com.webbot.client;

import java.util.ArrayList;
import java.util.List;

public class Telemetry {
    public class Line {
        public String content;

        public Line(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }

    public class Item extends Line {
        public Item(String content) {
            super(content);
        }
    }

    private List<Line> lines = new ArrayList<>();
    private boolean autoClear = true;

    private Telemetry() {}

    public Item addData(String caption,Object value) {
        Item item = new Item(String.format("%s: %s",caption,value.toString()));
        lines.add(item);
        return item;
    }

    public Item addData(String caption,String format,Object... args) {
        Item item = new Item(String.format("%s: %s",caption,String.format(format,args)));
        lines.add(item);
        return item;
    }

    public Line addLine() {
        Line line = new Line("");
        lines.add(line);
        return line;
    }

    public Line addLine(String content) {
        Line line = new Line(content);
        lines.add(line);
        return line;
    }

    public void clearAll() {
        lines.clear();
    }

    public boolean removeItem(Item item) {
        return lines.remove(item);
    }

    public boolean removeLine(Line line) {
        return lines.remove(line);
    }

    public void setAutoClear(boolean autoClear) {
        this.autoClear = autoClear;
    }

    public void update() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        for ( Line line : lines ) {
            System.out.println(line.getContent());
        }
        if ( autoClear ) clearAll();
    }

    private static Telemetry instance = null;

    public static Telemetry getInstance() {
        if ( instance == null ) instance = new Telemetry();
        return instance;
    }
}
