package aaacomms.aaa_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import java.sql.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class JobSheetFragmentFinalize extends Fragment {

    ImageButton navBtn;
    Button sign, submit;
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
        submit = getView().findViewById(R.id.submitButton);
        jobNoTV = getView().findViewById(R.id.jobNoTV);

        endTimeTP.setIs24HourView( true );
        startTimeTP.setIs24HourView( true );

        if ( getCurrentJob() != 0 ) {

            initializeFields();

        } else {
            startTimeTP.setEnabled( false );
            endTimeTP.setEnabled( false );
            datePicker.setEnabled( false );

            sign.setEnabled( false );
        }

        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker arg0, int arg1, int arg2, int arg3) {
                setDate( getCurrentJob(), arg3, arg2+1, arg1 );
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

                setTime( startMinute, startHour , true);

                setTotalHours();
            }
        });

        endTimeTP.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                endMinute = minute;
                endHour = hourOfDay;

                setTime( endMinute, endHour , false);

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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setJobStatus( getCurrentJob(),  "completed" );
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
            }
        });

        setJobNoTV( getCurrentJob() );

        if ( getCurrentJob() != 0 ) {
            if (fieldsComplete(getCurrentJob())) {
                submit.setEnabled(true);
            } else {
                submit.setEnabled(false);
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if ( getCurrentJob() != 0 ) {
            if (fieldsComplete(getCurrentJob())) {
                submit.setEnabled(true);
            } else {
                submit.setEnabled(false);
            }
        }
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

    private void setDate(int jobNo, int day, int month, int year) {
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt( getResources().getString(R.string.dayString) , day );
        editor.putInt( getResources().getString(R.string.monthString) , month );
        editor.putInt( getResources().getString(R.string.yearString) , year ).apply();
    }

    private void setTime(int minutes, int hours, boolean start){

        if ( minutes < 10 && hours < 10 )
            if ( start )
                setStartTime(getCurrentJob(), 0 + String.valueOf( hours ) + 0 + String.valueOf( minutes ) );
            else
                setEndTime(getCurrentJob(), 0 + String.valueOf( hours ) + 0 + String.valueOf( minutes ) );

        if ( minutes >= 10 && hours < 10 )
            if ( start )
                setStartTime(getCurrentJob(), 0 + String.valueOf( hours ) + String.valueOf( minutes ));
            else
                setEndTime(getCurrentJob(), 0 + String.valueOf( hours ) + String.valueOf( minutes ));

        if ( minutes < 10 && hours >= 10 )
            if ( start )
                setStartTime( getCurrentJob(), String.valueOf( hours ) + 0 + String.valueOf( minutes ) );
            else
                setEndTime( getCurrentJob(), String.valueOf( hours ) + 0 + String.valueOf( minutes ) );

        if ( minutes >= 10 && hours >= 10 )
            if ( start )
                setStartTime( getCurrentJob(), String.valueOf( hours ) + String.valueOf( minutes ) );
            else
                setEndTime( getCurrentJob(), String.valueOf( hours ) + String.valueOf( minutes ) );

    }

    private void setJobNoTV(int jobNo) {
        if ( jobNo == 0 ) {
            jobNoTV.setText(R.string.noJobSelectedTV);
            jobNoTV.setTextSize( 18 );
            jobNoTV.setTextColor(Color.parseColor("#FF0000") );
        } else {
            jobNoTV.setText(String.valueOf(jobNo));
            jobNoTV.setTextSize( 28 );
            jobNoTV.setTextColor(Color.parseColor("#FFFFFF") );
        }
    }

    private String getStartTime(int jobNo) {
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , Context.MODE_PRIVATE);
        return prefs.getString( getResources().getString(R.string.startTimeString) , null);
    }

    private String getEndTime(int jobNo) {
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , Context.MODE_PRIVATE);
        return prefs.getString( getResources().getString(R.string.endTimeString) , null);
    }

    private int getDay(int jobNo) {
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , Context.MODE_PRIVATE);
        return prefs.getInt( getResources().getString(R.string.dayString) , 0);
    }

    private int getMonth(int jobNo) {
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , Context.MODE_PRIVATE);
        return prefs.getInt( getResources().getString(R.string.monthString) , 0);
    }

    private int getYear(int jobNo) {
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , Context.MODE_PRIVATE);
        return prefs.getInt( getResources().getString(R.string.yearString) , 0);
    }

    private void initializeFields() {
        startTimeTP.setEnabled( true );
        endTimeTP.setEnabled( true );
        datePicker.setEnabled( true );

        sign.setEnabled( true );

        if ( getDay( getCurrentJob() ) == 0 && getMonth( getCurrentJob() ) == 0 && getYear( getCurrentJob() ) == 0 ) {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            datePicker.updateDate(year, month, day);
            setDate( getCurrentJob(), day, month, year);
        } else {
            datePicker.updateDate( getYear( getCurrentJob() ), getMonth( getCurrentJob() ) - 1, getDay( getCurrentJob() ) );
        }

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int minutes = cal.get(Calendar.MINUTE);
        int hours = cal.get(Calendar.HOUR_OF_DAY);

        startMinute = minutes;
        startHour = hours;
        endMinute = minutes;
        endHour = hours;

        if (getStartTime(getCurrentJob()) != null && getEndTime(getCurrentJob()) != null) {
            String startTime = getStartTime(getCurrentJob());
            startHour = Integer.valueOf(startTime.substring(0, startTime.length() / 2));
            startMinute = Integer.valueOf(startTime.substring(startTime.length() / 2));

            String endTime = getEndTime(getCurrentJob());
            endHour = Integer.valueOf(endTime.substring(0, startTime.length() / 2));
            endMinute = Integer.valueOf(endTime.substring(startTime.length() / 2));
        }

        setTime(startMinute, startHour, true);
        setTime(endMinute, endHour, false);

        startTimeTP.setHour(startHour);
        startTimeTP.setMinute(startMinute);

        endTimeTP.setHour(endHour);
        endTimeTP.setMinute(endMinute);

        setTotalHours();
    }

    private Boolean fieldsComplete(int jobNo) {
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , Context.MODE_PRIVATE);
        ArrayList<String> missingFields = new ArrayList<>();

        if ( prefs.getString( getResources().getString(R.string.customerString), null).equals("") ) {
            missingFields.add( "customer name" );
            Toast.makeText(getActivity(), "missing CUSTOMER NAME", Toast.LENGTH_LONG).show();
        }
        if ( prefs.getString( getResources().getString(R.string.firstNameString), null).equals("") ) {
            missingFields.add( "first name" );
            Toast.makeText(getActivity(), "missing FIRST NAME", Toast.LENGTH_LONG).show();
        }
        if ( prefs.getString( getResources().getString(R.string.lastNameString), null).equals("") ) {
            missingFields.add( "last name" );
            Toast.makeText(getActivity(), "missing LAST", Toast.LENGTH_LONG).show();
        }
        if ( !jobSheetSigned( getCurrentJob() ) ) {
            missingFields.add( "customer signature" );
            Toast.makeText(getActivity(), "missing CUSTOMER NAME", Toast.LENGTH_LONG).show();
        }

        if ( missingFields.size() > 0 ) {
            String listString = "missing ";
            for ( String s : missingFields ) {
                listString += s + ", ";
            }
            Toast.makeText(getActivity(), listString.substring(0, (listString.length() - 2) ), Toast.LENGTH_LONG).show();
            missingFields.clear();
            return  false;
        }

        return true;
    }

    private Boolean jobSheetSigned(int jobNo) {
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , Context.MODE_PRIVATE);
        return prefs.getBoolean( getResources().getString(R.string.signedString), false);
    }

    private void setJobStatus(int jobNo, String jobStatus) {
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString( getResources().getString(R.string.jobStatusString) , jobStatus ).apply();
    }

}
