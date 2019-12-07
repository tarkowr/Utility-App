package com.example.utility;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public abstract class SingleFragmentActivity extends AppCompatActivity {
    protected FragmentManager fm;
    protected abstract Fragment returnFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        setupFragmentManager(R.id.fragment_container);
    }

    /*
    Initialize the fragment manager and add the initial fragment to the child class activity
    Referenced Android Programming by The Big Nerd Ranch Guide
     */
    protected void setupFragmentManager(int fragmentContainerId){
        fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(fragmentContainerId);

        if(fragment == null){
            fragment = returnFragment();
            fm.beginTransaction()
                    .add(fragmentContainerId, fragment)
                    .commit();
        }
    }

    /*
    Remove a fragment from the child class activity
     */
    protected void removeFragment(Fragment fragment){
        if(fm == null){
            fm = getSupportFragmentManager();
        }

        fm.beginTransaction()
                .remove(fragment)
                .commit();
    }

    /*
    Replaces the fragment in the child activity class with another fragment
    Referenced https://stackoverflow.com/questions/5658675/replacing-a-fragment-with-another-fragment-inside-activity-group to replace the fragment
     */
    protected void changeFragment(Fragment newFragment){
        if(fm == null){
            fm = getSupportFragmentManager();
        }

        fm.beginTransaction().replace(R.id.fragment_container, newFragment)
                .addToBackStack(null)
                .commit();
    }
}
