package com.example.utility;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.utility.dataservice.UserDataService;
import com.example.utility.helpers.JavaUtils;
import com.example.utility.models.User;

import java.util.Timer;
import java.util.TimerTask;

public class SplashFragment extends Fragment {

    private final int TIMEOUT = 10000;
    private final int START_DELAY = 200;
    private final int MAX_DELAY = 1000;
    private final int PROGRESS_INCREMENT = 25;

    private ProgressBar progressBar;
    private Timer timeout;
    private TextView txtStatus;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        txtStatus = view.findViewById(R.id.txtStatus);

        TimerTask startSetup = new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setStatusText(R.string.status_db);
                        new DatabaseStatus().execute();
                    }
                });
            }
        };

        TimerTask handleTimeout = new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        launchCreateAccountFragment();
                    }
                });
            }
        };

        this.timeout = new Timer();
        this.timeout.schedule(startSetup, START_DELAY);
        this.timeout.schedule(handleTimeout, TIMEOUT);

        return view;
    }

    private void launchHomeActivity(final User user){
        cancelTimer();
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                SplashActivity splashActivity = (SplashActivity)getActivity();
                splashActivity.removeFragment(SplashFragment.this);
                Intent intent = HomeActivity.newIntent(getActivity(), user.getId());
                startActivity(intent);
                splashActivity.finishActivity();
            }
        });
    }

    private void launchCreateAccountFragment(){
        cancelTimer();
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                SplashActivity splashActivity = (SplashActivity)getActivity();
                splashActivity.removeFragment(SplashFragment.this);
                splashActivity.launchCreateAccountFragment();
            }
        });
    }

    private User databaseSetup(){
        updateProgressAndDelay();

        UserDataService dataService = UserDataService.get(getActivity());
        updateProgressAndDelay();

        setStatusText(R.string.status_user);
        User user = dataService.getDefaultUser();
        updateProgressAndDelay();

        cancelTimer();
        setStatusText(R.string.status_complete);
        updateProgressAndDelay();

        return user;
    }

    private void updateProgressAndDelay(){
        setProgressBar(incrementProgress());
        JavaUtils.pauseUI(JavaUtils.returnRandomInt(MAX_DELAY));
    }

    private void setProgressBar(int value){
        progressBar.setProgress(value);
    }

    private int incrementProgress(){
        return progressBar.getProgress() + PROGRESS_INCREMENT;
    }

    private void cancelTimer(){
        this.timeout.cancel();
        this.timeout.purge();
    }

    private void setStatusText(final int stringResource){
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                txtStatus.setText(stringResource);
            }
        });
    }

    /*
    https://developer.android.com/reference/android/os/AsyncTask
     */
    private class DatabaseStatus extends AsyncTask<String, String, User> {

        @Override
        protected User doInBackground(String... args) {
            try{
                return databaseSetup();
            }
            catch (Exception e){
                Log.e("UtilitySplashFragment", "DB SETUP");
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(String... progress) { }

        @Override
        protected void onPostExecute(User user) {
            if(user == null){
                launchCreateAccountFragment();
            }
            else{
                launchHomeActivity(user);
            }
        }
    }
}
