package com.example.chrstock.lightappcomm.temp;

import android.graphics.Color;

import org.opencv.core.Point;

import java.util.List;

public final class MicrocontrollerBitUtils {

    private static final int POSITION_COUNT_BIT_1 = 24;

    private static final int POSITION_COUNT_BIT_2 = 36;

    private static final int POSITION_COUNT_BIT_3 = 48;

    private static final int POSITION_COUNT_BIT_4 = 60;

    private static final int POSITION_COUNT_BIT_5 = 72;

    private static final int POSITION_COUNT_BIT_6 = 84;

    /**
     * Calculates the current order position of the Pointmatrix
     *
     * @param allBits - all catched points
     * @return current order position
     */
    public static int calculateCountBit(String allBits) {

        if (isBitOn(allBits, POSITION_COUNT_BIT_1)) {
            return 1;
        } else if (isBitOn(allBits, POSITION_COUNT_BIT_2)) {
            return 2;
        } else if (isBitOn(allBits, POSITION_COUNT_BIT_3)) {
            return 3;
        } else if (isBitOn(allBits, POSITION_COUNT_BIT_4)) {
            return 4;
        } else if (isBitOn(allBits, POSITION_COUNT_BIT_5)) {
            return 5;
        } else if (isBitOn(allBits, POSITION_COUNT_BIT_6)) {
            return 6;
        } else {
            return 0;
        }
    }

    public static String calculateUseBits(String allBits) {

        return allBits.substring(14, 21) + allBits.substring(26, 33) + allBits.substring(38, 45)
                + allBits.substring(50, 57) + allBits.substring(62, 69) + allBits.substring(74, 81)
                + allBits.substring(86, 93) + allBits.substring(98, 105);
    }

    public static String calculateLightToBitSequence(List<Point> points) {

        int pixel;
        StringBuilder bits = new StringBuilder();

        for (int i = 0; i < points.size(); i++) {

            pixel = 1;

            if (pixel != Color.BLACK) {
                bits.append("1");
            } else {
                bits.append("0");
            }
        }

        return bits.toString();
    }

    private static boolean isBitOn(String allBits, int i) {
        return allBits.charAt(i) == '1';
    }
}
