package aaacomms.aaa_app;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class JobReporterFragment extends Fragment {

    Button submit, sign;

    EditText addText, firstET, lastET, customerET, jobNoET;

    DatePicker dateDP;

    TimePicker startTimeTP, endTimeTP;

    TextView totalHours;

    ImageButton navBtn;

    private DrawerLayout drawer;

    int startMinute, startHour, endMinute, endHour;

    private static final int WRITE_EXTERNAL_STORAGE = 200;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){

        if (requestCode == WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText( getActivity(), "Permission was not granted", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions,grantResults);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_job_reporter, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        submit = getView().findViewById(R.id.submitButton);
        startTimeTP = getView().findViewById(R.id.startTimeTP);
        endTimeTP = getView().findViewById(R.id.endTimeTP);
        dateDP = getView().findViewById(R.id.dateDP);
        totalHours = getView().findViewById(R.id.totalHoursText);
        sign = getView().findViewById(R.id.signButton);
        addText = getView().findViewById(R.id.additionalText);
        firstET = getView().findViewById(R.id.firstNameET);
        lastET = getView().findViewById(R.id.lastNameET);
        customerET = getView().findViewById(R.id.customerET);
        jobNoET = getView().findViewById(R.id.jobNumberET);

        drawer = getActivity().findViewById(R.id.drawer_layout);
        navBtn = getView().findViewById(R.id.navButton);

        startTimeTP.setIs24HourView( true );
        endTimeTP.setIs24HourView( true );

        navBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.START);
            }
        });

        jobNoET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {      //hide job number hint on click
                if (hasFocus)
                    jobNoET.setHint("");
                else
                    jobNoET.setHint(R.string.jobNo);
            }
        });

        customerET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {      //hide customer hint on click
                if (hasFocus)
                    customerET.setHint("");
                else
                    customerET.setHint(R.string.customer);
            }
        });

        firstET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {      //hide first name hint on click
                if (hasFocus)
                    firstET.setHint("");
                else
                    firstET.setHint(R.string.firstName);
            }
        });

        lastET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {      //hide last name hint on click
                if (hasFocus)
                    lastET.setHint("");
                else
                    lastET.setHint(R.string.lastName);
            }
        });

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE );
        }

        addText.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent event) {
                // TODO Auto-generated method stub
                if (view.getId() ==R.id.additionalText) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction()&MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
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
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                Toast.makeText(getActivity().getApplicationContext(),"Job submitted",Toast.LENGTH_SHORT).show();
            }
        });

//        startTimeTP.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Calendar calendar = Calendar.getInstance();
//                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
//                int currentMinute = calendar.get(Calendar.MINUTE);
//
//                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
//
////                        startTimeET.setText(String.format("%02d:%02d", hourOfDay, minutes));
//
//                        startMinute = minutes;
//                        startHour = hourOfDay;
//
//                        setTotalHours();
//                    }
//                }, currentHour, currentMinute, false);
//
//                timePickerDialog.show();
//
//            }
//        });

        startTimeTP.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                startMinute = minute;
                startHour = hourOfDay;

                setTotalHours();
            }
        });

        endTimeTP.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                endMinute = minute;
                endHour = hourOfDay;

                setTotalHours();
            }
        });

//        endTimeTP.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Calendar calendar = Calendar.getInstance();
//                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
//                int currentMinute = calendar.get(Calendar.MINUTE);
//
//                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
//
////                        endTimeET.setText(String.format("%02d:%02d", hourOfDay, minutes));
//
//                        endMinute = minutes;
//                        endHour = hourOfDay;
//
//                        setTotalHours();
//                    }
//                }, currentHour, currentMinute, false);
//
//                timePickerDialog.show();
//
//            }
//        });

    }

    private void setTotalHours() {

        Toast.makeText(getActivity().getApplicationContext(),"Total time CALLED",Toast.LENGTH_SHORT).show();

        if ( ( endMinute > 0 | endHour > 0 ) & ( startMinute > 0 | startHour > 0 ) ) {
            if (startMinute > endMinute) {
                totalHours.setText(((endHour - startHour) - 1) + "h " + (60 + (endMinute - startMinute)) + "m");
            } else {
                totalHours.setText((endHour - startHour) + "h " + (endMinute - startMinute) + "m");
            }
        }

    }

}
