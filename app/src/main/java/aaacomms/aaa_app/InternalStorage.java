package aaacomms.aaa_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;

public class InternalStorage extends AppCompatActivity {

    public String jobPrefsString;
    public String jobNumString;
    public String numJobsString;
    public String customerString;

    public SharedPreferences jobPrefs;
    SharedPreferences prefs;
    int numberOfJobs;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        jobPrefsString = getResources().getString(R.string.jobsPrefsString);
        jobNumString = getResources().getString(R.string.jobNumString);
//        numJobsString = getResources().getString(R.string.numJobsString);
        customerString = getResources().getString(R.string.customerString);

        jobPrefs = getSharedPreferences( jobPrefsString , MODE_PRIVATE);
        numberOfJobs = jobPrefs.getInt( numJobsString , 0);
    }





}
