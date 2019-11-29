package com.example.utility;

import androidx.fragment.app.Fragment;

public class SplashActivity extends SingleFragmentActivity {

    @Override
    protected Fragment returnFragment(){
        return new SplashFragment();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void launchCreateAccountFragment(){
        changeFragment(new CreateUserFragment());
    }

    public void removeFragment(Fragment fragment){
        super.removeFragment(fragment);
    }

    public void finishActivity(){
        finish();
    }
}
