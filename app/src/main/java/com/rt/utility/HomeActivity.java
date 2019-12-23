package com.rt.utility;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.rt.utility.dataservice.UserDataService;
import com.rt.utility.models.User;
import com.google.android.material.snackbar.Snackbar;

import java.util.UUID;

public class HomeActivity extends AppCompatActivity {

    public static final String EXTRA_USER_ID = "utility.user.id";
    protected FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        welcomeUser();

        // Initializes the fragment manager and adds the app list fragment to the home frame container
        // Learned about fragment managers from Android Programming by The Big Nerd Ranch
        fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.home_fragment_container);

        if(fragment == null){
            fragment = new AppListFragment();
            fm.beginTransaction()
                    .add(R.id.home_fragment_container, fragment)
                    .commit();
        }
    }

    /*
    Creates the intent to start this activity and defines the necessary parameters
    Learned about android intents from Android Programming by The Big Nerd Ranch
     */
    public static Intent newIntent(Context context, UUID userId){
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
    }

    /*
    Retrieves the user object from the intent and welcomes the user with a snack bar message
     */
    private void welcomeUser(){
        String welcomeMsg = getResources().getString(R.string.txt_welcome);
        int sbFontSize = 20;

        User user = getUserFromIntent();

        if(!user.getUsername().isEmpty()){
            welcomeMsg = "Welcome, " + user.getUsername() + "!";
        }

        showSnackBar(welcomeMsg, sbFontSize);
    }

    /*
    Retrieves the passed user object from the intent launching this by activity by a private key
     */
    private User getUserFromIntent(){
        UUID id = (UUID)getIntent().getSerializableExtra(HomeActivity.EXTRA_USER_ID);
        return UserDataService.get(HomeActivity.this).getUser(id);
    }

    /*
    Displays the welcome user snack bar (a widget from material design)
    Learned about snack bars from https://developer.android.com/reference/android/support/design/widget/Snackbar
        and https://stackoverflow.com/questions/33517255/changing-typeface-of-snackbar/33517490
     */
    private void showSnackBar(String msg, Integer fontSize){
        Snackbar sb = Snackbar.make(findViewById(R.id.homeRootContstraintLayout), msg,
                Snackbar.LENGTH_LONG);

        TextView sbTextView = sb.getView().findViewById(R.id.snackbar_text);
        sbTextView.setTextSize( fontSize );
        sbTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        sb.show();
    }
}
