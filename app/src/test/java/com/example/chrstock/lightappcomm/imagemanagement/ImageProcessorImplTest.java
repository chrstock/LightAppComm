package com.example.chrstock.lightappcomm.imagemanagement;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.chrstock.lightappcomm.config.ComponentTest;

import org.junit.Test;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ImageProcessorImplTest extends ComponentTest {

    private static final String FILE_RECORD_CLOSE = "fotonah.png";

    @Test
    public void checkThatResultIsNotNull(){
        //Bitmap bitmapPhotoClose = getBitmap(FILE_RECORD_CLOSE);
        //Mat mat = new Mat();

        //Utils.bitmapToMat(bitmapPhotoClose,mat);

        String psk = getImageProcessor().calculateSignal(null);
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
