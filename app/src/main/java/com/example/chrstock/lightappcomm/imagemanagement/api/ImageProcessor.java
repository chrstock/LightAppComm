package com.example.chrstock.lightappcomm.imagemanagement.api;

import org.opencv.core.Mat;

import java.util.List;

public interface ImageProcessor {

    int NUMBER_OF_ROWS = 10;
    int NUMBER_OF_COLS = 12;

    /**
     * Calulcates the Message from the Signal
     *
     * @param mats Liste of {@link Mat}
     * @return Message
     */
    String calculateSignal(List<Mat> mats);
}
