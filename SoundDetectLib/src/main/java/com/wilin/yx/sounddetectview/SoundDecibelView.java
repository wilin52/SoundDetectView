package com.wilin.yx.sounddetectview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The SoundDecibelView class displays the curve of decibels. It doesn't draw
 * the degree information,but the {@linkplain SoundDecibelLevelView} class does.
 *
 * @version 1.0 2016-06-01
 * @author linwenlong
 */
public class SoundDecibelView extends View {
    // maximum
    private int maxDecibel = 200;
    // minimum
    private int minDecibel = 0;
    protected int width, height;

    private ArrayList<Point> decibelPoints;

    private Paint decibelFillPaint;
    private Path decibelFillPath;
    private Paint decibelPaint;
    private Path decibelPath;
    private final int decibelLineWidth = 8;

    private final static int DECIBEL_COUNT = 24;//
    private final static int STABLE_DECIBEL_COUNT = 4; // to make the line stable.
    public static final int TOTAL_DECIBEL_COUNT = DECIBEL_COUNT + 1 + STABLE_DECIBEL_COUNT;
    private int decibelDis;

    // if the data changed, it should be redraw.
    private boolean dataChanged = true;
    public static int DELAY_INIT_DECIBEL_TIME = 100;

    // for previewing
    private boolean isTest = false;

    public SoundDecibelView(Context context) {
        super(context);
        initData();
        initPaint();
    }

    public SoundDecibelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
        initPaint();
    }

    public SoundDecibelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
        initPaint();
    }

    private void initData() {
        decibelPoints = new ArrayList<Point>();
    }

    private void initPaint() {

        // the curve
        decibelPaint = new Paint();
        decibelPaint.setStrokeWidth(decibelLineWidth);
        decibelPaint.setStrokeCap(Paint.Cap.ROUND);
        decibelPaint.setAntiAlias(true);
        decibelPaint.setStyle(Style.STROKE);
        decibelPaint.setColor(Color.argb(255, 0, 180, 235));
        decibelPath = new Path();

        // fill the curve
        decibelFillPaint = new Paint();
        decibelFillPaint.setStrokeCap(Paint.Cap.ROUND);
        decibelFillPaint.setAntiAlias(true);
        decibelFillPaint.setStyle(Style.FILL);
        decibelFillPaint.setColor(Color.argb(70, 0, 180, 235));
        decibelFillPath = new Path();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        width = getWidth();
        height = getHeight();
        decibelDis = width / DECIBEL_COUNT;

        if (isTest) {
            testData();
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (null != decibelPoints && decibelPoints.size() > 0) {
            if (dataChanged) {
                decibelPath.reset();
                decibelFillPath.reset();
                calBezierPath(decibelPoints, decibelPath, decibelFillPath);
//                catmullRom(decibelPoints,200, decibelPath, decibelFillPath);

                decibelFillPath.lineTo(width, height);
                decibelFillPath.lineTo(0, height);
                decibelFillPath.lineTo(decibelPoints.get(0).x, decibelPoints.get(0).y);
            }
            canvas.drawPath(decibelPath, decibelPaint);
            canvas.drawPath(decibelFillPath, decibelFillPaint);

        }
        dataChanged = false;

        super.onDraw(canvas);
    }

    /**
     * @param decibels
     */
    public void setData(int[] decibels) {
        dataChanged = true;
        decibelPoints.clear();
        if (null != decibels) {
            for (int i = 0; i < decibels.length; i++) {
                decibelPoints.add(convertDecibelInt2Point(i, decibels[i]));
            }
        }

        postInvalidate();
    }

    /**
     * to make the curve smoother, the size of decibels should be 1;
     *
     * @param decibels
     */
    public void addDecibels(int[] decibels) {
        dataChanged = true;
        if (null != decibels) {
            for (int j = 0; j < decibels.length; j++) {
                addVolumeData(decibels[j]);
            }
        }
        postInvalidate();
    }

    /**
     * to make the curve smoother, the size of decibels should be 1;
     *
     * @param decibel
     */
    public void addDecibel(int decibel) {
        dataChanged = true;
        addVolumeData(decibel);
        postInvalidate();
    }

    private void addVolumeData(int decibel) {
        Iterator<Point> it = decibelPoints.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
            break;
        }
        // update the position
        for (int i = 0, size = decibelPoints.size(); i < size; i++) {
            Point point = decibelPoints.get(i);
            point.setX(point.x - decibelDis);
        }
        decibelPoints.add(convertDecibelInt2Point(decibelPoints.size(), decibel));
    }

    /**
     * generate the point
     *
     * @param index   the index of decibels
     * @param decibel the decibel
     * @return
     */
    private Point convertDecibelInt2Point(int index, int decibel) {
        Point point = new Point((index - STABLE_DECIBEL_COUNT / 2) * decibelDis, (float) ((1 - (decibel - minDecibel)
                / (maxDecibel * 1.0))
                * (height - decibelLineWidth) + decibelLineWidth));

        return point;
    }

    /**
     * just for the first time
     *
     * @param decibels
     */
    public void initDecibel(final int[] decibels) {
        postDelayed(new Runnable() {

            @Override
            public void run() {
                setData(decibels);
            }
        }, DELAY_INIT_DECIBEL_TIME);
    }

	// calculate the path
	private void catmullRom(ArrayList<Point> point, int cha, Path framePath, Path fillpath) {
		if (point.size() < 4) {
			return;
		}
		framePath.moveTo(point.get(0).x, point.get(0).y);
		fillpath.moveTo(point.get(0).x, point.get(0).y);
		for (int index = 1; index < point.size() - 2; index++) {
			Point p0 = point.get(index - 1);
			Point p1 = point.get(index);
			Point p2 = point.get(index + 1);
			Point p3 = point.get(index + 2);

			for (int i = 1; i <= cha; i++) {
				float t = i * (1.0f / cha);
				float tt = t * t;
				float ttt = tt * t;

				Point pi = new Point(); // intermediate point
				pi.x = (float) (0.5 * (2 * p1.x + (p2.x - p0.x) * t + (2 * p0.x - 5 * p1.x + 4 * p2.x - p3.x) * tt + (3
						* p1.x - p0.x - 3 * p2.x + p3.x)
						* ttt));
				pi.y = (float) (0.5 * (2 * p1.y + (p2.y - p0.y) * t + (2 * p0.y - 5 * p1.y + 4 * p2.y - p3.y) * tt + (3
						* p1.y - p0.y - 3 * p2.y + p3.y)
						* ttt));
				framePath.lineTo(pi.x, pi.y);
				fillpath.lineTo(pi.x, pi.y);
				pi = null;
			}
		}
		framePath.lineTo(point.get(point.size() - 1).x, point.get(point.size() - 1).y);
		fillpath.lineTo(point.get(point.size() - 1).x, point.get(point.size() - 1).y);
	}

    private void calBezierPath(ArrayList<Point> points, Path framePath, Path fillpath) {

        if (points.size() < 4) {
            return;
        }

        //reference http://www.2cto.com/kf/201604/497130.html
        List<Point> controlPoints = initControlPoints(points);
        for (int i = 0; i < points.size(); i++) {
            if (i == 0) {// 第一条为二阶贝塞尔
                framePath.moveTo(points.get(i).x, points.get(i).y);// 起点
                framePath.quadTo(controlPoints.get(i).x, controlPoints.get(i).y,// 控制点
                        points.get(i + 1).x, points.get(i + 1).y);

                fillpath.moveTo(points.get(i).x, points.get(i).y);// 起点
                fillpath.quadTo(controlPoints.get(i).x, controlPoints.get(i).y,// 控制点
                        points.get(i + 1).x, points.get(i + 1).y);

            } else if (i < points.size() - 2) {// 三阶贝塞尔
                framePath.cubicTo(controlPoints.get(2 * i - 1).x, controlPoints.get(2 * i - 1).y,// 控制点
                        controlPoints.get(2 * i).x, controlPoints.get(2 * i).y,// 控制点
                        points.get(i + 1).x, points.get(i + 1).y);// 终点

                fillpath.cubicTo(controlPoints.get(2 * i - 1).x, controlPoints.get(2 * i - 1).y,// 控制点
                        controlPoints.get(2 * i).x, controlPoints.get(2 * i).y,// 控制点
                        points.get(i + 1).x, points.get(i + 1).y);// 终点
            } else if (i == points.size() - 2) {// 最后一条为二阶贝塞尔
                framePath.moveTo(points.get(i).x, points.get(i).y);// 起点
                framePath.quadTo(controlPoints.get(controlPoints.size() - 1).x, controlPoints.get(controlPoints.size() - 1).y,
                        points.get(i + 1).x, points.get(i + 1).y);// 终点

                fillpath.moveTo(points.get(i).x, points.get(i).y);// 起点
                fillpath.quadTo(controlPoints.get(controlPoints.size() - 1).x, controlPoints.get(controlPoints.size() - 1).y,
                        points.get(i + 1).x + decibelLineWidth, points.get(i + 1).y);// 终点
            }
        }

    }

    private List<Point> initControlPoints(List<Point> points) {
        List<Point> midPoints = initMidPoints(points);
        List<Point> controlPoints = new ArrayList<Point>();
        for (int i = 1; i < midPoints.size(); i++) {
            Point p0 = midPoints.get(i - 1);
            Point p1 = midPoints.get(i);
            Point mp = initMidPoint(p0, p1);
            float dis = points.get(i).y - mp.y;
            controlPoints.add(new Point(p0.x, p0.y + dis));
            controlPoints.add(new Point(p1.x, p1.y + dis));
        }
        return controlPoints;
    }

    private List<Point> initMidPoints(List<Point> points) {
        List<Point> midPoints = new ArrayList<Point>();
        for (int i = 0; i < points.size() - 1; i++) {
            midPoints.add(initMidPoint(points.get(i), points.get(i + 1)));
        }
        return midPoints;
    }

    private Point initMidPoint(Point p0, Point p1) {
        return new Point((p0.x + p1.x) / 2, (p0.y + p1.y) / 2);
    }


    private void testData() {
        int[] decibels = new int[SoundDecibelLevelView.TOTAL_DECIBEL_COUNT];

        for (int i = 0; i < decibels.length; i++) {
            decibels[i] = (int) (Math.random() * 150);
        }

        setData(decibels);
    }

    /**
     * for previewing
     */
    public void test() {
        isTest = true;
    }

    public int getMaxDecibel() {
        return maxDecibel;
    }

    public void setMaxDecibel(int maxDecibel) {
        this.maxDecibel = maxDecibel;
    }

    public int getMinDecibel() {
        return minDecibel;
    }

    public void setMinDecibel(int minDecibel) {
        this.minDecibel = minDecibel;
    }
}
