package com.example.utility.apps;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;

import androidx.appcompat.app.AppCompatActivity;

import com.example.utility.R;
import com.example.utility.helpers.JavaUtils;
import com.example.utility.models.StopWatch;

public class StopWatchAppActivity extends AppCompatActivity {
    private Chronometer chronometer;
    private StopWatch stopWatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch_app);
        getSupportActionBar().setTitle(JavaUtils.FormatActionBarText(R.string.app_stopwatch, StopWatchAppActivity.this));
        InitializeStopWatch();
    }

    /*
    Create new StopWatch object with chronometer widget
     */
    private void InitializeStopWatch()
    {
        chronometer = findViewById(R.id.stopwatch);
        stopWatch = new StopWatch(chronometer);
        Log.d("Chronometer Base Time", Long.toString(chronometer.getBase()));
    }

    /*
    Start stopwatch onClick event
     */
    public void startWatch(View view)
    {
        stopWatch.start();
    }

    /*
    Reset stopwatch onClick event
     */
    public void resetWatch(View view)
    {
        stopWatch.reset();
    }
}
