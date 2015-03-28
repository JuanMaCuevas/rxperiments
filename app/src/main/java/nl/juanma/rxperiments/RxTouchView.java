package nl.juanma.rxperiments;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import rx.Observable;
import rx.functions.Action1;
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

        setAfterDragSubscriber();
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


    public void setAfterDragSubscriber() {
        mDownObservable.subscribe(new Action1<MotionEvent>() {
            @Override public void call(MotionEvent o) {
                final Path path = new Path();
                path.moveTo(o.getX(), o.getY());
                Log.i("", "down");
                mMovesObservable
                        .takeUntil(mUpObservable.doOnNext(new Action1<MotionEvent>() {
                            @Override public void call(MotionEvent o) {
                                draw(path);
                                path.close();
                                Log.i("", "up");
                            }
                        }))
                        .subscribe(new Action1<MotionEvent>() {
                            @Override public void call(MotionEvent o) {
                                path.lineTo(o.getX(), getY());
                                draw(path);
                                Log.i("", "move");
                            }
                        });
            }
        });

    }

    private Bitmap CustomBitmap;
    private Canvas CustomCanvas;
    private Paint CustomPaint;

    private void draw(Path path) {
        if (CustomBitmap == null) {
            CustomBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            CustomCanvas = new Canvas(CustomBitmap);
            CustomPaint = new Paint();
            CustomPaint.setColor(Color.GREEN);
            CustomPaint.setStyle(Paint.Style.STROKE);
        }

        CustomCanvas.drawPath(path, CustomPaint);
        invalidate();
    }


    public void onDraw(Canvas canvas) {
        if (CustomBitmap != null)
            canvas.drawBitmap(CustomBitmap, 0, 0, CustomPaint);
    }


}

