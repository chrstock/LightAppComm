package com.example.chrstock.lightappcomm.temp;

import android.support.annotation.NonNull;

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

import java.util.Collections;
import java.util.List;

public class ImageProcessorImpl implements ImageProcessor {

    public String calculateSignal(List<Mat> mats) {

        List<String> pskSorted = Collections.emptyList();

        for (Mat mat : mats) {

            List<Point> squarePoints = extractSquarepoints(mat);

            List<Point> ledPoints = calculateLedPoints(squarePoints);

            String bits = MicrocontrollerBitUtils.calculateLightToBitSequence(ledPoints);

            String useBits = MicrocontrollerBitUtils.calculateUseBits(bits);

            sortPsk(pskSorted, bits, useBits);

        }

        return buildPsk(pskSorted);
    }

    @NonNull
    private String buildPsk(List<String> pskSorted) {
        return String.join("", pskSorted);
    }

    private void sortPsk(List<String> pskSorted, String bits, String useBits) {
        String psk = AsciiConverterUtils.convertToAscii(useBits);

        int pskOrder = MicrocontrollerBitUtils.calculateCountBit(bits);

        pskSorted.set(pskOrder, psk);
    }

    private List<Point> extractSquarepoints(Mat mat) {
        List<Point> coordinates = calculateBoundingBoxCenter(mat);
        List<LineTo> lines = calculateDiagonals(coordinates);

        return LineUtils.determingPointsInSquare(lines);
    }

    private List<Point> calculateLedPoints(List<Point> squarePoints) {

        List<Double> firstRow = calculateFirstRow(squarePoints);
        List<Double> firstColumn = calculateFirstColumn(squarePoints);
        List<Double> lastRow = calculateLastRow(squarePoints);
        List<Double> lastColumn = calculateLastColumn(squarePoints);

        return calculateAllPoints(firstRow, firstColumn, lastRow, lastColumn);
    }

    private List<Point> calculateBoundingBoxCenter(Mat mat) {

        Mat matHierarchy = new Mat();

        List<MatOfPoint> contours = Collections.emptyList();
        Imgproc.findContours(mat, contours, matHierarchy, 0, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        Imgproc.drawContours(mat, contours, -1, new Scalar(255, 255, 255), -1);

        List<Point> detectedPoints = Collections.emptyList();

        for (MatOfPoint point : contours) {
            Moments moment = Imgproc.moments(point);
            int x = (int) (moment.get_m10() / moment.get_m00());
            int y = (int) (moment.get_m01() / moment.get_m00());
            detectedPoints.add(new Point(x, y));
        }

        matHierarchy.release();

        return detectedPoints;
    }

    private List<LineTo> calculateDiagonals(List<Point> points) {

        int numberOfPoints = points.size();
        double distance;
        double firstBiggestDistance = 0.0;
        double secondBiggestDistance = 0.0;

        List<LineTo> lines = Collections.emptyList();

        LineTo longestLine = calculateLongestLine(points);

        for (int indexStartPoint = 0; indexStartPoint < (numberOfPoints - 1); indexStartPoint++) {
            Point pointStart = points.get(indexStartPoint);
            for (int indexEndPoint = (indexStartPoint + 1); indexEndPoint < numberOfPoints; indexEndPoint++) {
                Point pointEnd = points.get(indexEndPoint);
                distance = CustomUtils.calculateDistance(pointStart, pointEnd);


                if (distance > firstBiggestDistance) {

                    if (firstBiggestDistance != 0.0)
                        lines.add(1, lines.get(0));

                    lines.add(0, new LineTo(pointStart, pointEnd));
                    secondBiggestDistance = firstBiggestDistance;
                    firstBiggestDistance = distance;
                } else if (distance > secondBiggestDistance) {
                    lines.add(1, new LineTo(pointStart, pointEnd));
                    secondBiggestDistance = distance;
                }

            }
        }
        return lines;
    }

    private LineTo calculateLongestLine(List<Point> points){

        int numberOfPoints = points.size();

        double firstBiggestDistance = 0.0;

        LineTo longestLine = null;

        for (int indexStartPoint = 0; indexStartPoint < (numberOfPoints - 1); indexStartPoint++) {
            Point pointStart = points.get(indexStartPoint);
            for (int indexEndPoint = (indexStartPoint + 1); indexEndPoint < numberOfPoints; indexEndPoint++) {
                Point pointEnd = points.get(indexEndPoint);
                double distance = CustomUtils.calculateDistance(pointStart, pointEnd);

                if (distance > firstBiggestDistance) {

                    if (firstBiggestDistance != 0.0)
                    longestLine = new LineTo(pointStart,pointEnd);
                    firstBiggestDistance = distance;
                }

            }
        }
        return longestLine;
    }


    private List<Double> calculateFirstRow(List<Point> squarePoints) {

        List<Double> firstRow = Collections.emptyList();

        double coordinateXSquarePointA = squarePoints.get(0).x;
        double coordinateXSquarePointC = squarePoints.get(2).x;

        for (int rowIndex = 0; rowIndex < NUMBER_OF_ROWS; rowIndex++) {
            double difference = Math.abs(coordinateXSquarePointA - coordinateXSquarePointC);

            difference = difference / (NUMBER_OF_ROWS - 1) * rowIndex;

            if (coordinateXSquarePointA > coordinateXSquarePointC) {
                firstRow.add((coordinateXSquarePointA) - difference);
            } else {
                firstRow.add((coordinateXSquarePointA) + difference);
            }


        }
        return firstRow;
    }

    private List<Double> calculateLastRow(List<Point> squarePoints) {
        List<Double> lastRow = Collections.emptyList();

        double coordinateXSquarePointB = squarePoints.get(1).x;
        double coordinateXSquarePointD = squarePoints.get(3).x;

        for (int rowIndex = 0; rowIndex < NUMBER_OF_ROWS; rowIndex++) {
            double difference = Math.abs(coordinateXSquarePointB - coordinateXSquarePointD);
            difference = difference / (NUMBER_OF_ROWS - 1) * rowIndex;

            if (coordinateXSquarePointB > coordinateXSquarePointD) {
                lastRow.add(coordinateXSquarePointB - difference);
            } else {
                lastRow.add(coordinateXSquarePointB + difference);
            }

        }
        return lastRow;
    }

    private List<Double> calculateLastColumn(List<Point> squarePoints) {

        List<Double> lastColumn = Collections.emptyList();

        double coordinateYSquarePointB = squarePoints.get(1).y;
        double coordinateYSquarePointD = squarePoints.get(3).y;

        for (int colIndex = 0; colIndex < NUMBER_OF_COLS; colIndex++) {
            double difference;

            difference = Math.abs(coordinateYSquarePointB - coordinateYSquarePointD);
            difference = difference / (NUMBER_OF_COLS - 1) * colIndex;

            lastColumn.add(coordinateYSquarePointB + difference);

        }

        return lastColumn;
    }

    private List<Double> calculateFirstColumn(List<Point> squarePoints) {

        List<Double> firstColumn = Collections.emptyList();

        double coordinateYSquarePointA = squarePoints.get(0).y;
        double coordinateYSquarePointC = squarePoints.get(2).y;

        for (int colIndex = 0; colIndex < NUMBER_OF_COLS; colIndex++) {
            double difference;
            difference = Math.abs(coordinateYSquarePointC - coordinateYSquarePointA);
            difference = difference / (NUMBER_OF_COLS - 1) * colIndex;

            firstColumn.add(coordinateYSquarePointA + difference);

        }

        return firstColumn;
    }

    private List<Point> calculateAllPoints(List<Double> firstRow, List<Double> firstColumn, List<Double> lastRow, List<Double> lastColumn) {

        List<Point> points = Collections.emptyList();

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