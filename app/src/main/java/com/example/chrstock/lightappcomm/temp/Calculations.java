package com.example.chrstock.lightappcomm.temp;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.chrstock.lightappcomm.model.LineTo;
import com.example.chrstock.lightappcomm.utils.AsciiConverterUtils;
import com.example.chrstock.lightappcomm.utils.CustomUtils;

import org.opencv.android.Utils;
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

    private List<LineTo> lineList = new ArrayList<>();
    private Map<String, Point> squarePoints = new HashMap<>();

    private List<Double> firstRow;
    private List<Double> firstColumn;
    private List<Double> lastRow;
    private List<Double> lastColumn;

    private String countBit;

    public String calculateSignal(List<Mat> mats, List<Bitmap> bitmaps) {
        int pskOrder;

        String psk;
        String bits;

        List<String> pskSorted = new ArrayList<>();
        int count = 0;

        for (Mat mat : mats) {

            pskOrder = 6;

            List<Point> coordinates = calculateBoundingBoxCenter(mat);
            calculateAllDistances(coordinates);

            determingPointsInSquare();

            firstRow = calculateRows(0);
            firstColumn = calculateColumns(0);
            lastRow = calculateRows(11);
            lastColumn = calculateColumns(11);

            //comment

            List <Point> points = calculateAllPoints();
            //

            bits = calculateLightToBitSequence(mat, bitmaps.get(count++),points);

            if (countBit.charAt(0) == '1') pskOrder = 0;
            if (countBit.charAt(1) == '1') pskOrder = 1;
            if (countBit.charAt(2) == '1') pskOrder = 2;
            if (countBit.charAt(3) == '1') pskOrder = 3;
            if (countBit.charAt(4) == '1') pskOrder = 4;
            if (countBit.charAt(5) == '1') pskOrder = 5;

            psk = AsciiConverterUtils.convertToAscii(bits);

            pskSorted.add(pskOrder, psk);

        }
        psk = "        ";

        for (int i = 0; i < pskSorted.size(); i++) {
            psk = psk + " " + pskSorted.get(i);
        }

        return psk;

    }

    private List<Point> calculateBoundingBoxCenter(Mat mat) {

        int x;
        int y;

        List<Point> detectedPoints = new ArrayList<>();

        Mat matHierarchy = new Mat();

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(mat, contours, matHierarchy, 0, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        Imgproc.drawContours(mat, contours, -1, new Scalar(255, 255, 255), -1);

        List<Moments> moments = new ArrayList<>(contours.size());
        for (int i = 0; i < contours.size(); i++) {
            Moments moment = moments.get(i);
            x = (int) (moment.get_m10() / moment.get_m00());
            y = (int) (moment.get_m01() / moment.get_m00());
            detectedPoints.add(new Point(x, y));
        }

        matHierarchy.release();

        return detectedPoints;
    }

    private void calculateAllDistances(List<Point> points) {

        int amount = points.size();
        double distance;
        double firstBiggestDistance = 0.0;
        double secondBiggestDistance = 0.0;

        for (int i = 0; i < (amount - 1); i++) {
            Point pointStart = points.get(i);
            for (int j = (i + 1); j < amount; j++) {
                Point pointEnd = points.get(j);
                distance = CustomUtils.calculateDistance(pointStart, pointEnd);
                if (distance > firstBiggestDistance) {

                    if (firstBiggestDistance != 0.0)
                        lineList.add(1, lineList.get(0));

                    lineList.add(0, new LineTo(pointStart, pointEnd));
                    secondBiggestDistance = firstBiggestDistance;
                    firstBiggestDistance = distance;
                } else {
                    if (distance > secondBiggestDistance) {
                        lineList.add(1, new LineTo(pointStart, pointEnd));
                        secondBiggestDistance = distance;
                    }
                }
            }
        }
    }

    private void determingPointsInSquare() {

        double nearDistanceOrigin = 2000.0;
        double farDistanceOrigin = 0.0;
        double distance;

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


    }

    private List<Double> calculateRows(int rowCount) {
        List<Double> column = new ArrayList<>();

        double coordinateXSquarePointA;

        if(squarePoints.containsKey("A")&squarePoints.get("A")!=null){
            coordinateXSquarePointA = squarePoints.get("A").x;
        }else{
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

    private List<Double> calculateColumns(int columnCount) {

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

    private List<Point> calculateAllPoints() {

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


    private String calculateLightToBitSequence(Mat mat, Bitmap bmp, List<Point> points) {

        int x, y;
        int pixel;
        String bits = "";
        String useBits = "";
        countBit = "";

        Utils.matToBitmap(mat, bmp);

        for (int i = 0; i < points.size(); i++) {

            x = (int) points.get(i).x;
            y = (int) points.get(i).y;

            pixel = bmp.getPixel(x, y);

            if (pixel != Color.BLACK) {
                bits += "1";
            } else {
                bits += "0";
            }
        }

        countBit += bits.charAt(24);
        countBit += bits.charAt(36);
        countBit += bits.charAt(48);
        countBit += bits.charAt(60);
        countBit += bits.charAt(72);
        countBit += bits.charAt(84);

        useBits += bits.substring(14, 21);
        useBits += bits.substring(26, 33);
        useBits += bits.substring(38, 45);
        useBits += bits.substring(50, 57);
        useBits += bits.substring(62, 69);
        useBits += bits.substring(74, 81);
        useBits += bits.substring(86, 93);
        useBits += bits.substring(98, 105);

        return useBits;
    }

}