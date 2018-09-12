package aaacomms.aaa_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SwitchCompat;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AppSettingsFragment extends Fragment {

    private DrawerLayout drawer;
    ImageButton navBtn;
    Button clearData;

    Boolean savePhotos, appTheme;

    TextView prompt;

    SharedPreferences prefs;
    String appPrefs = "ApplicationPreferences";

    SwitchCompat savePhotosSW, appThemeSW;

    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_app_settings, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        prefs = getActivity().getApplicationContext().getSharedPreferences( appPrefs , Context.MODE_PRIVATE);
        appTheme = prefs.getBoolean("appTheme", false);
        RelativeLayout activity = getView().findViewById(R.id.activity_main);

        if ( appTheme ) {
            getActivity().setTheme( R.style.darkTheme );
            activity.setBackgroundResource( R.color.darkBackground );
        } else {
            getActivity().setTheme( R.style.lightTheme );
            activity.setBackgroundResource( R.color.lightBackground );
        }

        super.onActivityCreated(savedInstanceState);

        drawer = getActivity().findViewById(R.id.drawer_layout);
        navBtn = getView().findViewById(R.id.navButton);
        clearData = getView().findViewById(R.id.clearDataButton);
        savePhotosSW = getView().findViewById(R.id.savePhotosSwitch);
        appThemeSW = getView().findViewById(R.id.appThemeSwitch);

        context = getActivity();

        savePhotos = prefs.getBoolean("savePhotos", false);      //FALSE for LIGHT, TRUE for DARK

        savePhotosSW.setChecked(savePhotos);
        appThemeSW.setChecked(appTheme);

        navBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.START);
            }
        });

        clearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.warning_reset, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.DialogTheme);
                alertDialogBuilder.setView(promptsView);

                alertDialogBuilder.setCancelable(true).setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                clearAllData();
                                Toast.makeText(getActivity().getApplicationContext(),"Job Sheet Data Cleared",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        savePhotosSW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                if (isChecked) {
                    savePhotos = true;
                    //noinspection ConstantConditions
                    editor.putBoolean("savePhotos", savePhotos).apply();
                } else {
                    savePhotos = false;
                    //noinspection ConstantConditions
                    editor.putBoolean("savePhotos", savePhotos).apply();
                }
            }
        });

        appThemeSW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                if (isChecked) {
                    appTheme = true;
                    //noinspection ConstantConditions
                    editor.putBoolean("appTheme", appTheme).apply();
                    getActivity().setTheme( R.style.darkTheme );
                } else {
                    appTheme = false;
                    //noinspection ConstantConditions
                    editor.putBoolean("appTheme", appTheme).apply();
                    getActivity().setTheme( R.style.lightTheme );
                }

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AppSettingsFragment()).commit();

            }
        });

    }

    private void clearAllData() {
        int numJobs = getNumJobs();

        for ( int i = 0; i < numJobs; i++ ) {
            SharedPreferences prefs = getContext().getSharedPreferences( getResources().getString(R.string.jobsPrefsString) + i , Context.MODE_PRIVATE );
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear().apply();
        }
        SharedPreferences prefs = getContext().getSharedPreferences( getResources().getString(R.string.jobsPrefsString), Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear().apply();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( getActivity() );
        SharedPreferences.Editor editor2 = preferences.edit();
        editor2.clear().apply();
    }

    private int getNumJobs() {
        SharedPreferences prefs = getContext().getSharedPreferences( getResources().getString(R.string.jobsPrefsString) , Context.MODE_PRIVATE);
        return prefs.getInt( getResources().getString(R.string.numJobsString), 0 );
    }

}
