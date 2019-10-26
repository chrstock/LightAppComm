package com.example.chrstock.lightappcomm.imagemanagement.impl;

import android.support.annotation.NonNull;

import com.example.chrstock.lightappcomm.imagemanagement.api.ImageProcessor;
import com.example.chrstock.lightappcomm.model.LineTo;
import com.example.chrstock.lightappcomm.temp.MicrocontrollerBitUtils;
import com.example.chrstock.lightappcomm.utils.AsciiConverterUtils;
import com.example.chrstock.lightappcomm.utils.CustomUtils;
import com.example.chrstock.lightappcomm.utils.LineUtils;
import com.google.common.collect.Lists;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ImageProcessorImpl implements ImageProcessor {

    public String calculateSignal(List<Mat> mats) {
        List<String> pskSorted = new LinkedList<>();

        for (Mat mat : mats) {

            Map<Character, Point> squarePoints = extractSquarePoints(mat);
            List<Point> ledPoints = calculateLedPoints(squarePoints);
            String bits = MicrocontrollerBitUtils.calculateLightToBitSequence(ledPoints);
            String useBits = MicrocontrollerBitUtils.calculateUseBits(bits);

            String text = AsciiConverterUtils.convertToAscii(useBits);
            int position = MicrocontrollerBitUtils.calculateCountBit(bits);

            pskSorted.set(position, text);
        }
        return buildPsk(pskSorted);
    }

    @NonNull
    private String buildPsk(List<String> pskSorted) {
        return String.join("", pskSorted);
    }

    private Map<Character, Point> extractSquarePoints(Mat mat) {
        List<Point> coordinates = calculateCoordinates(mat);
        List<LineTo> lines = calculateLongestLines(coordinates);

        return LineUtils.determingPointsInSquare(lines);
    }

    private List<Point> calculateLedPoints(Map<Character, Point> squarePoints) {

        Point pointA = squarePoints.get('A');
        Point pointB = squarePoints.get('B');
        Point pointC = squarePoints.get('C');
        Point pointD = squarePoints.get('D');

        List<Double> firstRowPoints = calculateRowPoints(pointA, pointC);
        List<Double> firstColumnPoints = calculateColumnPoints(pointA, pointC);
        List<Double> lastRow = calculateRowPoints(pointB, pointD);
        List<Double> lastColumn = calculateColumnPoints(pointB, pointD);

        return calculateAllPoints(firstRowPoints, firstColumnPoints, lastRow, lastColumn);
    }

    private List<Point> calculateCoordinates(Mat mat) {

        Mat matHierarchy = new Mat();

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(mat, contours, matHierarchy, 0, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        Imgproc.drawContours(mat, contours, -1, new Scalar(255, 255, 255), -1);

        List<Point> detectedCoordinates = Collections.emptyList();

        for (MatOfPoint point : contours) {
            Moments moment = Imgproc.moments(point);
            int x = (int) (moment.get_m10() / moment.get_m00());
            int y = (int) (moment.get_m01() / moment.get_m00());
            detectedCoordinates.add(new Point(x, y));
        }

        matHierarchy.release();

        return detectedCoordinates;
    }

    private List<LineTo> calculateLongestLines(List<Point> points) {

        double currentLongestDistance = 0.0;
        double secondLongestDistance = 0.0;
        double distanceToCompare;

        LineTo longestLine = null;
        LineTo secondLongestLine = null;
        LineTo lineToCompare;

        for (Point point : points) {
            for (Point pointToCompare : points) {
                double comparedDistance = CustomUtils.calculateDistance(point, pointToCompare);

                if (comparedDistance > currentLongestDistance) {
                    lineToCompare = longestLine;
                    distanceToCompare = currentLongestDistance;

                    longestLine = new LineTo(point, pointToCompare);
                    currentLongestDistance = comparedDistance;
                } else {
                    lineToCompare = new LineTo(point, pointToCompare);
                    distanceToCompare = comparedDistance;
                }

                if (distanceToCompare > secondLongestDistance) {
                    secondLongestLine = lineToCompare;
                    secondLongestDistance = distanceToCompare;
                }
            }
        }
        return Lists.newArrayList(longestLine, secondLongestLine);
    }

    private List<Double> calculateRowPoints(Point pointLeft, Point pointRight) {
        List<Double> firstRow = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < NUMBER_OF_ROWS; rowIndex++) {
            double difference = Math.abs(pointLeft.x - pointRight.x);
            difference = difference / (NUMBER_OF_ROWS - 1) * rowIndex;

            firstRow.add((pointLeft.x) + difference);
        }
        return firstRow;
    }

    private List<Double> calculateColumnPoints(Point pointTop, Point pointBottom) {
        List<Double> columnPoints = new ArrayList<>();

        for (int colIndex = 0; colIndex < NUMBER_OF_COLS; colIndex++) {
            double difference = Math.abs(pointBottom.y - pointTop.y);
            difference = difference / (NUMBER_OF_COLS - 1) * colIndex;

            columnPoints.add(pointTop.y + difference);
        }
        return columnPoints;
    }

    private List<Point> calculateAllPoints(List<Double> firstRow, List<Double> firstColumn, List<Double> lastRow, List<Double> lastColumn) {

        List<Point> points = new ArrayList<>();

        for (int row = 0; row < NUMBER_OF_ROWS; row++) {
            for (int column = 0; column < NUMBER_OF_COLS; column++) {

                double diffX = lastRow.get(row) - firstRow.get(row);
                double diffY = lastColumn.get(row) - firstColumn.get(row);

                diffX = (diffX / 11 * column);
                diffY = (diffY / 11 * column);

                int x = (int) Math.abs(firstRow.get(row) + diffX);
                int y;
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