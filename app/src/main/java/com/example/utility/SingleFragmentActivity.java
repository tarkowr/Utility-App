package com.example.utility;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public abstract class SingleFragmentActivity extends AppCompatActivity {
    private FragmentManager fm;
    protected abstract Fragment returnFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null){
            fragment = returnFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    protected void removeFragment(Fragment fragment){
        if(fm == null){
            fm = getSupportFragmentManager();
        }

        fm.beginTransaction()
                .remove(fragment)
                .commit();
    }

    /*
    https://stackoverflow.com/questions/5658675/replacing-a-fragment-with-another-fragment-inside-activity-group
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
