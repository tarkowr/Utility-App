package com.example.utility;

import android.content.Context;

import androidx.fragment.app.Fragment;

public class SplashActivity extends SingleFragmentActivity {

    @Override
    protected Fragment returnFragment(){
        return new SplashFragment();
    }

    public void launchCreateAccountFragment(){
        changeFragment(new CreateUserFragment());
    }

    public void removeFragment(Fragment fragment){
        super.removeFragment(fragment);
    }
}
