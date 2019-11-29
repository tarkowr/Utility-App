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

    private ProgressBar progressBar;
    private User user;
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
                        launchHomeActivity();
                    }
                });
            }
        };

        this.timeout = new Timer();
        this.timeout.schedule(startSetup, START_DELAY);
        this.timeout.schedule(handleTimeout, TIMEOUT);

        setProgressBar(0);

        return view;
    }

    private void launchHomeActivity(){
        cancelTimer();
        ((SplashActivity)getActivity()).removeFragment(SplashFragment.this);
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
    }

    private void launchCreateAccountFragment(){
        cancelTimer();
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                ((SplashActivity)getActivity()).removeFragment(SplashFragment.this);
                ((SplashActivity)getActivity()).launchCreateAccountFragment();
            }
        });
    }

    private void databaseSetup(){
        setProgressBar(25);
        JavaUtils.pauseUI(JavaUtils.returnRandomInt(MAX_DELAY));

        UserDataService dataService = UserDataService.get(getActivity());
        setProgressBar(50);
        JavaUtils.pauseUI(JavaUtils.returnRandomInt(MAX_DELAY));

        setStatusText(R.string.status_user);
        this.user = dataService.getDefaultUser();

        setProgressBar(75);
        JavaUtils.pauseUI(JavaUtils.returnRandomInt(MAX_DELAY));

        setProgressBar(100);
        JavaUtils.pauseUI(JavaUtils.returnRandomInt(MAX_DELAY));

        cancelTimer();
        setStatusText("");

        if(this.user == null){
            Log.d("USER", "IS NULL");
            launchCreateAccountFragment();
        }
        else{
            Log.d("USER", user.getUsername());
            launchHomeActivity();
        }
    }

    private void setProgressBar(int value){
        progressBar.setProgress(value);
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

    private void setStatusText(final String str){
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                txtStatus.setText(str);
            }
        });
    }

    private class DatabaseStatus extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String ... args) {
            databaseSetup();
            // setProgressBar(100);
            /*while(true) {
                publishProgress();
            }*/

            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            // setProgressBar(100);
            // cancelTimer();
            // launchHomeActivity();
        }
    }

}
