package aaacomms.aaa_app;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class JobSheetFragmentFinalize extends Fragment {

    ImageButton navBtn;
    private DrawerLayout drawer;
    Button sign, submit;

    EditText startTimeET, endTimeET, dateET, totalTimeET;

    int startMinute, startHour, endMinute, endHour;

    TextView totalHours, jobNoTV;

    TextView details, photos, finalize;

    Boolean appTheme;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.darkTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        return localInflater.inflate(R.layout.fragment_job_sheet_finalize, container, false);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        appTheme = true;

        if ( getActivity() != null && getView() != null ) {
            startTimeET = getView().findViewById(R.id.startTimeET);
            endTimeET = getView().findViewById(R.id.endTimeET);
            dateET = getView().findViewById(R.id.dateET);
            totalHours = getView().findViewById(R.id.totalHoursText);
            totalTimeET = getView().findViewById(R.id.totalTimeET);
            sign = getView().findViewById(R.id.signButton);
            submit = getView().findViewById(R.id.submitButton);
            jobNoTV = getView().findViewById(R.id.jobNoTV);

            details = getView().findViewById(R.id.detailsBtn);
            photos = getView().findViewById(R.id.photosBtn);
            finalize = getView().findViewById(R.id.finalizeBtn);

            drawer = getActivity().findViewById(R.id.drawer_layout);
            navBtn = getView().findViewById(R.id.navButton);
            navBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { drawer.openDrawer(Gravity.START);
                }
            });
        }

        totalTimeET.setEnabled( false );

        if ( getCurrentJob() != 0 ) {

            sign.setEnabled( false );
            sign.setBackgroundResource( R.drawable.box_disabled );
            initializeFields();

        } else {
            sign.setEnabled( false );
            sign.setBackgroundResource( R.drawable.box_disabled );
            submit.setEnabled( false );
            submit.setBackgroundResource( R.drawable.box_disabled );
            startTimeET.setEnabled( false );
            endTimeET.setEnabled( false );
            dateET.setEnabled( false );
        }

        startTimeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog( getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        startMinute = minutes;
                        startHour = hourOfDay;
                        setTime( startMinute, startHour, true);
                        startTimeET.setText( getStartTime( getCurrentJob() ) );
                        setTotalHours();
                        updateSignBtn();
                    }
                }, currentHour, currentMinute, false);

                timePickerDialog.show();

                Button nbutton = timePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                Button pbutton = timePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                nbutton.setTextColor(Color.parseColor("#00897B"));
                pbutton.setTextColor(Color.parseColor("#00897B"));

            }
        });

        endTimeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        endMinute = minutes;
                        endHour = hourOfDay;
                        setTime( endMinute, endHour , false);
                        endTimeET.setText( getEndTime( getCurrentJob() ) );
                        setTotalHours();
                        updateSignBtn();
                    }
                }, currentHour, currentMinute, false);

                timePickerDialog.show();

                Button nbutton = timePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                Button pbutton = timePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                nbutton.setTextColor(Color.parseColor("#00897B"));
                pbutton.setTextColor(Color.parseColor("#00897B"));

            }
        });

        dateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int dayOfMonth;
                int month;
                int year;

                if ( getDay( getCurrentJob() ) == 0 && getMonth( getCurrentJob() ) == 0 && getYear( getCurrentJob() ) == 0 ) {
                    Calendar calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                } else {
                    dayOfMonth = getDay( getCurrentJob() );
                    month = getMonth( getCurrentJob() );
                    year = getYear( getCurrentJob() );
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                setDate( getCurrentJob(), day, month + 1, year);
                                dateET.setText( getString( R.string.dateFormat, day, "/", month + 1, "/", year ) );
                            }
                        }, year, month, dayOfMonth);

                datePickerDialog.show();

                Button nbutton = datePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                Button pbutton = datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                nbutton.setTextColor(Color.parseColor("#00897B"));
                pbutton.setTextColor(Color.parseColor("#00897B"));

            }
        });

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new JobSheetFragment()).commit();
            }
        });

        photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new JobSheetFragmentPhotos()).commit();
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
                if ( submitJob() ) {
                    setJobStatus(getCurrentJob());
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new HomeFragment()).commit();
                } else {
                    Toast.makeText(getContext(), "failed to submit job", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setJobNoTV( getCurrentJob() );

        if ( getCurrentJob() > 0 ) {
            if (fieldsComplete(getCurrentJob())) {
                submit.setEnabled(true);
                submit.setBackgroundResource( R.drawable.box );
            } else {
                submit.setEnabled(false);
                submit.setBackgroundResource( R.drawable.box_disabled );
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if ( getCurrentJob() != 0 ) {
            if (fieldsComplete(getCurrentJob())) {
                submit.setEnabled(true);
                SharedPreferences prefs;
                if ( getActivity() != null ) {
                    prefs = getActivity().getApplicationContext().getSharedPreferences("ApplicationPreferences", Context.MODE_PRIVATE);
                    Boolean appTheme = prefs.getBoolean("appTheme", false);
                    submit.setBackgroundResource(R.drawable.box);
                }
            } else {
                submit.setEnabled(false);
                submit.setBackgroundResource( R.drawable.box_disabled );
            }
        }
    }

    private void setTotalHours() {

        if (startMinute > endMinute) {
            String s = ( ( endHour - startHour ) - 1 ) + "h " + ( 60 + ( endMinute - startMinute ) ) + "m";
            totalTimeET.setText( s );
            setTotalTime( getCurrentJob(), s );
        } else {
            String s = ( endHour - startHour ) + "h " + ( endMinute - startMinute ) + "m";
            totalTimeET.setText( s );
            setTotalTime( getCurrentJob(), s );
        }
    }

    private int getCurrentJob(){
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString), Context.MODE_PRIVATE);
            return prefs.getInt(getResources().getString(R.string.currentJobString), 0);
        }
        return 0;
    }

    private void setStartTime(int jobNo, String startTime) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(getResources().getString(R.string.startTimeString), startTime).apply();
        }
    }

    private void setEndTime(int jobNo, String endTime) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(getResources().getString(R.string.endTimeString), endTime).apply();
        }
    }

    private void setTotalTime(int jobNo, String totalTime) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(getResources().getString(R.string.totalTimeString), totalTime).apply();
        }
    }

    private void setDate(int jobNo, int day, int month, int year) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(getResources().getString(R.string.dayString), day);
            editor.putInt(getResources().getString(R.string.monthString), month);
            editor.putInt(getResources().getString(R.string.yearString), year).apply();
        }
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
            jobNoTV.setTextSize( 28 );
        } else {
            jobNoTV.setText(String.valueOf(jobNo));
            jobNoTV.setTextSize( 28 );
        }
    }

    @Nullable
    private String getStartTime(int jobNo) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
            return prefs.getString(getResources().getString(R.string.startTimeString), null);
        }
        return null;
    }

    @Nullable
    private String getEndTime(int jobNo) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
            return prefs.getString(getResources().getString(R.string.endTimeString), null);
        }
        return  null;
    }

    private int getDay(int jobNo) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
            return prefs.getInt(getResources().getString(R.string.dayString), 0);
        }
        return 0;
    }

    private int getMonth(int jobNo) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
            return prefs.getInt(getResources().getString(R.string.monthString), 0);
        }
        return 0;
    }

    private int getYear(int jobNo) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
            return prefs.getInt(getResources().getString(R.string.yearString), 0);
        }
        return 0;
    }

    private void initializeFields() {

        updateSignBtn();
        startTimeET.setEnabled( true );
        endTimeET.setEnabled( true );
        dateET.setEnabled( true );

        if ( getDay( getCurrentJob() ) == 0 && getMonth( getCurrentJob() ) == 0 && getYear( getCurrentJob() ) == 0 ) {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            setDate( getCurrentJob(), day, month, year);
            dateET.setText( getString(R.string.dateFormat, day, "/", month, "/", year ) );
        } else {
            dateET.setText( getString(R.string.dateFormat, getDay( getCurrentJob() ) , "/" , getMonth( getCurrentJob() ) , "/" ,  getYear( getCurrentJob() ) ) );
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

        if ( getStartTime(getCurrentJob()) != null ) {
            String startTime = getStartTime(getCurrentJob());
            if ( startTime != null ) {
                startHour = Integer.valueOf(startTime.substring(0, startTime.length() / 2));
                startMinute = Integer.valueOf(startTime.substring(startTime.length() / 2));
            }
            setTime( startMinute, startHour, true);
            startTimeET.setText( getStartTime( getCurrentJob() ) );
        }

        if ( getEndTime(getCurrentJob()) != null ) {
            String endTime = getEndTime(getCurrentJob());
            if ( endTime != null ) {
                endHour = Integer.valueOf(endTime.substring(0, endTime.length() / 2));
                endMinute = Integer.valueOf(endTime.substring(endTime.length() / 2));
            }
            setTime( endMinute, endHour, false);
            endTimeET.setText( getEndTime( getCurrentJob() ) );
            setTotalHours();
        }

    }

    private Boolean fieldsComplete(int jobNo) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
            ArrayList<String> missingFields = new ArrayList<>();

            if (prefs.getString(getResources().getString(R.string.customerString), "").equals("")) {
                missingFields.add("customer name");
            }
            if (prefs.getString(getResources().getString(R.string.firstNameString), "").equals("")) {
                missingFields.add("first name");
            }
            if (prefs.getString(getResources().getString(R.string.lastNameString), "").equals("")) {
                missingFields.add("last name");
            }
            if (prefs.getString(getResources().getString(R.string.startTimeString), null) == null) {
                missingFields.add("start time");
            }
            if (prefs.getString(getResources().getString(R.string.endTimeString), null) == null) {
                missingFields.add("end time");
            }
            if (!jobSheetSigned(getCurrentJob())) {
                missingFields.add("customer signature");
            }

            if (missingFields.size() > 0) {
                String listString;
                StringBuilder builder = new StringBuilder();
                builder.append( "missing " );
                listString = builder.toString();
                for (int i = 0; i < missingFields.size(); i++)
                    if (i == missingFields.size() - 2) {
                        builder.append( missingFields.get(i) );
                        builder.append( " and " );
                        listString = builder.toString();
                    } else {
                        builder.append( missingFields.get(i) );
                        builder.append( ", " );
                        listString = builder.toString();
                    }
                Toast.makeText(getActivity(), listString.substring(0, (listString.length() - 2)), Toast.LENGTH_LONG).show();
                missingFields.clear();
                return false;
            }
        } else
            return false;
        return true;
    }

    private Boolean jobSheetSigned(int jobNo) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
            return prefs.getBoolean(getResources().getString(R.string.signedString), false);
        }
        return false;
    }

    private void setJobStatus(int jobNo) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(getResources().getString(R.string.jobStatusString), "completed").apply();
        }
    }

    @Nullable
    private String getFirstName(int jobNo) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
            return prefs.getString(getResources().getString(R.string.firstNameString), null);
        }
        return  null;
    }

    @Nullable
    private String getLastName(int jobNo) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
            return prefs.getString(getResources().getString(R.string.lastNameString), null);
        }
        return  null;
    }

    private void updateSignBtn() {
        if( getEndTime( getCurrentJob() ) != null && getEndTime( getCurrentJob() ) != null && getFirstName( getCurrentJob() ) != null && getLastName( getCurrentJob() ) != null ) {
            sign.setEnabled(true);
            if ( getActivity() != null ) {
                SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences("ApplicationPreferences", Context.MODE_PRIVATE);
                Boolean appTheme = prefs.getBoolean("appTheme", false);
                sign.setBackgroundResource(R.drawable.box);
            }
        }
    }

    private Boolean submitJob() {
        AzureID azureID = new AzureID();
        return azureID.getLoginStatus( getContext() );
    }

}
