package com.webbot.client.util;

public class Range {
    public double clip(double number,double min,double max) {
        if ( number < min ) return min;
        else if ( number > max ) return max;
        return number;
    }

    public double scale(double number,double x1,double x2,double y1,double y2) {
        double ratio = (number - x1) / (x2 - x1);
        return ratio * (y2 - y1) + y1;
    }

    public void throwIfRangeIsInvalid(double number,double min,double max) {
        if ( number < min || number > max ) throw new IllegalArgumentException();
    }
}
