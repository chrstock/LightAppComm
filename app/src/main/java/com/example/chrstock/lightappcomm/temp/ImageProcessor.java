package com.example.chrstock.lightappcomm.temp;

import org.opencv.core.Mat;

import java.util.List;

import dagger.Component;

public interface ImageProcessor {

    int NUMBER_OF_ROWS = 10;
    int NUMBER_OF_COLS = 12;

    /**
     * Calulcates the Message from the Signal
     *
     * @param mats Liste of Mats
     * @return Message
     */
    String calculateSignal(List<Mat> mats);
}
