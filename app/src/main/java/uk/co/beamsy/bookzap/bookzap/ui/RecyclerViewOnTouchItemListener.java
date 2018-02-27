package uk.co.beamsy.bookzap.bookzap.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class RecyclerViewOnTouchItemListener implements RecyclerView.OnItemTouchListener{
    private OnTouchListener onTouchListener;

    public interface OnTouchListener {
        void onTap(View view, int adaptorPosition);
        void onHold(View view, int adaptorPosition);
    }

    private GestureDetector gestureDetector;

    public RecyclerViewOnTouchItemListener(Context context, final RecyclerView rv, final OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && onTouchListener != null) {
                    onTouchListener.onHold(child, rv.getChildAdapterPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && onTouchListener != null && gestureDetector.onTouchEvent(e)) {
            onTouchListener.onTap(childView, rv.getChildAdapterPosition(childView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
