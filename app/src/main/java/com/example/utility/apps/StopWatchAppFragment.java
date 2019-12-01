package com.example.utility.apps;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.utility.R;
import com.example.utility.models.StopWatch;

public class StopWatchAppFragment extends Fragment {
    private Chronometer chronometer;
    private StopWatch stopWatch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stopwatch_app, container, false);
        InitializeStopWatch(view);

        Button start = view.findViewById(R.id.btnStart);
        Button reset = view.findViewById(R.id.btnReset);

        start.setOnClickListener(startWatch);
        reset.setOnClickListener(resetWatch);

        return view;
    }

    /*
    Create new StopWatch object with chronometer widget
     */
    private void InitializeStopWatch(View view)
    {
        chronometer = view.findViewById(R.id.stopwatch);
        stopWatch = new StopWatch(chronometer);
        Log.d("Chronometer Base Time", Long.toString(chronometer.getBase()));
    }

    /*
    Start stopwatch onClick event
     */
    View.OnClickListener startWatch = new View.OnClickListener() {
        public void onClick(View view){
            stopWatch.start();
        }
    };

    /*
    Reset stopwatch onClick event
     */
    View.OnClickListener resetWatch = new View.OnClickListener() {
        public void onClick(View view){
            stopWatch.reset();
        }
    };
}
