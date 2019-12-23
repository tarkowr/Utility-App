package com.rt.utility;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.rt.utility.dataservice.AppDataService;
import com.rt.utility.helpers.JavaUtils;
import com.rt.utility.models.AppItem;

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

        // Initializes the fragment manager and adds the passed app's fragment to this activity's frame layout
        // Learned about Fragment Managers from Android Programming by The Big Nerd Ranch
        fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.app_fragment_container);

        if(fragment == null){
            fragment = app.getFragment();
            fm.beginTransaction()
                     .add(R.id.app_fragment_container, fragment)
                     .commit();
        }
    }

    /*
    Creates the intent to start this activity and defines the necessary parameters
    Learned about android intents from Android Programming by The Big Nerd Ranch
     */
    public static Intent newIntent(Context context, UUID appId){
        Intent intent = new Intent(context, AppActivity.class);
        intent.putExtra(EXTRA_APP_ID, appId);
        return intent;
    }

    /*
    Retrieves the passed app object from the intent launching this by activity by a private key
     */
    private AppItem getAppFromIntent(){
        UUID id = (UUID)getIntent().getSerializableExtra(EXTRA_APP_ID);
        return AppDataService.get(AppActivity.this).getAppById(id);
    }

    /*
    Override the back button onPress event to finish the activity instead of removing the fragment
     */
    @Override
    public void onBackPressed() {
        finish();
    }

    /*
    Event to destroy this activity and end its lifecycle
     */
    private View.OnClickListener onClickHome = new View.OnClickListener() {
        public void onClick(View view){
            finish();
        }
    };
}
