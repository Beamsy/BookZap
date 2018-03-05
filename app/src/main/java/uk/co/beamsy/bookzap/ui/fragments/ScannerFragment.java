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
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import uk.co.beamsy.bookzap.BookZap;
import uk.co.beamsy.bookzap.R;
import uk.co.beamsy.bookzap.connections.GoogleBooksConnection;
import uk.co.beamsy.bookzap.model.UserBook;


public class ScannerFragment extends Fragment implements Detector.Processor<Barcode>, GoogleBooksConnection.SingleSearchResultListener {
    private SurfaceView cameraView;
    private TextView barcodeInfo;
    private static ScannerFragment fragment;
    private CameraSource cameraSource;
    private boolean detected = false;

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
        detected = false;

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
        if ( detections.getDetectedItems().size() > 0 && !detected) {
            Log.d("Barcode: ", "barcode detected!");
            SparseArray<Barcode> detectedItems = detections.getDetectedItems();
            int key = detectedItems.keyAt(0);
            Barcode barcode = detectedItems.get(key);
            if (barcode.valueFormat != Barcode.ISBN) return;
            String isbn = barcode.displayValue;
            GoogleBooksConnection.searchSingle(isbn, getContext(), this);
            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((BookZap)getActivity()).showLoadingCircle();
                }
            });
            detected = true;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onSingleSearchResult(UserBook userBook) {
        ((BookZap)getActivity()).hideLoadingCircle();
        if (userBook == null) {
            detected = false;
            Toast.makeText(getContext(), "Book not found" ,Toast.LENGTH_SHORT).show();
            return;
        }
        BookFragment bookFragment = BookFragment.getInstance();
        bookFragment.setBook(userBook);
        ((BookZap)getActivity()).changeFragment(bookFragment, "book");
    }
}
