package ru.sberbank.lesson10.task.game.advanced;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        final Button button = findViewById(R.id.newGame);
        button.setVisibility(isStarted ? View.INVISIBLE : View.VISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ballPosition = new Random().nextInt(3) + 1;
                isStarted = true;
                button.setVisibility(View.INVISIBLE);
                for(Bucket bucket : buckets.values()) {
                    ImageView bucketImageView = getBucket(bucket.getBucketId());
                    bucketImageView.setImageDrawable(getResources().getDrawable(R.mipmap.bucket));
                    bucket.setVisible(true);
                    bucketImageView.setVisibility(View.VISIBLE);
                }
            }
        });

        bucketViews = Maps.newHashMap();

        for(Bucket bucket : buckets.values()) {
            int id = bucket.getBucketId();
            ImageView bucketImageView = getBucket(id);
            bucketImageView.setVisibility(bucket.isVisible() ? View.VISIBLE : View.INVISIBLE);
            bucketViews.put(id, bucketImageView);
        }

        for (ImageView bucketView : bucketViews.values()) {
            bucketView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final Resources res = getResources();
                    String result;
                    final int selectedBucketNum = Integer.parseInt(v.getTag().toString());
                    if (isStarted) {
                        if ( selectedBucketNum == ballPosition) {
                            result = res.getString(R.string.win);
                        } else {
                            result = res.getString(R.string.looose);
                        }
                    } else {
                        result = res.getString(R.string.isOver);
                    }
                    Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();

                    Bucket correctBucket = buckets.get(ballPosition);
                    if (correctBucket == null) {
                        return;
                    }
                    final ImageView correctBucketImageView = getBucket(correctBucket.getBucketId());
                    final int correctBucketNum = Integer.parseInt(correctBucketImageView.getTag().toString());

                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            v.setVisibility(View.INVISIBLE);
                            for (final ImageView view : bucketViews.values()) {
                                int currentBucketNum = Integer.parseInt(view.getTag().toString());
                                if (currentBucketNum != selectedBucketNum && currentBucketNum != correctBucketNum) {
                                    view.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            view.setVisibility(View.INVISIBLE);
                                            correctBucketImageView.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    correctBucketImageView.setImageDrawable(res.getDrawable(R.mipmap.ball));
                                                }
                                            }, 1000);
                                        }
                                    }, 1000);
                                }
                            }
                        }
                    }, 1000);

                    button.setVisibility(View.VISIBLE);
                    isStarted = false;
                }
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

    private Runnable work(final View v) {
        return new Runnable() {
            @Override
            public void run() {
                v.setVisibility(View.INVISIBLE);
            }
        };
    }
}
