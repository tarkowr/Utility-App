package com.example.utility;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.utility.dataservice.AppDataService;
import com.example.utility.helpers.JavaUtils;
import com.example.utility.models.AppItem;

import java.util.UUID;

public class AppActivity extends AppCompatActivity {

    public static final String EXTRA_APP_ID = "utility.app.id";
    protected FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        AppItem app = getAppFromIntent();
        getSupportActionBar().setTitle(JavaUtils.FormatActionBarText(app.getName(), AppActivity.this));

        ImageView home = findViewById(R.id.imgHome);
        home.setOnClickListener(onClickHome);

        fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.app_fragment_container);

        if(fragment == null){
            fragment = app.getFragment();
            fm.beginTransaction()
                     .add(R.id.app_fragment_container, fragment)
                     .commit();
        }
    }

    public static Intent newIntent(Context context, UUID appId){
        Intent intent = new Intent(context, AppActivity.class);
        intent.putExtra(EXTRA_APP_ID, appId);
        return intent;
    }

    private AppItem getAppFromIntent(){
        UUID id = (UUID)getIntent().getSerializableExtra(EXTRA_APP_ID);
        return AppDataService.get(AppActivity.this).getAppById(id);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    View.OnClickListener onClickHome = new View.OnClickListener() {
        public void onClick(View view){
            finish();
        }
    };
}