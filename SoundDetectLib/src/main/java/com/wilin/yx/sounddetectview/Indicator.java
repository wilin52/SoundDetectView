package com.wilin.yx.sounddetectview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class Indicator extends View {

    private Paint paint;
    private Path path;
    private int width;
    private int obtuseAngleWidth = 8;

    public Indicator(Context context) {
        super(context);
        initPaint();
    }

    public Indicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public Indicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        paint = new Paint();
        paint.setStrokeWidth(8);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(255, 255, 255, 0));

        path = new Path();

        // 20dp
        width = dip2px(getContext(), 20);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        path.moveTo(0, width / 2 - obtuseAngleWidth / 2);
        path.lineTo(width, 0);
        path.lineTo(width, width);
        path.lineTo(0, width / 2 + obtuseAngleWidth / 2);
        path.close();
        canvas.drawPath(path, paint);
        super.onDraw(canvas);
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
