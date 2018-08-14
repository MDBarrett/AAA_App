package aaacomms.aaa_app;

import android.Manifest;
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

    EditText startTimeET, endTimeET, dateET;

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        submit = getView().findViewById(R.id.submitButton);
        startTimeET = getView().findViewById(R.id.startTimeText);
        endTimeET = getView().findViewById(R.id.endTimeText);
        dateET = getView().findViewById(R.id.dateText);
        totalHours = getView().findViewById(R.id.totalHoursText);
        sign = getView().findViewById(R.id.signButton);

        drawer = getActivity().findViewById(R.id.drawer_layout);
        navBtn = getView().findViewById(R.id.navButton);

        navBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.START);
            }
        });

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE );
        }

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

        startTimeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {

                        startTimeET.setText(String.format("%02d:%02d", hourOfDay, minutes));

                        startMinute = minutes;
                        startHour = hourOfDay;

                        setTotalHours();
                    }
                }, currentHour, currentMinute, false);

                timePickerDialog.show();

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

                        endTimeET.setText(String.format("%02d:%02d", hourOfDay, minutes));

                        endMinute = minutes;
                        endHour = hourOfDay;

                        setTotalHours();
                    }
                }, currentHour, currentMinute, false);

                timePickerDialog.show();

            }
        });

        dateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                                dateET.setText(day + "/" + (month + 1) + "/" + year);

                            }
                        }, year, month, dayOfMonth);

                datePickerDialog.show();

            }
        });

    }

    private void setTotalHours() {

        if ( ( endMinute > 0 | endHour > 0 ) & ( startMinute > 0 | startHour > 0 ) )  {
            if ( startMinute > endMinute ) {
                totalHours.setText( ( ( endHour - startHour ) -1 ) + "h " + ( 60 + ( endMinute - startMinute ) ) + "m" );
            } else {
                totalHours.setText( ( endHour - startHour ) + "h " + ( endMinute - startMinute ) + "m" );
            }
        }

    }

    public void setButtonVis() {
        submit.setVisibility(View.INVISIBLE);
        sign.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity().getApplicationContext(),"set vis called",Toast.LENGTH_SHORT).show();
    }

}
