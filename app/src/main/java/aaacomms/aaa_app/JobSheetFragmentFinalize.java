package aaacomms.aaa_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

public class JobSheetFragmentFinalize extends Fragment {

    ImageButton navBtn;
    Button sign;
    private DrawerLayout drawer;

    TimePicker startTimeTP, endTimeTP;
    DatePicker datePicker;

    int startMinute, startHour, endMinute, endHour;

    TextView totalHours, jobNoTV;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_job_sheet_finalize, container, false);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        datePicker = getView().findViewById(R.id.dateDP);
        startTimeTP = getView().findViewById(R.id.startTimeTP);
        endTimeTP = getView().findViewById(R.id.endTimeTP);
        drawer = getActivity().findViewById(R.id.drawer_layout);
        navBtn = getView().findViewById(R.id.navButton);
        totalHours = getView().findViewById(R.id.totalHoursText);
        sign = getView().findViewById(R.id.signButton);
        jobNoTV = getView().findViewById(R.id.jobNoTV);

        endTimeTP.setIs24HourView( true );
        startTimeTP.setIs24HourView( true );

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int minutes = cal.get(Calendar.MINUTE);
        int hours = cal.get(Calendar.HOUR_OF_DAY);

        startMinute = minutes;
        startHour = hours;
        endMinute = minutes;
        endHour = hours;

        setStartTime(getCurrentJob(), String.valueOf(startHour) + String.valueOf(startMinute) );
        setEndTime(getCurrentJob(), String.valueOf(endHour) + String.valueOf(endMinute) );

        setTotalHours();

        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker arg0, int arg1, int arg2, int arg3) {
                String s = " "+arg3+ "/"+ (arg2+1) + "/"+arg1;
                setDate( getCurrentJob(), s );
//                Toast.makeText(getActivity(), s ,Toast.LENGTH_SHORT).show();
            }
        } );

        navBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.START);
            }
        });

        startTimeTP.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                startMinute = minute;
                startHour = hourOfDay;

                if ( startMinute < 10 && startHour < 10 ) {
                    setStartTime(getCurrentJob(), 0 + String.valueOf(startHour) + String.valueOf(startMinute) + 0);
                }
                if ( startMinute >= 10 && startHour < 10 ) {
                    setStartTime(getCurrentJob(), 0 + String.valueOf(startHour) + String.valueOf(startMinute));
                }
                if ( startMinute < 10 && startHour >= 10 ) {
                    setStartTime( getCurrentJob(), String.valueOf(startHour) + String.valueOf(startMinute) + 0);
                }
                if ( startMinute >= 10 && startHour >= 10 ) {
                    setStartTime( getCurrentJob(), String.valueOf(startHour) + String.valueOf(startMinute) );
                }

                setTotalHours();
            }
        });

        endTimeTP.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                endMinute = minute;
                endHour = hourOfDay;

                if ( endMinute < 10 && endHour < 10 ) {
                    setEndTime(getCurrentJob(), 0 + String.valueOf(endHour) + String.valueOf(endMinute) + 0);
                }
                if ( endMinute >= 10 && endHour < 10 ) {
                    setEndTime(getCurrentJob(), 0 + String.valueOf(endHour) + String.valueOf(endMinute));
                }
                if ( endMinute < 10 && endHour >= 10 ) {
                    setEndTime( getCurrentJob(), String.valueOf(endHour) + String.valueOf(endMinute) + 0);
                }
                if ( endMinute >= 10 && endHour >= 10 ) {
                    setEndTime( getCurrentJob(), String.valueOf(endHour) + String.valueOf(endMinute) );
                }

                setTotalHours();
            }
        });

        BottomNavigationView bottomNavigationView = getView().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.action_details:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new JobSheetFragment()).commit();
                        break;
                    case R.id.action_photos:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new JobSheetFragmentPhotos()).commit();
                        break;
                    case R.id.action_finalize:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new JobSheetFragmentFinalize()).commit();
                        break;
                }
                return true;
            }
        });

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Esignature.class);
                startActivity(intent);
            }
        });

        jobNoTV.setText( String.valueOf( getCurrentJob() ) );

    }

    private void setTotalHours() {

        if (startMinute > endMinute) {
            String s = ( ( endHour - startHour ) - 1 ) + "h " + ( 60 + ( endMinute - startMinute ) ) + "m";
            totalHours.setText( s );
            setTotalTime( getCurrentJob(), s );
        } else {
            String s = ( endHour - startHour ) + "h " + ( endMinute - startMinute ) + "m";
            totalHours.setText( s );
            setTotalTime( getCurrentJob(), s );
        }
    }

    private int getNumJobs() {
        SharedPreferences prefs = getContext().getSharedPreferences( getResources().getString(R.string.jobsPrefsString) , Context.MODE_PRIVATE);
        return prefs.getInt( getResources().getString(R.string.numJobsString), 0 );
    }

    private int getIndex(int jobNo) {
        int index = 0;
        int numJobs = getNumJobs();

        for ( int i = 0; i < numJobs; i++ ) {
            SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + i , Context.MODE_PRIVATE);
            int jobNum = prefs.getInt( getResources().getString(R.string.jobNumString) , 0);
            if ( jobNo == jobNum ) {
                index = i;
            }
        }
        return index;
    }

    private int getCurrentJob(){
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) , Context.MODE_PRIVATE);
        return prefs.getInt( getResources().getString(R.string.currentJobString) , 0);
    }

    private void setStartTime(int jobNo, String startTime) {
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString( getResources().getString(R.string.startTimeString) , startTime ).apply();
    }

    private void setEndTime(int jobNo, String endTime) {
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString( getResources().getString(R.string.endTimeString) , endTime ).apply();
    }

    private void setTotalTime(int jobNo, String totalTime) {
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString( getResources().getString(R.string.totalTimeString) , totalTime ).apply();
    }

    private void setDate(int jobNo, String date) {
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString( getResources().getString(R.string.dateString) , date ).apply();
    }

}
