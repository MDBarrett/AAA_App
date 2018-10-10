package aaacomms.aaa_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

public class AppSettingsFragment extends Fragment {

    private DrawerLayout drawer;
    ImageButton navBtn;
    Button clearData;

    Boolean savePhotos, appTheme;

    TextView prompt;

    SharedPreferences prefs;
    String appPrefs;

    SwitchCompat savePhotosSW, appThemeSW;

    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.darkTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        return localInflater.inflate(R.layout.fragment_app_settings, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if ( getActivity()!= null && getView() != null ) {
            drawer = getActivity().findViewById(R.id.drawer_layout);
            navBtn = getView().findViewById(R.id.navButton);
            clearData = getView().findViewById(R.id.clearDataButton);
            savePhotosSW = getView().findViewById(R.id.savePhotosSwitch);
            appThemeSW = getView().findViewById(R.id.appThemeSwitch);
        }

        savePhotosSW.setEnabled( false );
        appThemeSW.setEnabled( false );

        context = getActivity();

//        savePhotos = prefs.getBoolean("savePhotos", false);      //FALSE for LIGHT, TRUE for DARK

//        savePhotosSW.setChecked(savePhotos);
//        appThemeSW.setChecked(appTheme);

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
                View promptsView = li.inflate(R.layout.warning_reset, new LinearLayout(context), false);
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

                Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                nbutton.setTextColor(Color.parseColor("#00897B"));
                pbutton.setTextColor(Color.parseColor("#00897B"));
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

//        appThemeSW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
////                SharedPreferences.Editor editor = prefs.edit();
////                if (isChecked) {
////                    appTheme = true;
////                    editor.putBoolean(getResources().getString(R.string.appTheme), appTheme).apply();
////                    getActivity().setTheme( R.style.darkTheme );
////                } else {
////                    appTheme = false;
////                    editor.putBoolean(getResources().getString(R.string.appTheme), appTheme).apply();
////                    getActivity().setTheme( R.style.lightTheme );
////                }
////
////                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
////                        new AppSettingsFragment()).commit();
//
//            }
//        });

    }

    private void clearAllData() {
        Set<String> set = getJobsSet();
        if ( set != null )
            for ( String s : set ) {
                SharedPreferences prefs;
                if( getContext() != null ) {
                    prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + s, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.clear().apply();
                }
            }
        SharedPreferences prefs;
        if( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear().apply();
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( getActivity() );
        SharedPreferences.Editor editor2 = preferences.edit();
        editor2.clear().apply();
    }

    public Set<String> getJobsSet() {
        SharedPreferences prefs;
        if( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString), Context.MODE_PRIVATE);
            return prefs.getStringSet(getResources().getString(R.string.jobsListString), null);
        }
        return new HashSet<>();
    }
}
