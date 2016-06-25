package com.wilin.yx.sounddetectview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

/**
 * The SoundDecibelLevelView class displays the curve of decibels and the degree
 * of decibels. It cannot be set a threshold to detect, but you can operate the
 * {@linkplain SoundDetectionView} class.
 *
 * @version 1.0 2016-06-01
 * @author linwenlong
 */
public class SoundDecibelLevelView extends SoundDecibelView {

	// to mark the state of the select-level bar
	private final String CURRENT_LEVEL_SCROLLING = "current_level_scrolling";
	private final String CURRENT_LEVEL_INVALID = "current_level_invalid";
	
	// the levels of sound
	private List<String> levels;
	private String curLevel = CURRENT_LEVEL_INVALID;

	private int levelLineWidth = 4;

	//frame line
	private Paint framePaint;
	private Path framePath;
	public final int frameLineWidth = 4;

	// the select-level bar
	private Paint levelPaint;
	private Path levelPath;
	private Paint levelTextPaint;
	private Paint selectLevelPaint;
	private Path selectLevelPath;

	private int selectLevelY = -1;
	private int textSize;

	public SoundDecibelLevelView(Context context) {
		super(context);
		initPaint();
	}

	public SoundDecibelLevelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint();
	}

	public SoundDecibelLevelView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initPaint();
	}

	public void updateCurLevel(String level) {
		this.curLevel = level;
		invalidate();
	}
	
	public void updateCurLevel(int index){
		if(null != this.levels && index < this.levels.size()){
			this.curLevel = this.levels.get(index);
			updateCurLevel(curLevel);
		}
	}

	public void updateLevelPosition(int y) {
		selectLevelY = y;
		curLevel = CURRENT_LEVEL_SCROLLING;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		drawFrameLine(canvas);

		if (null == levels || levels.size() <= 0) {
			// draw the curve
			super.onDraw(canvas);
		} else {
			// draw the frame
			int levelHeight = height / (levels.size() - 1);
			for (int i = 0; i < levels.size(); i++) {
				levelPath.moveTo(0, (i + 1) * levelHeight);
				levelPath.lineTo(width, (i + 1) * levelHeight);
				canvas.drawPath(levelPath, levelPaint);
			}

			// draw the curve
			super.onDraw(canvas);

			// draw the select-level and text of level
			for (int i = 0; i < levels.size(); i++) {
				String level = levels.get(i);

				selectLevelPath.reset();
				if (level.equals(curLevel)) {
					selectLevelY = i * levelHeight;
					
					// to make the level bolder
					if(i == 0){
						selectLevelY += frameLineWidth;
					} else if(i == levels.size() - 1){
						selectLevelY -= frameLineWidth;
					}
					
					selectLevelPath.reset();
					selectLevelPath.moveTo(0, selectLevelY);
					selectLevelPath.lineTo(width, selectLevelY);

				} else if (CURRENT_LEVEL_SCROLLING.equals(curLevel)) {

					selectLevelPath.moveTo(0, selectLevelY);
					selectLevelPath.lineTo(width, selectLevelY);
				}
				canvas.drawPath(selectLevelPath, selectLevelPaint);

				if (i == 0) {
					canvas.drawText(level, 20, i * levelHeight + textSize, levelTextPaint);
				} else {
					canvas.drawText(level, 20, i * levelHeight-10, levelTextPaint);
				}
			}
		}

	}

	private void drawFrameLine(Canvas canvas) {
		// to make the fame bolder.

		framePath.moveTo(0, 0);
		framePath.lineTo(width, 0);
		framePath.lineTo(width, height);
		framePath.lineTo(0, height);
		framePath.lineTo(0, 0);
		canvas.drawPath(framePath, framePaint);
	}

	public void setLevels(List<String> levels) {
		if (null == levels || levels.size() <= 0) {
			return;
		}

		if (null == this.levels) {
			this.levels = new ArrayList<String>();
		}

		this.levels.clear();
		this.levels.addAll(levels);
		invalidate();
	}
	
	public List<String> getLevels(){
		return this.levels;
	}

	/**
	 * select-level
	 * @param level the level to detect
	 */
	public void setDetectionLevel(String level) {
		if (null == this.levels || this.levels.size() <= 0) {
			return;
		}

		if (levels.contains(level)) {
			curLevel = level;
		} else {
			curLevel = levels.get(0);
		}
		invalidate();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		super.onLayout(changed, l, t, r, b);
	}

	private void initPaint() {

		framePaint = new Paint();
		framePaint.setStrokeWidth(frameLineWidth);
		framePaint.setStyle(Paint.Style.STROKE);
		framePaint.setColor(Color.argb(255, 208, 208, 208));
		framePath = new Path();

		// the levels of detection
		levelPaint = new Paint();
		levelPaint.setStrokeWidth(levelLineWidth);
		levelPaint.setStyle(Paint.Style.STROKE);
		levelPaint.setColor(Color.argb(255, 208, 208, 208));
		levelPath = new Path();

		textSize = dip2px(getContext(), 12);
		levelTextPaint = new Paint();
		levelTextPaint.setTextSize(textSize);
		levelTextPaint.setColor(Color.argb(255, 92, 92, 92));

		//select-level bar
		selectLevelPaint = new Paint();
		selectLevelPaint.setStrokeWidth(levelLineWidth * 2);
		selectLevelPaint.setStyle(Paint.Style.STROKE);
		selectLevelPaint.setColor(Color.argb(255, 255, 255, 0));
		selectLevelPath = new Path();
	}

	private int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
}
