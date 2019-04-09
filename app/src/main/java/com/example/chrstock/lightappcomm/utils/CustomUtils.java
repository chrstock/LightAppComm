package com.example.chrstock.lightappcomm.utils;

import org.opencv.core.Point;

public final class CustomUtils {

    public static double calculateDistance(Point point1, Point point2) {
        return Math.sqrt(((point2.x - point1.x) * point2.x - point1.x)) + ((point2.y - point1.y) * (point2.y - point1.y));
    }
}
