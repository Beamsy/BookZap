package uk.co.beamsy.bookzap.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

import uk.co.beamsy.bookzap.R;

/**
 * Created by bea17007261 on 28/02/2018.
 */

public class UpdateProgressDialog extends DialogFragment {

    private static UpdateProgressDialog updateProgressDialog;

    public interface UpdateProgressListener {
        public void updateProgress(long readTo);
    }

    UpdateProgressListener listener;

    public UpdateProgressDialog(){

    }

    public static UpdateProgressDialog getInstance(long readTo, long pageCount, UpdateProgressListener listener) {
        if (updateProgressDialog == null) {
            updateProgressDialog = new UpdateProgressDialog();
        }
        Bundle args = new Bundle();
        args.putLong("readTo", readTo);
        args.putLong("pageCount", pageCount);
        updateProgressDialog.listener = listener;
        updateProgressDialog.setArguments(args);
        return updateProgressDialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (listener ==  null) {
            throw new NullPointerException("No listener found");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final long pageCount = getArguments().getLong("pageCount");
        final long readTo = getArguments().getLong("readTo");
        View rootView = inflater.inflate(R.layout.dialog_update_progress, null);
        final SeekBar seekBar = rootView.findViewById(R.id.update_progress_bar);
        seekBar.setMax((int) pageCount);
        seekBar.setProgress((int) readTo);
        final EditText textBox = rootView.findViewById(R.id.update_progress_box);
        textBox.setFilters(new InputFilter[]{ new MinMaxFilter(0, (int) pageCount)});
        textBox.setText(Long.toString(readTo));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textBox.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        builder.setView(rootView)
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.updateProgress(seekBar.getProgress());
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
        });
        return builder.create();
    }

    private class MinMaxFilter implements InputFilter {

        private int minVal, maxVal;

        public MinMaxFilter (int minVal, int maxVal) {
            this.minVal = minVal;
            this.maxVal = maxVal;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String newVal = dest.toString().substring(0, dstart) + dest.toString().substring(dend, dest.toString().length());
            newVal = newVal.substring(0, dstart) + source.toString() + newVal.substring(dstart, newVal.length());
            int input = Integer.parseInt(newVal);
            if (isInRange(minVal, maxVal, input)) return null;
            return "";
        }

        private boolean isInRange (int min, int max, int input) {
            // Is min smaller than max
            // If so is input greater than oet min and less than oet max
            // Else check inverse for negative values.
            return min < max ? min <= input && max >= input : min >= input && max <= input;
        }
    }
}
