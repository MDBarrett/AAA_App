package aaacomms.aaa_app;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;

public class JobReporter extends AppCompatActivity {

    Button submit, sign;

    EditText startTimeET, endTimeET, dateET;

    TextView totalHours;

    ImageView signed;

    int startMinute, startHour, endMinute, endHour;

    private static final int WRITE_EXTERNAL_STORAGE = 200;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){

        if (requestCode == WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Permission was not granted", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions,grantResults);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_reporter);

        getSupportActionBar().setTitle("Job Reporter");

        submit = findViewById(R.id.submitButton);
        startTimeET = findViewById(R.id.startTimeText);
        endTimeET = findViewById(R.id.endTimeText);
        dateET = findViewById(R.id.dateText);
        totalHours = findViewById(R.id.totalHoursText);
        signed = findViewById(R.id.signedImage);
        sign = findViewById(R.id.signButton);

        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
        } else {
            if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(this, "Body Sensors permissions is needed to show the heart rate.", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(JobReporter.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE );
        }

        signed.setVisibility(View.INVISIBLE);

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JobReporter.this, Esignature.class);
                startActivity(intent);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JobReporter.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(),"Job submitted",Toast.LENGTH_SHORT).show();
            }
        });

        startTimeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(JobReporter.this, new TimePickerDialog.OnTimeSetListener() {
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(JobReporter.this, new TimePickerDialog.OnTimeSetListener() {
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(JobReporter.this,
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
                totalHours.setText( "Total Time: " + ( ( endHour - startHour ) -1 ) + "h " + ( 60 + ( endMinute - startMinute ) ) + "m" );
            } else {
                totalHours.setText( "Total Time: " + ( endHour - startHour ) + "h " + ( endMinute - startMinute ) + "m" );
            }
        }

    }

    public void setButtonVis() {
        submit.setVisibility(View.INVISIBLE);
        sign.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(),"set vis called",Toast.LENGTH_SHORT).show();
    }

}