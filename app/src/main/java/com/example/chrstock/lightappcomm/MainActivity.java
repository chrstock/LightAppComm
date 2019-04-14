package com.example.chrstock.lightappcomm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chrstock.lightappcomm.temp.Calculations;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_VIDEO_CAPTURE = 1;
    public static final int RECORD_TIME = 5;

    private Uri videoUri;
    private List<Bitmap> bitmaps;

    private Button buttonRecord;
    private Button buttonProcess;
    private Button buttonCalculate;

    private TextView textView;

    public void recordButton(View view) {
        dispatchTakeVideoIntent();
        showOnlyProcessButton();
    }

    private void initializeView() {
        buttonRecord = findViewById(R.id.button_record_video);
        buttonProcess = findViewById(R.id.button_process_video);
        buttonCalculate = findViewById(R.id.button_calculate_video);
        textView = findViewById(R.id.textView);

        showOnlyCalculateButton();
    }

    public void processButton(View view) {
        this.bitmaps = getFrames(this.videoUri);
        showOnlyCalculateButton();
    }

    public void calculateButton(View view) {

        Bitmap bitmapTemp = BitmapFactory.decodeResource(getResources(),R.drawable.fotonah);

        Mat src = new Mat(bitmapTemp.getHeight(),bitmapTemp.getWidth(),CvType.CV_8UC1);
        Utils.bitmapToMat(bitmapTemp,src);
        src.convertTo(src,CvType.CV_8U);

        List<Mat> matsNew = produceMats(src,bitmapTemp);

        String text = Calculations.calculateSignal(matsNew);
        textView.setText(text);
        showOnlyRecordButton();
    }

    private List<Mat> produceMats(Mat src, Bitmap bitmap){

        Mat hsvImage = new Mat();

        Mat newSrc = new Mat();
        src.convertTo(newSrc,CvType.CV_8UC1);

        Imgproc.cvtColor(newSrc, hsvImage, Imgproc.COLOR_BGR2HSV);

        Mat maskedImage = new Mat();

        Core.inRange(hsvImage, new Scalar(0, 50, 40), new Scalar(10, 255, 255), maskedImage);

        List<Mat> mats = new ArrayList<>();
        mats.add(hsvImage);
        return mats;

    }

    private List<Bitmap> getFrames(Uri videoUri) {
        List<Bitmap> bitmaps = new ArrayList<>();

        if (videoUri != null) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, this.videoUri);

            for (int i = 0; i < RECORD_TIME * 1000000; i += 1000000) {
                Bitmap bitmap = retriever.getFrameAtTime(i);
                bitmaps.add(bitmap);
                Log.i(this.getClass().getSimpleName(), "Bitmap " + i / 1000000 + "second added ");
            }
        }

        return bitmaps;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeView();

        checkOnOpenCV();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            this.videoUri = intent.getData();
            buttonProcess.setVisibility(View.VISIBLE);
        }
    }


    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, RECORD_TIME);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void checkOnOpenCV() {
        if (!OpenCVLoader.initDebug()) {
            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");
        } else {
            Log.i(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
        }
    }

    private void showOnlyRecordButton() {
        buttonRecord.setVisibility(View.VISIBLE);
        buttonCalculate.setVisibility(View.INVISIBLE);
        buttonProcess.setVisibility(View.INVISIBLE);
    }

    private void showOnlyProcessButton() {
        buttonRecord.setVisibility(View.INVISIBLE);
        buttonCalculate.setVisibility(View.INVISIBLE);
        buttonProcess.setVisibility(View.VISIBLE);
    }

    private void showOnlyCalculateButton() {
        buttonRecord.setVisibility(View.INVISIBLE);
        buttonCalculate.setVisibility(View.VISIBLE);
        buttonProcess.setVisibility(View.INVISIBLE);
    }
}
