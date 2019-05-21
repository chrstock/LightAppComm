package com.example.chrstock.lightappcomm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.chrstock.lightappcomm.temp.ImageProcessorImpl;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ImageProcessorImplTest {

    private static final String FILE_RECORD_CLOSE = "fotonah.png";

    @Test
    public void checkThatResultIsNotNull(){
        Bitmap bitmapPhotoClose = getBitmap(FILE_RECORD_CLOSE);
        Mat mat = new Mat();

        Utils.bitmapToMat(bitmapPhotoClose,mat);

        String psk = ImageProcessorImpl.calculateSignal(Lists.newArrayList(mat));
        assertThat(psk).isNotNull();

    }

    @Test
    public void readBinaryFile() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(FILE_RECORD_CLOSE);
        assertThat(inputStream).isNotNull();

        Bitmap bmp = getBitmap(FILE_RECORD_CLOSE);

        assertThat(bmp).isNotNull();
    }

    private Bitmap getBitmap(String fileName){

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

        return BitmapFactory.decodeStream(inputStream);
    }
}
