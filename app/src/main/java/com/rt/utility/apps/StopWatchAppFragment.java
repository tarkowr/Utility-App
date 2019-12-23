package com.rt.utility.apps;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.rt.utility.R;
import com.rt.utility.models.StopWatch;

public class StopWatchAppFragment extends Fragment {
    private Chronometer chronometer;
    private StopWatch stopWatch;
    private Button startPause;
    private Button reset;
    private long stopTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stopwatch_app, container, false);
        InitializeStopWatch(view);
        return view;
    }

    /*
    Create new StopWatch object with chronometer widget
     */
    private void InitializeStopWatch(View view)
    {
        chronometer = view.findViewById(R.id.stopwatch);
        stopWatch = new StopWatch(chronometer);
        startPause = view.findViewById(R.id.btnStartPause);
        reset = view.findViewById(R.id.btnReset);
        stopTime = 0;

        startPause.setOnClickListener(startPauseWatch);
        reset.setOnClickListener(resetWatch);
        Log.d("Chronometer Base Time", Long.toString(chronometer.getBase()));
    }

    /*
    Start/Pause stopwatch onClick event
    Learned how to pause/resume a chronometer from https://stackoverflow.com/questions/19194302/android-chronometer-resume-function
     */
    private View.OnClickListener startPauseWatch = new View.OnClickListener() {
        public void onClick(View view){
            if(stopWatch.isCounting){
                stopTime = chronometer.getBase() - SystemClock.elapsedRealtime();
                stopWatch.stop();
            }
            else{
                chronometer.setBase(SystemClock.elapsedRealtime() + stopTime);
                stopWatch.start();
            }

            toggleStartPauseButton(!stopWatch.isCounting); // Reverse since stopWatch toggles isCounting on stop() and start()
        }
    };

    /*
    Reset stopwatch onClick event
     */
    private View.OnClickListener resetWatch = new View.OnClickListener() {
        public void onClick(View view){
            stopTime = 0;
            stopWatch.reset();
            toggleStartPauseButton(true);
        }
    };

    /*
    Toggle the UI attributes/properties of the start/stop button as the chronometer switches between start/pause states
     */
    private void toggleStartPauseButton(boolean setStart){
        if(setStart){
            startPause.setText(getResources().getText(R.string.app_stopwatch_start_btn));
            startPause.setBackground(getResources().getDrawable(R.drawable.circle_button));
            startPause.setTextColor(getResources().getColor(R.color.colorWhite));
        }
        else{
            startPause.setText(getResources().getText(R.string.app_stopwatch_stop_btn));
            startPause.setBackground(getResources().getDrawable(R.drawable.circle_button_hollow));
            startPause.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }
}
