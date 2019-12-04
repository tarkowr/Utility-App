package com.example.utility;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.utility.dataservice.UserDataService;
import com.example.utility.models.User;
import com.google.android.material.snackbar.Snackbar;

import java.util.UUID;

public class HomeActivity extends AppCompatActivity {

    public static final String EXTRA_USER_ID = "utility.user.id";
    protected FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        String welcomeMsg = getResources().getString(R.string.txt_welcome);
        Integer sbFontSize = 20;

        User user = getUserFromIntent();

        if(!user.getUsername().isEmpty()){
            welcomeMsg = "Welcome, " + user.getUsername() + "!";
        }

        showWelcomeSnackBar(welcomeMsg, sbFontSize);

        fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.home_fragment_container);

        if(fragment == null){
            fragment = new AppListFragment();
            fm.beginTransaction()
                    .add(R.id.home_fragment_container, fragment)
                    .commit();
        }
    }

    public static Intent newIntent(Context context, UUID userId){
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
    }

    private User getUserFromIntent(){
        UUID id = (UUID)getIntent().getSerializableExtra(HomeActivity.EXTRA_USER_ID);
        return UserDataService.get(HomeActivity.this).getUser(id);
    }

    private void showWelcomeSnackBar(String msg, Integer fontSize){
        Snackbar sb = Snackbar.make(findViewById(R.id.homeRootContstraintLayout), msg,
                Snackbar.LENGTH_LONG);

        TextView sbTextView = sb.getView().findViewById(R.id.snackbar_text);
        sbTextView.setTextSize( fontSize );
        sbTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        sb.show();
    }
}
