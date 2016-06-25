package com.wilin.yx.sounddetectview;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * The SoundDetectionView class displays the curve of decibels and
 *              the degree of decibels. And you can set the threshold to detect
 *              the sound.
 *
 * @version 1.0 2016-06-01
 * @author linwenlong
 */
public class SoundDetectionView extends FrameLayout {
    private List<String> levels;
    private String curLevel;
    public static final int TOTAL_DECIBEL_COUNT = SoundDecibelLevelView.TOTAL_DECIBEL_COUNT;

    private View indicator;
    private SoundDecibelLevelView soundDecibelLevelView;

    private SoundDetectionLevelListener detectionLevelListener;

    public SoundDetectionView(Context context) {
        super(context);
        setWillNotDraw(true);
        initView(context);
    }

    public SoundDetectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(true);
        initView(context);
    }

    public SoundDetectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(true);
        initView(context);
    }

    public void setLevels(List<String> levels) {
        if (null == this.levels) {
            this.levels = new ArrayList<String>();
        }
        this.levels.addAll(levels);
        soundDecibelLevelView.setLevels(levels);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        super.onLayout(changed, l, t, r, b);
    }

    private void initView(Context context) {
        View layout = LayoutInflater.from(context).inflate(R.layout.sound_detection_view, this);
        indicator = layout.findViewById(R.id.sound_detection_indicator);
        soundDecibelLevelView = (SoundDecibelLevelView) layout.findViewById(R.id.sound_detection_level_decibelview);

        if (null != curLevel) {
            soundDecibelLevelView.updateCurLevel(curLevel);
        }

        indicator.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_MOVE: {
                        int y = (int) event.getY();
                        moveLevelPosition(y);
                    }
                    break;
                    case MotionEvent.ACTION_UP: {
                        updateSelectLevelPosition();
                    }
                    break;
                    case MotionEvent.ACTION_DOWN:
                        break;
                }
                return true;
            }
        });

        soundDecibelLevelView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                int action = event.getAction();
                if (action == MotionEvent.ACTION_UP) {
                    updateSelectLevelPosition();
                }
                return true;
            }
        });
    }

    private GestureDetector gestureDetector = new GestureDetector(getContext(),
            new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    int y = (int) distanceY;
                    moveLevelPosition(-y);
                    return false;
                }

            }, null, true);

    public void initDecibel(final int[] decibels) {
        soundDecibelLevelView.initDecibel(decibels);
    }

    public interface SoundDetectionLevelListener {
        void levelSelect(int level);
    }

    /**
     * according to the position of indicator, change the position of
     * select-level bar
     */
    private void updateSelectLevelPosition() {
        if (levels == null || levels.size() == 0) {
            return;
        }
        int levelNum = this.levels.size() - 1;
        int levelHeight = soundDecibelLevelView.getHeight() / levelNum;
        int levelViewTop = soundDecibelLevelView.getTop();
        int i = levelNum;
        int indiCenterY = (indicator.getTop() + indicator.getBottom()) / 2;
        while (i >= 0) {
            int y = i * levelHeight + levelViewTop;
            // find the level
            if (indiCenterY > y) {
                if (null != detectionLevelListener) {
                    int selectLevel = i + 1;

                    // if out of range
                    if (i >= this.levels.size() - 1 || indiCenterY - y < 30) {
                        selectLevel--;
                        y -= levelHeight;
                    }

                    if (selectLevel == this.levels.size() - 1) {
                        y -= soundDecibelLevelView.frameLineWidth;
                    } else if (selectLevel == 0) {
                        y += soundDecibelLevelView.frameLineWidth;
                    }

                    this.curLevel = levels.get(selectLevel);
                    detectionLevelListener.levelSelect(selectLevel);
                    soundDecibelLevelView.updateCurLevel(this.levels.get(selectLevel));
                }
                indicator.layout(indicator.getLeft(), y + levelHeight - indicator.getHeight() / 2,
                        indicator.getRight(), y + levelHeight + indicator.getHeight() / 2);
                break;
            }

            if (i == 0) {
                if (null != detectionLevelListener) {
                    detectionLevelListener.levelSelect(0);
                    this.curLevel = levels.get(0);
                }
                soundDecibelLevelView.updateCurLevel(this.levels.get(0));
                indicator.layout(indicator.getLeft(), y - indicator.getHeight() / 2 + soundDecibelLevelView.frameLineWidth, indicator.getRight(), y
                        + indicator.getHeight() / 2 + soundDecibelLevelView.frameLineWidth);
            }

            i--;
        }
    }

    /**
     * move the level bar
     *
     * @param y the distance
     */
    private void moveLevelPosition(int y) {
        soundDecibelLevelView.updateLevelPosition(indicator.getTop() + y);
        indicator.layout(indicator.getLeft(), indicator.getTop() + y, indicator.getRight(), indicator.getBottom() + y);
    }

    private void updateIndicatorPosition(int index) {
        int levelNum = this.levels.size() - 1;
        int levelHeight = soundDecibelLevelView.getHeight() / levelNum;
        int y = index * levelHeight + soundDecibelLevelView.getTop();

        if (index == this.levels.size() - 1) {
            y -= soundDecibelLevelView.frameLineWidth;
        } else if (index == 0) {
            y += soundDecibelLevelView.frameLineWidth;
        }

        indicator.layout(indicator.getLeft(), y - indicator.getHeight() / 2, indicator.getRight(), y
                + indicator.getHeight() / 2);
    }

    public void setSoundDetectionLevelListener(SoundDetectionLevelListener detectionLevelListener) {
        this.detectionLevelListener = detectionLevelListener;
    }

    public void setDecibels(int[] decibels) {
        soundDecibelLevelView.setData(decibels);
    }

    public void addDecibels(int[] decibels) {
        soundDecibelLevelView.addDecibels(decibels);
    }

    public void addDecibel(int decibel) {
        soundDecibelLevelView.addDecibel(decibel);
    }

    public void setCurLevel(String curLevel) {
        if (null != this.levels && this.levels.size() > 0) {
            if (curLevel == null) {
                setCurLevel(0);
            } else {
                for (int i = 0, size = this.levels.size(); i < size; i++) {
                    if (this.levels.get(i).equals(curLevel)) {
                        setCurLevel(i);
                        break;
                    }
                }
            }

        }

    }

    public void setCurLevel(final int index) {
        if (null != this.levels && index < this.levels.size()) {
            this.curLevel = this.levels.get(index);
            soundDecibelLevelView.updateCurLevel(index);
            postDelayed(new Runnable() {

                @Override
                public void run() {
                    updateIndicatorPosition(index);

                }
            }, SoundDecibelLevelView.DELAY_INIT_DECIBEL_TIME);

        }
    }

    public String getCurLevel() {
        return this.curLevel;
    }

    public void setMaxDecibel(int maxDecibel) {
        soundDecibelLevelView.setMaxDecibel(maxDecibel);
    }

    public void setMinDecibel(int minDecibel) {
        soundDecibelLevelView.setMinDecibel(minDecibel);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.curLevel = this.curLevel;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.curLevel = ss.curLevel;
    }
}
