package com.example.utility;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.utility.apps.CurrencyExchangeAppActivity;
import com.example.utility.dataservice.UserDataService;
import com.example.utility.models.AppItem;
import com.example.utility.models.User;

import java.util.UUID;

public class HomeActivity extends AppCompatActivity {

    public static final String EXTRA_USER_ID = "utility.user.id";
    protected FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        TextView welcomeTitle = findViewById(R.id.txtWelcome);

        User user = getUserFromIntent();

        if(!user.getUsername().isEmpty()){
            welcomeTitle.setText("Welcome, " + user.getUsername() + "!");
        }

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
}
