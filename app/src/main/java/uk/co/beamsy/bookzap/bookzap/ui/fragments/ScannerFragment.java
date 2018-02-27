package uk.co.beamsy.bookzap.bookzap.ui.fragments;

import android.Manifest;
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

import uk.co.beamsy.bookzap.bookzap.BookZap;
import uk.co.beamsy.bookzap.bookzap.R;



public class ScannerFragment extends Fragment {
    private SurfaceView cameraView;
    private TextView barcodeInfo;
    private static ScannerFragment fragment;


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
                .setBarcodeFormats(Barcode.ISBN)
                .build();

        final CameraSource cameraSource = new CameraSource
                .Builder(rootView.getContext(), barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(250, 250)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
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
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            cameraSource.release();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    barcodeInfo.setText(barcodes.get(0).displayValue);
                }
            }
        });




        return rootView;
    }

}
