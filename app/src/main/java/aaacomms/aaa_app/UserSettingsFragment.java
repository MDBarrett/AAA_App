package aaacomms.aaa_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UserSettingsFragment extends Fragment {

    private DrawerLayout drawer;
    ImageButton navBtn;
    CardView login;

    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_settings, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences( "ApplicationPreferences" , Context.MODE_PRIVATE);
        Boolean appTheme = prefs.getBoolean("appTheme", false);
        LinearLayout activity = getView().findViewById(R.id.activity_main);

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
        login = getView().findViewById(R.id.loginCV);

        context = getActivity();

        navBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.START);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginFragment nextFrag= new LoginFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, nextFrag,"findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

    }

}
