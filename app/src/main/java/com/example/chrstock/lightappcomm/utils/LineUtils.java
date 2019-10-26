package com.example.chrstock.lightappcomm.utils;

import com.example.chrstock.lightappcomm.model.LineTo;
import com.google.common.collect.Lists;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineUtils {

    public static Map<Character, Point> determingPointsInSquare(List<LineTo> lineList) {

        Map<Character, Point> squarePoints = new HashMap<>();

        List<Point> points = getAllPointsOfLineList(lineList);

        Point pointD = calculateEdgePointD(points);
        squarePoints.put('D', pointD);
        Point pointB = calculateEdgePointB(points);
        squarePoints.put('B', pointB);

        Point pointA;
        Point pointC;

        if (squarePoints.containsValue(lineList.get(0).getPointStart()) || squarePoints.containsValue(lineList.get(0).getPointEnd())) {

            LineTo line2 = lineList.get(1);
            Point line2PointStart = line2.getPointStart();
            Point line2PointEnd = line2.getPointEnd();

            pointA = line2PointStart.y < line2PointEnd.y ? line2PointStart : line2PointEnd;
            pointC = line2PointStart.y < line2PointEnd.y ? line2PointEnd : line2PointStart;
        } else {
            LineTo line1 = lineList.get(0);
            Point line1PointStart = line1.getPointStart();
            Point line1PointEnd = line1.getPointEnd();

            pointA = line1PointStart.y < line1PointEnd.y ? line1PointStart : line1PointEnd;
            pointC = line1PointStart.y < line1PointEnd.y ? line1PointEnd : line1PointStart;
        }

        squarePoints.put('A', pointA);
        squarePoints.put('C', pointC);

        return squarePoints;

    }

    private static List<Point> getAllPointsOfLineList(List<LineTo> lineList) {

        List<Point> points = new ArrayList<>();

        lineList.forEach(l -> points.addAll(Lists.newArrayList(l.getPointStart(), l.getPointEnd())));

        return points;
    }

    private static Point calculateEdgePointB(List<Point> points) {

        double furthestDistance = 0.0;
        Point pointB = new Point();

        for (Point point : points) {
            double calculatedDistance = calculateDistanceToOrigin(point);
            if (isPointFurtherToOrigin(furthestDistance, calculatedDistance)) {
                furthestDistance = calculatedDistance;
                pointB = point;
            }
        }

        return pointB;

    }

    private static Point calculateEdgePointD(List<Point> points) {

        double closestDistance = 2000.0;
        Point pointD = new Point();

        for (Point point : points) {
            double calculatedDistance = calculateDistanceToOrigin(point);
            if (isPointCloserToOrigin(closestDistance, calculatedDistance)) {
                closestDistance = calculatedDistance;
                pointD = point;
            }
        }

        return pointD;

    }

    private static boolean isPointCloserToOrigin(double currentDistance, double newDistance) {
        return newDistance < currentDistance;
    }

    private static boolean isPointFurtherToOrigin(double currentDistance, double newDistance) {
        return newDistance > currentDistance;
    }

    private static double calculateDistanceToOrigin(Point point) {
        return Math.sqrt(Math.pow(point.x, 2) + Math.pow(point.y, 2));
    }

}
