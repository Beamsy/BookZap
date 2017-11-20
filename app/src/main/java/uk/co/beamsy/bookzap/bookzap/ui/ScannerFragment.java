package uk.co.beamsy.bookzap.bookzap.ui;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import uk.co.beamsy.bookzap.bookzap.BookZap;
import uk.co.beamsy.bookzap.bookzap.R;

/**
 * Created by Jake on 20/11/2017.
 */

public class ScannerFragment extends Fragment {
    private SurfaceView cameraView;
    private TextView barcodeInfo;

    public ScannerFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflator.inflate(R.layout.fragment_scanner, container, false);
        cameraView = (SurfaceView) rootView.findViewById(R.id.camera_view);
        barcodeInfo = (TextView) rootView.findViewById(R.id.barcode_info);
        if (ActivityCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
           ActivityCompat.requestPermissions(
                   this.getActivity(),
                   new String[]{Manifest.permission.CAMERA},
                   rootView.getContext().getResources().getInteger(R.integer.PERMISSION_REQUEST_CAMERA));
        }

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(rootView.getContext())
                .setBarcodeFormats(Barcode.ISBN)
                .build();
        final CameraSource cameraSource = new CameraSource
                .Builder(rootView.getContext(), barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(cameraView.getWidth(), cameraView.getHeight())
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {

                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ie) {
                    Log.e("CAMERA SOURCE", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });





        return rootView;
    }


}
