package com.example.chrstock.lightappcomm.temp;

import com.example.chrstock.lightappcomm.model.LineTo;
import com.example.chrstock.lightappcomm.utils.AsciiConverterUtils;
import com.example.chrstock.lightappcomm.utils.CustomUtils;
import com.example.chrstock.lightappcomm.utils.LineUtils;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.List;

public class Calculations {

    public static String calculateSignal(List<Mat> mats) {

        List<String> pskSorted = new ArrayList<>();

        for (Mat mat : mats) {

            List<Point> coordinates = calculateBoundingBoxCenter(mat);
            List<LineTo> lines = calculateAllDistances(coordinates);

            List<Point> squarePoints = LineUtils.determingPointsInSquare(lines);

            List<Double> firstRow = calculateRows(0, squarePoints);
            List<Double> firstColumn = calculateColumns(0, squarePoints);
            List<Double> lastRow = calculateRows(11, squarePoints);
            List<Double> lastColumn = calculateColumns(11, squarePoints);

            List<Point> points = calculateAllPoints(firstRow, firstColumn, lastRow, lastColumn);

            String bits = MicrocontrollerBitUtils.calculateLightToBitSequence(mat, mat, points);

            int pskOrder = MicrocontrollerBitUtils.calculateCountBit(bits);

            String useBits = MicrocontrollerBitUtils.calculateUseBits(bits);

            String psk = AsciiConverterUtils.convertToAscii(useBits);

            pskSorted.set(pskOrder, psk);

        }

        return String.join("", pskSorted);
    }

    private static List<Point> calculateBoundingBoxCenter(Mat mat) {

        Mat matHierarchy = new Mat();

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(mat, contours, matHierarchy, 0, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        Imgproc.drawContours(mat, contours, -1, new Scalar(255, 255, 255), -1);

        List<Point> detectedPoints = new ArrayList<>();

        for (MatOfPoint point : contours) {
            Moments moment = Imgproc.moments(point);
            int x = (int) (moment.get_m10() / moment.get_m00());
            int y = (int) (moment.get_m01() / moment.get_m00());
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


    private static List<Double> calculateRows(int rowCount, List<Point> squarePoints) {
        List<Double> column = new ArrayList<>();

        double coordinateXSquarePointA = squarePoints.get(0).x;
        double coordinateXSquarePointB = squarePoints.get(1).x;
        double coordinateXSquarePointC = squarePoints.get(2).x;
        double coordinateXSquarePointD = squarePoints.get(3).x;

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

    private static List<Double> calculateColumns(int columnCount, List<Point> squarePoints) {

        List<Double> column = new ArrayList<>();

        double coordinateYSquarePointA = squarePoints.get(0).y;
        double coordinateYSquarePointB = squarePoints.get(1).y;
        double coordinateYSquarePointC = squarePoints.get(2).y;
        double coordinateYSquarePointD = squarePoints.get(3).y;

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
        int y;

        for (int row = 0; row < 10; row++) {
            for (int column = 0; column < 12; column++) {

                diffX = lastRow.get(row) - firstRow.get(row);
                diffY = lastColumn.get(row) - firstColumn.get(row);

                diffX = (diffX / 11 * column);
                diffY = (diffY / 11 * column);

                int x = (int) Math.abs(firstRow.get(row) + diffX);

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