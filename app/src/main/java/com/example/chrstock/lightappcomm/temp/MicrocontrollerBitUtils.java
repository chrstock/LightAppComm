package com.example.chrstock.lightappcomm.temp;

import android.graphics.Color;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.List;

public final class MicrocontrollerBitUtils {

    public static int calculateCountBit(String allBits) {

        int count = 0;

        if (allBits.charAt(24) == '1') {
            count = 1;
        } else if (allBits.charAt(36) == '1') {
            count = 2;
        } else if (allBits.charAt(48) == '1') {
            count = 3;
        } else if (allBits.charAt(60) == '1') {
            count = 4;
        } else if (allBits.charAt(72) == '1') {
            count = 5;
        } else if (allBits.charAt(84) == '1') {
            count = 6;
        }

        return count;
    }

    public static String calculateUseBits(String allBits) {

        return allBits.substring(14, 21) + allBits.substring(26, 33) + allBits.substring(38, 45)
                + allBits.substring(50, 57) + allBits.substring(62, 69) + allBits.substring(74, 81)
                + allBits.substring(86, 93) + allBits.substring(98, 105);
    }

    public static String calculateLightToBitSequence(List<Point> points) {

        int x, y;
        int pixel;
        StringBuilder bits = new StringBuilder();

        for (int i = 0; i < points.size(); i++) {

            x = (int) points.get(i).x;
            y = (int) points.get(i).y;

            pixel = 1;

            if (pixel != Color.BLACK) {
                bits.append("1");
            } else {
                bits.append("0");
            }
        }

        return bits.toString();
    }
}
