package com.foodom.myapplication;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;
    private Button switchButton;

    private String defaultVideoPath;
    private List<String> eventVideoPaths;
    private int currentEventVideoPathIndex;
    private boolean surfaceCreated;

    private int status;

    private static final String TAG = "DEMO";

    private static final int STATUS_INIT = 0;
    private static final int STATUS_DEFAULT = 1;
    private static final int STATUS_EVENT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prepare();

        initViews();
        initVideoPaths();
        initSurfaceView();
    }

    private void prepare() {
        currentEventVideoPathIndex = -1;
        status = STATUS_INIT;
        surfaceCreated = false;
    }

    private void initViews() {
        surfaceView = findViewById(R.id.surfaceView);
        switchButton = findViewById(R.id.button);
        switchButton.setOnClickListener(this);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i(TAG, "onCompletion");
                if (status == STATUS_EVENT) {
                    playDefaultVideo();
                }
            }
        });
    }

    private void initVideoPaths() {
        eventVideoPaths = new ArrayList<>();
        String filesDirPath = getExternalFilesDir(null).getAbsolutePath();
        Log.i(TAG, filesDirPath);
        eventVideoPaths.add(filesDirPath + "/1.mp4");
        eventVideoPaths.add(filesDirPath + "/2.mp4");
        eventVideoPaths.add(filesDirPath + "/3.mp4");
        defaultVideoPath = filesDirPath + "/default.mp4";
    }

    private void initSurfaceView() {
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mediaPlayer.setDisplay(surfaceHolder);
                surfaceCreated = true;
                playDefaultVideo();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    private void playEventVideo() {
        if (!surfaceCreated) return;
        status = STATUS_EVENT;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(eventVideoPaths.get(currentEventVideoPathIndex));
            mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            mediaPlayer.setLooping(false);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playDefaultVideo() {
        if (!surfaceCreated) return;
        status = STATUS_DEFAULT;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(defaultVideoPath);
            mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        currentEventVideoPathIndex++;
        if (currentEventVideoPathIndex == eventVideoPaths.size()) {
            currentEventVideoPathIndex = 0;
        }
        playEventVideo();
    }
}
