package com.example.chrstock.lightappcomm.model;

import com.example.chrstock.lightappcomm.utils.CustomUtils;

import org.opencv.core.Point;

public class LineTo {

    private Point pointStart;

    private Point pointEnd;

    private String lineName;

    public LineTo(Point pointStart, Point pointEnd ) {
        this.pointStart = pointStart;
        this.pointEnd = pointEnd;
        this.lineName = "";
    }

    public LineTo(Point pointStart, Point pointEnd, String lineName) {
        this.pointStart = pointStart;
        this.pointEnd = pointEnd;
        this.lineName = lineName;
    }

    public Point getPointStart() {
        return pointStart;
    }

    public Point getPointEnd() {
        return pointEnd;
    }

    public Double getDistance() {
        return CustomUtils.calculateDistance(pointStart,pointEnd);
    }

    public String getLineName() {
        return lineName;
    }
}
