package com.example.utility;

import androidx.fragment.app.Fragment;

public class SplashActivity extends SingleFragmentActivity {

    @Override
    protected Fragment returnFragment(){
        return new SplashFragment();
    }
}
