package nl.juanma.rxperiments;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * Created by juanma on 27/03/15.
 */
public class RxTouchView extends View {

    private final PublishSubject mTouchSubject = PublishSubject.create();
    private final Observable mTouches = mTouchSubject.asObservable();

    public RxTouchView(Context context) {
        super(context);
        init();
    }

    public RxTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RxTouchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RxTouchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        setOnTouchListener(new OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                mTouchSubject.onNext(event);
                return true;
            }
        });
    }


    private final Observable mDownObservable = mTouches.filter(new Func1<MotionEvent, Boolean>() {
        @Override public Boolean call(MotionEvent ev) {
            return ev.getActionMasked() == MotionEvent.ACTION_DOWN;
        }
    });

    private final Observable mUpObservable =
            mTouches.filter(new Func1<MotionEvent, Boolean>() {
                @Override public Boolean call(MotionEvent ev) {
                    return ev.getActionMasked() == MotionEvent.ACTION_UP;
                }
            });

    private final Observable mMovesObservable =
            mTouches.filter(new Func1<MotionEvent, Boolean>() {
                @Override public Boolean call(MotionEvent ev) {
                    return ev.getActionMasked() == MotionEvent.ACTION_MOVE;
                }
            });

}

