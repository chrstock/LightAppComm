package com.example.chrstock.lightappcomm.temp;

import android.graphics.Color;

import com.example.chrstock.lightappcomm.model.LineTo;
import com.example.chrstock.lightappcomm.utils.AsciiConverterUtils;
import com.example.chrstock.lightappcomm.utils.CustomUtils;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Calculations {

    public static String calculateSignal(List<Mat> mats) {

        String psk;

        List<String> pskSorted = new ArrayList<>();

        List<LineTo> lines;
        Map<String, Point> squarePoints;

        int count = 0;

        for (Mat mat : mats) {

            List<Point> coordinates = calculateBoundingBoxCenter(mat);
            lines = calculateAllDistances(coordinates);

            squarePoints = determingPointsInSquare(lines);

            List<Double> firstRow = calculateRows(0, squarePoints);
            List<Double> firstColumn = calculateColumns(0, squarePoints);
            List<Double> lastRow = calculateRows(11, squarePoints);
            List<Double> lastColumn = calculateColumns(11, squarePoints);

            List<Point> points = calculateAllPoints(firstRow, firstColumn, lastRow, lastColumn);

            String bits = MicrocontrollerBitUtils.calculateLightToBitSequence(mat, mats.get(count++), points);

            int pskOrder = MicrocontrollerBitUtils.calculateCountBit(bits);

            String useBits = MicrocontrollerBitUtils.calculateUseBits(bits);

            psk = AsciiConverterUtils.convertToAscii(useBits);

            pskSorted.add(pskOrder, psk);

        }
        psk = "        ";

        for (int i = 0; i < pskSorted.size(); i++) {
            psk = psk + " " + pskSorted.get(i);
        }

        return psk;

    }

    private static List<Point> calculateBoundingBoxCenter(Mat mat) {

        int x;
        int y;

        List<Point> detectedPoints = new ArrayList<>();

        Mat matHierarchy = new Mat();

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(mat, contours, matHierarchy, 0, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        Imgproc.drawContours(mat, contours, -1, new Scalar(255, 255, 255), -1);

        for (MatOfPoint point : contours) {
            Moments moment = Imgproc.moments(point);
            x = (int) (moment.get_m10() / moment.get_m00());
            y = (int) (moment.get_m01() / moment.get_m00());
            detectedPoints.add(new Point(x, y));
        }

        matHierarchy.release();

        return detectedPoints;
    }

    private static List<LineTo> calculateAllDistances(List<Point> points) {

        int amount = points.size();
        double distance;
        double firstBiggestDistance = 0.0;
        double secondBiggestDistance = 0.0;

        List<LineTo> lines = new ArrayList<>();

        for (int i = 0; i < (amount - 1); i++) {
            Point pointStart = points.get(i);
            for (int j = (i + 1); j < amount; j++) {
                Point pointEnd = points.get(j);
                distance = CustomUtils.calculateDistance(pointStart, pointEnd);
                if (distance > firstBiggestDistance) {

                    if (firstBiggestDistance != 0.0)
                        lines.add(1, lines.get(0));

                    lines.add(0, new LineTo(pointStart, pointEnd));
                    secondBiggestDistance = firstBiggestDistance;
                    firstBiggestDistance = distance;
                } else {
                    if (distance > secondBiggestDistance) {
                        lines.add(1, new LineTo(pointStart, pointEnd));
                        secondBiggestDistance = distance;
                    }
                }
            }
        }
        return lines;
    }

    private static Map<String, Point> determingPointsInSquare(List<LineTo> lineList) {

        double nearDistanceOrigin = 2000.0;
        double farDistanceOrigin = 0.0;
        double distance;

        Map<String, Point> squarePoints = new HashMap<>();

        try {
            for (int i = 0; i < 2; i++) {
                distance = lineList.get(i).getDistance();
                if (distance < nearDistanceOrigin) {
                    nearDistanceOrigin = distance;
                    squarePoints.put("A", lineList.get(i).getPointStart());
                }

                if (distance > farDistanceOrigin) {
                    farDistanceOrigin = distance;
                    squarePoints.put("D", lineList.get(i).getPointStart());
                }

                distance = lineList.get(i).getDistance();
                if (distance < nearDistanceOrigin) {
                    nearDistanceOrigin = distance;
                    squarePoints.put("A", lineList.get(i).getPointEnd());
                }

                if (distance > farDistanceOrigin) {
                    farDistanceOrigin = distance;
                    squarePoints.put("D", lineList.get(i).getPointEnd());
                }

            }

            if (squarePoints.containsValue(lineList.get(0).getPointStart()) || (squarePoints.containsValue(lineList.get(0).getPointEnd()))) {
                if (lineList.get(1).getPointStart().y < lineList.get(1).getPointEnd().y) {
                    squarePoints.put("B", lineList.get(1).getPointStart());
                    squarePoints.put("C", lineList.get(1).getPointEnd());
                } else {
                    squarePoints.put("C", lineList.get(1).getPointStart());
                    squarePoints.put("B", lineList.get(1).getPointEnd());
                }
            } else {
                if (lineList.get(0).getPointStart().y < lineList.get(0).getPointEnd().y) {
                    squarePoints.put("B", lineList.get(0).getPointStart());
                    squarePoints.put("C", lineList.get(0).getPointEnd());
                } else {
                    squarePoints.put("C", lineList.get(0).getPointStart());
                    squarePoints.put("B", lineList.get(0).getPointEnd());
                }
            }

        } catch (NullPointerException ne) {
            //no elements found
            squarePoints.put("A", new Point(0, 0));
            squarePoints.put("B", new Point(0, 0));
            squarePoints.put("C", new Point(0, 0));
            squarePoints.put("D", new Point(0, 0));
        }

        return squarePoints;

    }

    private static List<Double> calculateRows(int rowCount, Map<String, Point> squarePoints) {
        List<Double> column = new ArrayList<>();

        double coordinateXSquarePointA;

        if (squarePoints.containsKey("A") & squarePoints.get("A") != null) {
            coordinateXSquarePointA = squarePoints.get("A").x;
        } else {
            return null;
        }


        double coordinateXSquarePointB = squarePoints.get("B").x;
        double coordinateXSquarePointC = squarePoints.get("C").x;
        double coordinateXSquarePointD = squarePoints.get("D").x;

        for (int i = 0; i < 10; i++) {
            double difference;
            if (rowCount == 0) {
                difference = Math.abs(coordinateXSquarePointA - coordinateXSquarePointC);
                difference = difference / 9 * i;

                if (coordinateXSquarePointA > coordinateXSquarePointC) {
                    column.add((coordinateXSquarePointA) - difference);
                } else {
                    column.add((coordinateXSquarePointA) + difference);
                }
            }

            if (rowCount == 11) {
                difference = Math.abs(coordinateXSquarePointB - coordinateXSquarePointD);
                difference = difference / 9 * i;

                if (coordinateXSquarePointB > coordinateXSquarePointD) {
                    column.add(coordinateXSquarePointB - difference);
                } else {
                    column.add(coordinateXSquarePointB + difference);
                }
            }
        }
        return column;
    }

    private static List<Double> calculateColumns(int columnCount, Map<String, Point> squarePoints) {

        List<Double> column = new ArrayList<>();


        double coordinateYSquarePointA = squarePoints.get("A").y;
        double coordinateYSquarePointB = squarePoints.get("B").y;
        double coordinateYSquarePointC = squarePoints.get("C").y;
        double coordinateYSquarePointD = squarePoints.get("D").y;

        for (int i = 0; i < 10; i++) {
            double difference;
            if (columnCount == 0) {
                difference = Math.abs(coordinateYSquarePointC - coordinateYSquarePointA);
                difference = difference / 9 * i;

                column.add(coordinateYSquarePointA + difference);
            }

            if (columnCount == 11) {
                difference = Math.abs(coordinateYSquarePointB - coordinateYSquarePointD);
                difference = difference / 9 * i;

                column.add(coordinateYSquarePointB + difference);
            }

        }

        return column;
    }

    private static List<Point> calculateAllPoints(List<Double> firstRow, List<Double> firstColumn, List<Double> lastRow, List<Double> lastColumn) {

        List<Point> points = new ArrayList<>();

        double diffX, diffY;
        int x, y;

        for (int row = 0; row < 10; row++) {
            for (int column = 0; column < 12; column++) {
                diffX = lastRow.get(row) - firstRow.get(row);
                diffY = lastColumn.get(row) - firstColumn.get(row);
                diffX = (diffX / 11 * column);
                diffY = (diffY / 11 * column);
                x = (int) Math.abs(firstRow.get(row) + diffX);
                if (firstColumn.get(row) > lastColumn.get(row)) {
                    y = (int) Math.abs(firstColumn.get(row) - diffY);
                } else {
                    y = (int) Math.abs(firstColumn.get(row) + diffY);
                }
                points.add(new Point(x, y));
            }
        }

        return points;
    }

}