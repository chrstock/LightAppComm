package com.example.chrstock.lightappcomm.utils;

import com.example.chrstock.lightappcomm.model.LineTo;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

public class LineUtils {

    private static final double NEAR_DISTANCE_ORIGIN = 2000.0;

    private static final double FAR_DISTANCE_ORIGIN = 0.0;

    public static List<Point> determingPointsInSquare(List<LineTo> lineList) {

        List<Point> edgePoints = new ArrayList<>(4);


        List<Point> points = new ArrayList<>();

        for (LineTo line : lineList) {
            points.add(line.getPointStart());
            points.add(line.getPointEnd());
        }

        Point pointD = calculateEdgePointD(points);

        Point pointB = calculateEdgePointB(points);

        LineTo line1 = lineList.get(0);
        Point line1PointStart = line1.getPointStart();
        Point line1PointEnd = line1.getPointEnd();
        LineTo line2 = lineList.get(1);
        Point line2PointStart = line2.getPointStart();
        Point line2PointEnd = line2.getPointEnd();

        if (edgePoints.contains(line1PointStart) || edgePoints.contains(line1PointEnd)) {
            if (line2PointStart.y < line2PointEnd.y) {
                edgePoints.set(1, line2PointStart);
                edgePoints.set(2, line2PointEnd);
            } else {
                edgePoints.set(1, line2PointEnd);
                edgePoints.set(2, line2PointStart);
            }
        } else {
            if (line1PointStart.y < line1PointEnd.y) {
                edgePoints.set(1, line1PointStart);
                edgePoints.set(2, line1PointEnd);
            } else {
                edgePoints.set(1, line1PointEnd);
                edgePoints.set(2, line1PointStart);
            }
        }

        return edgePoints;

    }

    private static Point calculateEdgePointB(List<Point> points) {

        double farestDistance = FAR_DISTANCE_ORIGIN;

        Point pointB = new Point();

        for (Point point : points) {

            double calculatedDistance = calculateDistanceToOrigin(point);

            if (isPointFarerToOrigin(farestDistance,calculatedDistance)) {

                farestDistance = calculatedDistance;
                pointB = point;
            }
        }

        return pointB;

    }

    private static Point calculateEdgePointD(List<Point> points) {

        double closestDistance = NEAR_DISTANCE_ORIGIN;

        Point pointD = new Point();

        for (Point point : points) {

            double calculatedDistance = calculateDistanceToOrigin(point);

            if (isPointCloserToOrigin(closestDistance,calculatedDistance)) {
                closestDistance = calculatedDistance;
                pointD = point;
            }
        }

        return pointD;

    }

    private static boolean isPointCloserToOrigin(double currentDistance, double newDistance){
        return newDistance < currentDistance;
    }

    private static boolean isPointFarerToOrigin(double currentDistance, double newDistance){
        return newDistance > currentDistance;
    }

    private static double calculateDistanceToOrigin(Point point) {
        return Math.sqrt(Math.pow(point.x, 2) + Math.pow(point.y, 2));
    }

}
