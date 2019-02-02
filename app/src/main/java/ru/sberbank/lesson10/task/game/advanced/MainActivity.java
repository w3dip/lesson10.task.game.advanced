package ru.sberbank.lesson10.task.game.advanced;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ru.sberbank.lesson10.task.game.R;

import static ru.sberbank.lesson10.task.game.advanced.Bucket.of;

public class MainActivity extends Activity {

    private static final String GAME_IS_STARTED = "isStarted";
    private static final String BUCKETS_STATE = "buckets";
    private static final String BALL_STATE = "ballPosition";

    private int ballPosition;
    private boolean isStarted;

    private HashMap<Integer, Bucket> buckets;
    private Map<Integer, ImageView> bucketViews;

    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(Looper.getMainLooper());

        if (savedInstanceState != null) {
            isStarted = savedInstanceState.getBoolean(GAME_IS_STARTED);
            ballPosition = savedInstanceState.getInt(BALL_STATE);
            buckets = (HashMap<Integer, Bucket>) savedInstanceState.getSerializable(BUCKETS_STATE);
        } else {
            buckets = Maps.newHashMap();
            buckets.put(1, of(R.id.bucket1));
            buckets.put(2, of(R.id.bucket2));
            buckets.put(3, of(R.id.bucket3));
        }

        final Button newGameBtn = findViewById(R.id.newGame);
        newGameBtn.setVisibility(isStarted ? View.INVISIBLE : View.VISIBLE);
        newGameBtn.setOnClickListener(v -> {
            handler.removeCallbacksAndMessages(null);
            ballPosition = new Random().nextInt(3) + 1;
            isStarted = true;
            newGameBtn.setVisibility(View.INVISIBLE);
            for(Bucket bucket : buckets.values()) {
                ImageView bucketImageView = getBucket(bucket.getBucketId());
                bucketImageView.setImageDrawable(getResources().getDrawable(R.mipmap.bucket));
                bucket.setVisible(true);
                bucketImageView.setVisibility(View.VISIBLE);
            }
        });

        bucketViews = Maps.newHashMap();

        for(Bucket bucket : buckets.values()) {
            ImageView bucketImageView = getBucket(bucket.getBucketId());
            bucketImageView.setVisibility(bucket.isVisible() ? View.VISIBLE : View.INVISIBLE);
            bucketViews.put(Integer.parseInt(bucketImageView.getTag().toString()), bucketImageView);
        }

        for (Map.Entry<Integer, ImageView> entry : bucketViews.entrySet()) {
            entry.getValue().setOnClickListener(v -> {
                int index = 1;
                final Resources res = getResources();
                String result;
                final int selectedBucketNum = entry.getKey();
                if (!isStarted) {
                    result = res.getString(R.string.isOver);
                    Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                    return;
                } else if (selectedBucketNum == ballPosition) {
                    result = res.getString(R.string.win);
                } else {
                    result = res.getString(R.string.looose);
                }

                startAnimation(v, selectedBucketNum, index);
                handler.postDelayed(() -> Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show(), 1000);
                index++;

                for (Map.Entry<Integer, ImageView> entry1 : Maps.filterKeys(bucketViews, input -> input != selectedBucketNum).entrySet()) {
                    startAnimation(entry1.getValue(), entry1.getKey(), index);
                    index++;
                }

                newGameBtn.setVisibility(View.VISIBLE);
                isStarted = false;
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(GAME_IS_STARTED, isStarted);
        outState.putSerializable(BUCKETS_STATE, buckets);
        outState.putSerializable(BALL_STATE, ballPosition);
        super.onSaveInstanceState(outState);
    }

    private ImageView getBucket(int id) {
        return (ImageView)findViewById(id);
    }

    private Runnable openEmptyBucket(final View v) {
        return () -> v.setVisibility(View.INVISIBLE);
    }

    private Runnable openBallBucket(final View v) {
        return () -> ((ImageView)v).setImageDrawable(getResources().getDrawable(R.mipmap.ball));
    }

    private void startAnimation(View v, int currentBucketNum, int index) {
        if (currentBucketNum == ballPosition) {
            runnable = openBallBucket(v);
            handler.postDelayed(runnable, 1000 * index);
        } else {
            runnable = openEmptyBucket(v);
            handler.postDelayed(runnable, 1000 * index);
        }
    }
}
