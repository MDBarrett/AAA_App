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
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {

    private DrawerLayout drawer;

    ListView draftJobs, completedJobs;

    ImageButton navBtn;

    List<JobsListDataModel> draftJobsList;
    List<JobsListDataModel> completedJobsList;

    SharedPreferences jobPrefs;
    String jobPreferences = "jobPreferences";

    Boolean appTheme;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Context contextThemeWrapper;

        SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences( "ApplicationPreferences" , Context.MODE_PRIVATE);
        Boolean appTheme = prefs.getBoolean("appTheme", false);

        if ( appTheme ) {
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.darkTheme);
        } else {
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.lightTheme);
        }

        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        return localInflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences( "ApplicationPreferences" , Context.MODE_PRIVATE);
        appTheme = prefs.getBoolean("appTheme", false);
        ScrollView activity = getView().findViewById(R.id.activity_main);

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

    public int getIndex(int jobNo) {
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
        return prefs.getInt( getResources().getString(R.string.indexString), 0 );
    }

    public String[] getOrderedJobs() {
        LinkedList<String> jobsList = new LinkedList<>();
        SharedPreferences prefs = getContext().getSharedPreferences( getResources().getString(R.string.jobsPrefsString) , Context.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet( getResources().getString( R.string.jobsListString), null );

        if( set != null ) {
            ArrayList<Integer> jobIndices = new ArrayList<>();

            for ( String s : set ) {
                jobIndices.add( getIndex( Integer.valueOf( s ) ) );
            }

            Collections.sort( jobIndices );

            for( int i = 0; i < jobIndices.size(); i++ ) {
                for ( String s : set ) {
                    int index = getIndex( Integer.valueOf( s ) );
                    if ( index == jobIndices.get( i ) )
                        jobsList.add( s );
                }
            }
        }

        for ( String s : jobsList   )
            Log.d("orderedJobs", s );

        return jobsList.toArray( new String[ jobsList.size() ] );
    }

    private List<JobsListDataModel> getDraftJobs() {
        List<JobsListDataModel> draftJobs = new ArrayList<>();

        String[] set = getOrderedJobs();

        if ( set != null )
            for ( String s : set ) {
                SharedPreferences prefs = this.getActivity().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + s , Context.MODE_PRIVATE);
                String jobStatus = prefs.getString(getResources().getString(R.string.jobStatusString), null);
                if ( jobStatus != null && jobStatus.equals( "draft" ) ) {
                    String customer = prefs.getString(getResources().getString(R.string.customerString), null);
                    draftJobs.add( new JobsListDataModel( Integer.valueOf(s), customer ) );
                }
            }

        return draftJobs;
    }

    private List<JobsListDataModel> getCompletedJobs() {
        List<JobsListDataModel> completedJobs = new ArrayList<>();

        String[] set = getOrderedJobs();

        if ( set != null )
            for ( String s : set ) {
                SharedPreferences prefs = this.getActivity().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + s , Context.MODE_PRIVATE);
                String jobStatus = prefs.getString(getResources().getString(R.string.jobStatusString), null);
                if ( jobStatus != null && jobStatus.equals( "completed" ) ) {
                    String customer = prefs.getString(getResources().getString(R.string.customerString), null);
                    completedJobs.add( new JobsListDataModel( Integer.valueOf(s), customer ) );
                }
            }

        return completedJobs;
    }

    private void setDraftJobs() {
        draftJobsList = getDraftJobs();
        DraftsListAdapter adapter = new DraftsListAdapter(getActivity(), R.layout.custom_list_draft_jobs, draftJobsList);
        draftJobs.setAdapter( adapter );
    }

    private void setCompletedJobs() {
        completedJobsList = getCompletedJobs();
        DraftsListAdapter adapter = new DraftsListAdapter(getActivity(), R.layout.custom_list_draft_jobs, completedJobsList);
        completedJobs.setAdapter( adapter );
    }
}
