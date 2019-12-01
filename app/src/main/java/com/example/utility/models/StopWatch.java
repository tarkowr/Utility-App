package com.example.utility.models;

import android.os.SystemClock;
import android.widget.Chronometer;

public class StopWatch {
    private Chronometer stopWatch;
    public Boolean isCounting;

    /*
    StopWatch Constructor
    Chronometer widget passed via dependency injection
     */
    public StopWatch(Chronometer chronometer)
    {
        stopWatch = chronometer;
        isCounting = false;
    }

    /*
    Reset chronometer and start if not already running
     */
    public void start()
    {
        if(!isCounting)
        {
            this.reset();
            stopWatch.start();
            isCounting = true;
        }
    }

    /*
    Stop chronometer
     */
    public void stop()
    {
        stopWatch.stop();
        isCounting = false;
    }

    /*
    Reset chronometer to zero
    Source explaining how to reset chronometer to zero:
    https://stackoverflow.com/questions/31520859/chronometer-i-want-to-make-a-button-reset-to-00-00-not-restart-the-chronomete
     */
    public void reset()
    {
        stop();
        stopWatch.setBase(SystemClock.elapsedRealtime());
    }
}
