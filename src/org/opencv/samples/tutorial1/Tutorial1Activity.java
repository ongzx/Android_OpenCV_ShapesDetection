package org.opencv.samples.tutorial1;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.view.WindowManager;
import android.view.View.OnTouchListener;

import static org.opencv.samples.tutorial1.R.id.tutorial1_activity_java_surface_view;

public class Tutorial1Activity extends Activity implements OnTouchListener, CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;

    private Button mCaptureBtn;
    private Button mSavedBtn;

    private Mat                    mRgba;
    private Mat                    mThresholdMat;
    private Mat                    mBlurMat;
    private Mat                    mIntermediateMat;
    private Mat                    mGray;
    private boolean              mIsTouch = false;
    private MatOfPoint points;
    Mat hierarchy;
    private MatOfPoint2f approxCurve;

    int duration = Toast.LENGTH_SHORT;
    String mMessage = "Detected ";
    Mat temp_contour;
    List<MatOfPoint> contours;

    private Mat                    touchedRegionRgba;
    private Mat                    touched_mThresholdMat;
    private Mat                    touched_mBlurMat;
    private Mat                    touched_mIntermediateMat;
    private Mat                    touched_mGray;
    private MatOfPoint touched_points;
    private MatOfPoint2f touched_approxCurve;
    Mat touched_hierarchy;
    List<MatOfPoint> touched_contours;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(Tutorial1Activity.this);

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public Tutorial1Activity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.tutorial1_surface_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(tutorial1_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mCaptureBtn = (Button) findViewById(R.id.scan);
        mSavedBtn = (Button) findViewById(R.id.saved);

        mSavedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent action = new Intent(v.getContext(), SavedActions.class);
                startActivity(action);
            }
        });

        mCaptureBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

//                AnalyseFrame analyseFrame = new AnalyseFrame();
//                analyseFrame.execute();

            }
        });

        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        touched_mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
        hierarchy = new Mat();
        touched_hierarchy = new Mat();

    }

    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        mIntermediateMat.release();
        touched_mIntermediateMat.release();
        hierarchy.release();
        touched_hierarchy.release();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Log.v("TOUCH", "touching...");

        int cols = mRgba.cols();
        int rows = mRgba.rows();

        int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;
        int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;

        int x = (int)event.getX() - xOffset;
        int y = (int)event.getY() - yOffset;

        Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");

        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

        Rect touchedRect = new Rect();

        touchedRect.x = (x>4) ? x-4 : 0;
        touchedRect.y = (y>4) ? y-4 : 0;

        touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
        touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

        touchedRegionRgba = mRgba.submat(touchedRect);

        touched_contours = new ArrayList<MatOfPoint>();
        touched_hierarchy = new Mat();
        touched_approxCurve = new MatOfPoint2f();
        touched_mThresholdMat = new Mat();
        touched_mBlurMat = new Mat();

        Imgproc.GaussianBlur(mRgba, touched_mThresholdMat, new Size(3, 3), 0);
        Imgproc.Canny(touched_mThresholdMat, touched_mIntermediateMat, 80, 100);
        Imgproc.findContours(touched_mIntermediateMat, touched_contours, touched_hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

        touched_hierarchy.release();

        if (touched_contours.size() > 0) {


            for (int i = 0; i < touched_contours.size(); i++) {

                MatOfPoint2f contour2f = new MatOfPoint2f(touched_contours.get(i).toArray());
                double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
                Imgproc.approxPolyDP(contour2f, touched_approxCurve, approxDistance, true);


                if (!touched_contours.get(i).empty()) {

                    Mat touched_contour = touched_contours.get(i);

                    double contourArea = Imgproc.contourArea(touched_contour);

                    MatOfPoint mat = new MatOfPoint();
                    touched_approxCurve.convertTo(mat, CvType.CV_32S);

                    if (contourArea < 500 || !Imgproc.isContourConvex(mat)) {
                        continue;
                    }

                    touched_points = new MatOfPoint(touched_approxCurve.toArray());

                    Log.v("TOUCHED POINT", String.valueOf(touched_points.cols()));

                    if (touched_points.toArray().length == 3) {

                        Log.v("TOUCHED POINT", String.valueOf(touched_points.cols()));

                        detectShape(touched_points.toArray().length);
                    } else if (touched_points.toArray().length == 4) {
                        Log.v("TOUCHED POINT", touched_points.toString());

                        detectShape(touched_points.toArray().length);
                    } else if (touched_points.toArray().length == 5) {
                        Log.v("TOUCHED POINT", touched_points.toString());

                        detectShape(touched_points.toArray().length);
                    } else if (touched_points.toArray().length > 6) {
                        Log.v("TOUCHED POINT", touched_points.toString());
                        detectShape(touched_points.toArray().length);
                    }
                }
            }

            if (ActionList.getInstance().getData().size() > 0) {
                Intent action = new Intent(v.getContext(), SavedActions.class);
                startActivity(action);
            } else {
                showMessage("Nothing detected!");
            }
        }

        touchedRegionRgba.release();

        return false; // don't need subsequent touch events
    }

//    public class AnalyseFrame extends AsyncTask<CvCameraViewFrame, Void, Void> {
//
//        @Override
//        protected Void doInBackground(CvCameraViewFrame... inputFrame) {
////
//
////            contours = new ArrayList<MatOfPoint>();
////            hierarchy = new Mat();
////            approxCurve = new MatOfPoint2f();
////            mThresholdMat = new Mat();
////            mBlurMat = new Mat();
////
////            Imgproc.GaussianBlur(mRgba, mThresholdMat, new Size(3, 3), 0);
////            Imgproc.Canny(mThresholdMat, mIntermediateMat, 80, 100);
////            Imgproc.findContours(mIntermediateMat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
////
////            hierarchy.release();
//
////            if (contours.size() > 0) {
////
////                for(int i = 0; i < contours.size(); i++) {
////                    MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(i).toArray());
////                    double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
////                    Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
////
////                    Mat contour = contours.get(i);
////
////                    double contourArea = Imgproc.contourArea(contour);
////
////                    MatOfPoint mat = new MatOfPoint();
////                    approxCurve.convertTo(mat, CvType.CV_32S);
////
////                    if (contourArea < 500 || !Imgproc.isContourConvex(mat)) {
////                        continue;
////                    }
////
////                    MatOfPoint points = new MatOfPoint(approxCurve.toArray());
////                    detectShape(points.toArray().length);
////
////                }
////
////            } else {
////                detectShape(-1);
////            }
//
//            if (contours.size() > 0) {
//
//                for(int i = 0; i < contours.size(); i++)
//                {
//
//                    MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(0).toArray());
//                    double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
//                    Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
//
//                    if (!contours.get(0).empty()) {
//
//                        temp_contour = contours.get(0);
//
//                        double contourArea = Imgproc.contourArea(temp_contour);
//
//                        MatOfPoint mat = new MatOfPoint();
//                        approxCurve.convertTo(mat, CvType.CV_32S);
//
//                        if (contourArea < 500 || !Imgproc.isContourConvex(mat)) {
//                            continue;
//                        }
//
//                        points = new MatOfPoint(approxCurve.toArray());
//
//                        if (points.toArray().length == 3) {
//                            detectShape(points.toArray().length);
//                        } else if (points.toArray().length == 4) {
//                            detectShape(points.toArray().length);
//                        } else if (points.toArray().length == 5) {
//                            detectShape(points.toArray().length);
//                        } else if (points.toArray().length > 6) {
//                            detectShape(points.toArray().length);
//                        }
//                    }
//
//                }
//
//            }
//
//            return null;
//        }
//
//
//    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();

        contours = new ArrayList<MatOfPoint>();
        hierarchy = new Mat();
        approxCurve = new MatOfPoint2f();
        mThresholdMat = new Mat();
        mBlurMat = new Mat();

        Imgproc.GaussianBlur(mRgba, mThresholdMat, new Size(3, 3), 0);
        Imgproc.Canny(mThresholdMat, mIntermediateMat, 80, 100);
        Imgproc.findContours(mIntermediateMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

        hierarchy.release();

        if (contours.size() > 0) {

            for(int i = 0; i < contours.size(); i++)
            {
                MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(i).toArray());
                double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
                Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);


                if (!contours.get(i).empty()){

                    temp_contour = contours.get(i);

                    double contourArea = Imgproc.contourArea(temp_contour);

                    MatOfPoint mat = new MatOfPoint();
                    approxCurve.convertTo(mat, CvType.CV_32S);

                    if (contourArea < 500 || !Imgproc.isContourConvex(mat)) {
                        continue;
                    }

                    points = new MatOfPoint(approxCurve.toArray());

                    if (points.toArray().length == 3) {

                        Log.v("POINTS", String.valueOf(points.toArray()));

                        Imgproc.drawContours(mRgba, contours, i, new Scalar(227, 43, 51), 3);
                    } else if (points.toArray().length == 4) {

                        Log.v("POINTS", String.valueOf(points.toArray()));

                        Imgproc.drawContours(mRgba, contours, i, new Scalar(227, 43, 51), 3);
                    } else if (points.toArray().length == 5) {

                        Log.v("POINTS", String.valueOf(points.toArray()));

                        Imgproc.drawContours(mRgba, contours, i, new Scalar(227, 43, 51), 3);
                    } else if (points.toArray().length > 6) {

                        Log.v("POINTS", String.valueOf(points.toArray()));

                        Imgproc.drawContours(mRgba, contours, i, new Scalar(227, 43, 51), 3);
                    }

                }

            }
        }

        return mRgba;

    }

    private void detectShape(int pointLength) {

        if (pointLength == 3) {
            mMessage = "Triangle";
            ActionList.setData(mMessage);
        } else if (pointLength == 4) {
            mMessage = "Square";
            ActionList.setData(mMessage);
        } else if (pointLength == 5) {
            mMessage = "Pentagon";
            ActionList.setData(mMessage);
        } else if (pointLength > 6) {
            mMessage = "Circle";
            ActionList.setData(mMessage);
        } else {
            mMessage = "Nothing detected!";
        }

        //showMessage(mMessage);

    }

    private void showMessage(final CharSequence message) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

    }


}
