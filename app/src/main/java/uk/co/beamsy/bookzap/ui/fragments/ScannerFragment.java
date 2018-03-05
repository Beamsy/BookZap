package uk.co.beamsy.bookzap.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import uk.co.beamsy.bookzap.BookZap;
import uk.co.beamsy.bookzap.R;



public class ScannerFragment extends Fragment implements Detector.Processor<Barcode> {
    private SurfaceView cameraView;
    private TextView barcodeInfo;
    private static ScannerFragment fragment;
    private CameraSource cameraSource;

    public ScannerFragment() {

    }

    public static ScannerFragment getInstance() {
        if (fragment == null){
            fragment = new ScannerFragment();
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflator.inflate(R.layout.fragment_scanner, container, false);
        BookZap mainActivity = (BookZap) getActivity();
        mainActivity.setTitle("Scan a barcode");
        mainActivity.changeDrawerBack(true);
        cameraView = rootView.findViewById(R.id.camera_view);
        barcodeInfo = rootView.findViewById(R.id.barcode_info);


        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(rootView.getContext())
                //.setBarcodeFormats(Barcode.ISBN)
                .build();
        barcodeDetector.setProcessor(this);

        cameraSource = new CameraSource
                .Builder(rootView.getContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600, 1600)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @SuppressLint("MissingPermission")
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if(getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(cameraView.getHolder());
                    }
                } catch (IOException ie) {
                    Log.e("CAMERA SOURCE", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });



        return rootView;
    }

    public void changeInfo (String newInfo) {
        barcodeInfo.setText(newInfo);
    }

    @Override
    public void release() {
        cameraSource.release();
    }

    @Override
    public void receiveDetections(Detector.Detections<Barcode> detections) {
        if ( detections.getDetectedItems().size() > 0) {
            Log.d("Barcode: ", "barcode detected!");
        }
        SparseArray<Barcode> detectedItems = detections.getDetectedItems();
        for (int i = 0; i < detectedItems.size() ; i++) {
            int key = detectedItems.keyAt(i);
            Log.d("Barcode: ", detections.getDetectedItems().get(key).displayValue);
        }
    }
}
