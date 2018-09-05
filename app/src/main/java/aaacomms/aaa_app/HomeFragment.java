package aaacomms.aaa_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private DrawerLayout drawer;

    ListView draftJobs, completedJobs;

    ImageButton navBtn;

    ArrayAdapter<String> draftsAdapter;
    ArrayList<String> draftJobsList = new ArrayList<>();
    ArrayAdapter<String> completedAdapter;
    ArrayList<String> completedJobsList = new ArrayList<>();

    SharedPreferences jobPrefs;
    String jobPreferences = "jobPreferences";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        drawer = getActivity().findViewById(R.id.drawer_layout);
        navBtn = getView().findViewById(R.id.navButton);
        draftJobs = getView().findViewById(R.id.draftJobsLV);
        completedJobs = getView().findViewById(R.id.completedJobsLV);

        jobPrefs = this.getActivity().getSharedPreferences(jobPreferences, Context.MODE_PRIVATE);

        navBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.START);
            }
        });

        draftJobs.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        completedJobs.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        setDraftJobs();
        setCompletedJobs();

    }

    private ArrayList<String> getDraftJobs() {
        ArrayList<String> draftjobs = new ArrayList<>();

        int numJobs = getNumJobs();

        for ( int i = 0; i < numJobs; i++ ) {
            SharedPreferences prefs = this.getActivity().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + i , Context.MODE_PRIVATE);
            String jobStatus = prefs.getString(getResources().getString(R.string.jobStatusString), null);
            if ( jobStatus != null && jobStatus.equals( "draft" ) ) {
                int jobNo = prefs.getInt(getResources().getString(R.string.jobNumString), 0);
                String customer = prefs.getString(getResources().getString(R.string.customerString), null);
                draftjobs.add(jobNo + ": " + customer);
            }
        }

        return draftjobs;
    }

    private ArrayList<String> getCompletedJobs() {
        ArrayList<String> completedJobs = new ArrayList<>();

        int numJobs = getNumJobs();

        for ( int i = 0; i < numJobs; i++ ) {
            SharedPreferences prefs = this.getActivity().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + i , Context.MODE_PRIVATE);
            String jobStatus = prefs.getString(getResources().getString(R.string.jobStatusString), null);
            if ( jobStatus != null && jobStatus.equals( "completed" ) ) {
                int jobNo = prefs.getInt(getResources().getString(R.string.jobNumString), 0);
                String customer = prefs.getString(getResources().getString(R.string.customerString), null);
                completedJobs.add(jobNo + ": " + customer);
            }
        }

        return completedJobs;
    }

    private int getNumJobs() {
        SharedPreferences prefs = getContext().getSharedPreferences( getResources().getString(R.string.jobsPrefsString) , Context.MODE_PRIVATE);
        return prefs.getInt( getResources().getString(R.string.numJobsString), 0 );
    }

    private void setDraftJobs() {
        draftJobsList = getDraftJobs();

        draftsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, draftJobsList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                tv.setTextColor(Color.WHITE);
                return view;
            }
        };
        draftJobs.setAdapter( draftsAdapter );

        draftsAdapter.notifyDataSetChanged();
    }

    private void setCompletedJobs() {
        completedJobsList = getCompletedJobs();

        completedAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, completedJobsList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                tv.setTextColor(Color.WHITE);
                return view;
            }
        };
        completedJobs.setAdapter( completedAdapter );

        completedAdapter.notifyDataSetChanged();
    }
}
