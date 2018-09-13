package aaacomms.aaa_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class ActivityMain extends AppCompatActivity {

    ImageButton navBtn;
    private DrawerLayout drawer;
    Boolean appTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs = getApplicationContext().getSharedPreferences( "ApplicationPreferences" , Context.MODE_PRIVATE);
        appTheme = prefs.getBoolean("appTheme", false);

        if ( appTheme ) {
            setTheme( R.style.darkTheme );
        } else {
            setTheme( R.style.lightTheme );
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        RelativeLayout activity = findViewById(R.id.activity_main);

        if ( appTheme ) {
            activity.setBackgroundResource( R.color.darkBackground );
        } else {
            activity.setBackgroundResource( R.color.lightBackground );
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2,
                    new JobSheetFragment()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.action_home:
                            startActivity(new Intent(ActivityMain.this, MainActivity.class));
                            break;
                        case R.id.action_details:
                            selectedFragment = new JobSheetFragment();
                            break;
                        case R.id.action_photos:
                            selectedFragment = new JobSheetFragmentPhotos();
                            break;
                        case R.id.action_finalize:
                            selectedFragment = new JobSheetFragmentFinalize();
                            break;
                    }

                    if ( selectedFragment != null )
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2, selectedFragment).commit();

                    return true;
                }
            };
}