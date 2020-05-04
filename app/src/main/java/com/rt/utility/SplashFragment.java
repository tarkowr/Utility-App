package com.rt.utility;

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

import com.rt.utility.dataservice.UserDataService;
import com.rt.utility.helpers.JavaUtils;
import com.rt.utility.models.User;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class SplashFragment extends Fragment {

    private final String FRAGMENT_TAG = "SPLASH_FRAGMENT";
    private final int TIMEOUT = 10000;
    private final int START_DELAY = 200;
    private final int MAX_DELAY = 1000;

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
        progressBar.setVisibility(View.VISIBLE);
        txtStatus = view.findViewById(R.id.txtStatus);

        // Starts the database setup and user retrieval async task
        TimerTask startSetup = new TimerTask() {
            @Override
            public void run() {
                if(getActivity() == null){
                    return;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setStatusText(R.string.status_db);
                        new DatabaseStatus(SplashFragment.this).execute();
                    }
                });
            }
        };

        // Timeout event in case of an error during the db setup async method
        TimerTask handleTimeout = new TimerTask() {
            @Override
            public void run() {
                if(getActivity() == null){
                    return;
                }
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

    /*
    Launches the Home Activity and finishes the Splash Activity
     */
    private void launchHomeActivity(final User user){
        cancelTimer();

        if(getActivity() == null){
            return;
        }

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

    /*
    Replaces this fragment with the create account fragment
     */
    private void launchCreateAccountFragment(){
        cancelTimer();

        if(getActivity() == null){
            return;
        }

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                SplashActivity splashActivity = (SplashActivity)getActivity();

                if(splashActivity != null){
                    splashActivity.removeFragment(SplashFragment.this);
                    splashActivity.launchCreateAccountFragment();
                }
            }
        });
    }

    /*
    Gets the initialized database service and retrieves the default user from the db
     */
    private User databaseSetup(){
        String[] captions = new String[] {
                "Convert currencies with live rates!",
                "Flip a virtual coin in Coin Flip!",
                "Make your To-Do list in Task Manager!",
                "Scan local wireless networks!"
        };

        setStatusText(captions[(int)(Math.floor(Math.random() * captions.length))]);

        UserDataService dataService = UserDataService.get(getActivity());
        User user = dataService.getDefaultUser();

        cancelTimer();
        delay();

        return user;
    }

    /*
    Update the progress in the horizontal progress bar and briefly pause the UI
     */
    private void delay(){
        JavaUtils.pauseUI(JavaUtils.returnRandomInt(MAX_DELAY) + MAX_DELAY);
    }

    /*
    Cancel the scheduled timer task events
     */
    private void cancelTimer(){
        this.timeout.cancel();
        this.timeout.purge();
    }

    /*
    Set the status of the app loading state in the UI
     */
    private void setStatusText(final String stringResource){
        if(getActivity() == null){
            return;
        }

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                txtStatus.setText(stringResource);
            }
        });
    }

    private void setStatusText(final int stringResource){
        if(getActivity() == null){
            return;
        }

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                txtStatus.setText(stringResource);
            }
        });
    }

    /*
    Connects to the local SQLite db, creates the db if needed, and returns the default user data
    Learned about async tasks in android from https://developer.android.com/reference/android/os/AsyncTask
     */
    private static class DatabaseStatus extends AsyncTask<String, String, User> {

        private WeakReference<SplashFragment> ref;

        DatabaseStatus(SplashFragment context){
            ref = new WeakReference<>(context);
        }

        @Override
        protected User doInBackground(String... args) {
            SplashFragment activity = ref.get();

            if (activity == null) return null;

            try{
                return activity.databaseSetup();
            }
            catch (Exception ex){
                Log.e(activity.FRAGMENT_TAG, Objects.requireNonNull(ex.getMessage()));
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(String... progress) { }

        @Override
        protected void onPostExecute(User user) {
            SplashFragment activity = ref.get();

            if (activity == null) return;

            if(user == null){
                activity.launchCreateAccountFragment();
            }
            else{
                activity.launchHomeActivity(user);
            }
        }
    }
}
