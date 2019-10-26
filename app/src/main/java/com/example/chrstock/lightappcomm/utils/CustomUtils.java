package com.example.chrstock.lightappcomm.utils;

import org.opencv.core.Point;

public final class CustomUtils {

    public static double calculateDistance(Point point1, Point point2) {
        return Math.sqrt(Math.pow(point2.x - point1.x, 2) + Math.pow(point2.y - point1.y, 2));
    }
}
