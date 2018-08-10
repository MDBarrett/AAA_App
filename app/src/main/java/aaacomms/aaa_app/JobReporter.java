package aaacomms.aaa_app;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;

public class JobReporter extends AppCompatActivity {

    Button submit;

    EditText startTimeET, endTimeET, dateET;

    TextView totalHours;

    int startMinute, startHour, endMinute, endHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_reporter);

        submit = findViewById(R.id.submitButton);
        startTimeET = findViewById(R.id.startTimeText);
        endTimeET = findViewById(R.id.endTimeText);
        dateET = findViewById(R.id.dateText);
        totalHours = findViewById(R.id.totalHoursText);

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

}