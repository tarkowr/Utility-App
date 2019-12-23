package com.rt.utility;

import androidx.fragment.app.Fragment;

public class SplashActivity extends SingleFragmentActivity {

    /*
    Return the initial fragment in the Splash Activity
     */
    @Override
    protected Fragment returnFragment(){
        return new SplashFragment();
    }

    /*
    Override the back button onPress event to finish the activity instead of removing the fragment
     */
    @Override
    public void onBackPressed() {
        finish();
    }

    /*
    Replace the current fragment with a create user fragment
     */
    public void launchCreateAccountFragment(){
        changeFragment(new CreateUserFragment());
    }

    /*
    Remove a fragment from this activity
     */
    public void removeFragment(Fragment fragment){
        super.removeFragment(fragment);
    }

    /*
    Destroys this activity and ends its lifecycle
     */
    public void finishActivity(){
        finish();
    }
}
