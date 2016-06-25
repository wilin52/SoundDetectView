package com.wilin.yx.sounddetectview.sample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.wilin.yx.sounddetectview.SoundDecibelLevelView;
import com.wilin.yx.sounddetectview.SoundDetectionView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int maxDecibel = 100;
    private int minDecibel = 0;
    private List<String> levels;
    private String curLevel;
    private SoundDetectionView soundDetectionView;
    private SoundDecibelLevelView soundDecibelLevelView;
    private TextView mTextView;

    private boolean stop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initData();

        initView();
    }

    private void initData() {
        levels = new ArrayList<String>();
        levels.add("100");
        levels.add("80");
        levels.add("60");
        levels.add("40");
        levels.add("20");
        levels.add("0");

        curLevel = levels.get(0);
    }


    private void initView() {

        mTextView = (TextView) findViewById(R.id.threshold_text);
        mTextView.setText("the threshold is:".concat(levels.get(0)));

        soundDetectionView = (SoundDetectionView) findViewById(R.id.sound_detect_view);
        soundDetectionView.setSoundDetectionLevelListener(new SoundDetectionView.SoundDetectionLevelListener() {
            @Override
            public void levelSelect(int level) {
                String threshold = levels.get(level);
                mTextView.setText("the threshold is:".concat(threshold));
                soundDecibelLevelView.setDetectionLevel(threshold);
            }
        });
        soundDetectionView.setLevels(levels);
        soundDetectionView.setCurLevel(curLevel);
        soundDetectionView.setMaxDecibel(maxDecibel);
        soundDetectionView.setMinDecibel(minDecibel);
        soundDetectionView.initDecibel(initDecibel());

        soundDecibelLevelView = (SoundDecibelLevelView) findViewById(R.id.sound_level_view);
        soundDecibelLevelView.setLevels(levels);
        soundDecibelLevelView.setDetectionLevel(curLevel);
        soundDecibelLevelView.setMaxDecibel(maxDecibel);
        soundDecibelLevelView.setMinDecibel(minDecibel);
        soundDecibelLevelView.initDecibel(initDecibel());

        addDecibelThread.start();
    }

    /**
     * 模拟数据
     *
     * @return
     */
    private int[] initDecibel() {
        int[] decibels = new int[SoundDecibelLevelView.TOTAL_DECIBEL_COUNT];

//		for (int i = 0; i < decibels.length; i++) {
//			// decibels[i] = 0;
//			decibels[i] = (int) (Math.random() * 10) + 25;
//		}

        return decibels;

    }

    private Thread addDecibelThread = new Thread(new Runnable() {

        @Override
        public void run() {

            try {
                Thread.sleep(SoundDecibelLevelView.DELAY_INIT_DECIBEL_TIME);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            while (!stop) {
                int decibel = (int)(Math.random() * 20) + 30;
                soundDetectionView.addDecibel(decibel);
                soundDecibelLevelView.addDecibel(decibel);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    stop = true;
                    e.printStackTrace();
                }
            }
        }

    });


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {

        stop = true;
        super.onDestroy();
    }
}
