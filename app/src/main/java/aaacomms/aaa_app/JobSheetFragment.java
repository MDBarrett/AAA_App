package aaacomms.aaa_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class JobSheetFragment extends Fragment {

    ImageButton navBtn;
    private DrawerLayout drawer;

    TextView jobNoTV;
    EditText additionalTextET, firstET, lastET, customerET;
    Spinner spinner;
    Button jobNoBtn;

    TextView details, photos, finalize;

    Boolean newJob = false;
    Boolean appTheme;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_job_sheet, container, false);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        SharedPreferences prefs;
        if ( getActivity() != null ) {
            prefs = getActivity().getApplicationContext().getSharedPreferences("ApplicationPreferences", Context.MODE_PRIVATE);
            appTheme = prefs.getBoolean("appTheme", false);
            RelativeLayout activity;
            if ( getView() != null ) {
                activity = getView().findViewById(R.id.activity_main);

                if (appTheme) {
                    getActivity().setTheme(R.style.darkTheme);
                    activity.setBackgroundResource(R.color.darkBackground);
                } else {
                    getActivity().setTheme(R.style.lightTheme);
                    activity.setBackgroundResource(R.color.lightBackground);
                }
            }
        }

        super.onActivityCreated(savedInstanceState);

        drawer = getActivity().findViewById(R.id.drawer_layout);
        navBtn = getView().findViewById(R.id.navButton);
        navBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.START);
            }
        });

        jobNoTV = getView().findViewById(R.id.jobNoTV);
        additionalTextET = getView().findViewById(R.id.additionalText);
        firstET = getView().findViewById(R.id.firstNameET);
        lastET = getView().findViewById(R.id.lastNameET);
        customerET = getView().findViewById(R.id.customerET);
        spinner = getView().findViewById(R.id.jobNumberSP);
        jobNoBtn = getView().findViewById(R.id.newJobBtn);

        details = getView().findViewById(R.id.detailsBtn);
        photos = getView().findViewById(R.id.photosBtn);
        finalize = getView().findViewById(R.id.finalizeBtn);

        ArrayAdapter<Integer> dropAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getJobNumbers() );
        dropAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(dropAdapter);

        jobNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LayoutInflater li = LayoutInflater.from(getContext());
                View promptsView = li.inflate(R.layout.stat_change, new LinearLayout(getContext()), false);
                AlertDialog.Builder alertDialogBuilder;
                if (getContext() != null) {
                    alertDialogBuilder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
                    alertDialogBuilder.setView(promptsView);


                    final EditText result = promptsView.findViewById(R.id.editTextResult);
                    TextView prompt = promptsView.findViewById(R.id.prompt);
                    prompt.setText( getResources().getString(R.string.jobNumber) );

                    alertDialogBuilder.setCancelable(false).setPositiveButton("Create Job",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    createJob(result);
                                }
                            })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    nbutton.setTextColor(Color.parseColor("#00897B"));
                    pbutton.setTextColor(Color.parseColor("#00897B"));
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<Integer> jobNumbers = getJobNumbers();
                if ( !newJob ) {
                    setJobNoTV( jobNumbers.get(position) );
                } else {
                    newJob = false;
                }
                setCurrentJob( jobNumbers.get( position ) );
                loadFields( jobNumbers.get( position ) );
                setEditable( true );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });   //Spinner Listener

        setEditable( false );

        photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( getCurrentJob() > 0 ) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new JobSheetFragmentPhotos()).commit();
                } else {
                    Toast.makeText(getActivity(), "no job number selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        finalize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( getCurrentJob() > 0 ) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new JobSheetFragmentFinalize()).commit();
                } else {
                    Toast.makeText(getActivity(), "no job number selected", Toast.LENGTH_SHORT).show();
                }
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

        customerET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                storeCustomer( String.valueOf( s ), getCurrentJob() );
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });   //customer LISTENER

        firstET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                storeFirstName( String.valueOf( s ), getCurrentJob() );
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });   //first name LISTENER

        lastET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                storeLastName( String.valueOf( s ), getCurrentJob() );
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });   //last name LISTENER

        additionalTextET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                storeAdditionalComments( String.valueOf( s ), getCurrentJob() );
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });   //additional comments LISTENER

        setJobNoTV( getCurrentJob() );
        spinner.setSelection( getPosition( getCurrentJob() ) );
    }

    public int getIndex(int jobNo) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
            return prefs.getInt(getResources().getString(R.string.indexString), 0);
        }
        return 0;
    }

    public Set<String> getJobsSet() {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString), Context.MODE_PRIVATE);
            return prefs.getStringSet(getResources().getString(R.string.jobsListString), null);
        }
        return new HashSet<>();
    }

    public void storeJobNo(int jobNo) {
        if ( !( jobExist( jobNo ) ) ) {   //if the job number does NOT exist
            Set<String> set = getJobsSet();
            if ( set != null )
                set.add( String.valueOf( jobNo ) );
            else {
                set = new HashSet<>();
                set.add( String.valueOf( jobNo ) );
            }
            if ( getContext() != null ) {
                SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putStringSet(getResources().getString(R.string.jobsListString), set).apply();

                SharedPreferences jobPrefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
                SharedPreferences.Editor jobEditor = jobPrefs.edit();
                jobEditor.putInt(getResources().getString(R.string.indexString), set.size() - 1).apply();
            }
        }
    }

    private ArrayList<Integer> getJobNumbers() {
        ArrayList<Integer> jobNumbers = new ArrayList<>();
        String[] jobs = getOrderedJobs();
        for( String s : jobs )
            jobNumbers.add(Integer.valueOf(s));
        return jobNumbers;
    }

    public String[] getOrderedJobs() {
        LinkedList<String> jobsList = new LinkedList<>();
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString), Context.MODE_PRIVATE);
            Set<String> set = prefs.getStringSet(getResources().getString(R.string.jobsListString), null);


            if (set != null) {
                ArrayList<Integer> jobIndices = new ArrayList<>();

                for (String s : set) {
                    jobIndices.add(getIndex(Integer.valueOf(s)));
                }

                Collections.sort(jobIndices);

                for (int i = 0; i < jobIndices.size(); i++) {
                    for (String s : set) {
                        int index = getIndex(Integer.valueOf(s));
                        if (index == jobIndices.get(i))
                            jobsList.add(s);
                    }
                }
            }
        }

        for ( String s : jobsList   )
            Log.d("orderedJobs", s );

        return jobsList.toArray( new String[ jobsList.size() ] );
    }

    private boolean jobExist(int jobNo) {
        String[] set = getOrderedJobs();

        for ( String s : set )
            Log.d("jobExist", "set: " + s );

        for ( String s : set ) {
            Log.d("jobExist", "check: " + s + " - " + s );
            if ( jobNo == Integer.valueOf( s ) ) {
                return true;
            }
        }
        return false;
    }

    private void storeCustomer(String customer, int jobNo) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(getResources().getString(R.string.customerString), customer).apply();
        }
    }

    @Nullable
    private String getCustomer(int jobNo) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
            return prefs.getString(getResources().getString(R.string.customerString), null);
        }
        return null;
    }

    private void storeFirstName(String firstName, int jobNo) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(getResources().getString(R.string.firstNameString), firstName).apply();
        }
    }

    @Nullable
    private String getFirstName(int jobNo) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
            return prefs.getString(getResources().getString(R.string.firstNameString), null);
        }
        return null;
    }

    private void storeLastName(String lastName, int jobNo) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(getResources().getString(R.string.lastNameString), lastName).apply();
        }
    }

    @Nullable
    private String getLastName(int jobNo) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
            return prefs.getString(getResources().getString(R.string.lastNameString), null);
        }
        return null;
    }

    private void storeAdditionalComments(String additionalComments, int jobNo) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(getResources().getString(R.string.additionalCommentsString), additionalComments).apply();
        }
    }

    @Nullable
    private String getAdditionalComments(int jobNo) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
            return prefs.getString(getResources().getString(R.string.additionalCommentsString), null);
        }
        return null;
    }

    private void setEditable(boolean editable) {
        if ( editable ) {
            customerET.setEnabled(true);
            firstET.setEnabled(true);
            lastET.setEnabled(true);
            additionalTextET.setEnabled(true);
        } else {
            customerET.setEnabled(false);
            firstET.setEnabled(false);
            lastET.setEnabled(false);
            additionalTextET.setEnabled(false);
        }
    }

    private void loadFields(int jobNo) {
        customerET.setText( getCustomer( jobNo ) );
        firstET.setText( getFirstName( jobNo ) );
        lastET.setText( getLastName( jobNo ) );
        additionalTextET.setText( getAdditionalComments( jobNo ) );
    }

    private void setCurrentJob(int jobNo) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(getResources().getString(R.string.currentJobString), jobNo).apply();
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

    private Boolean jobAccepted(String jobNo) {

        if ( jobNo.length() != 5 ) {
            Toast.makeText(getActivity(), "job numbers require 5 digits", Toast.LENGTH_LONG).show();
            return false;
        }

        if ( jobNo.equals( "00000" ) ) {
            Toast.makeText(getActivity(), "job number is invalid", Toast.LENGTH_LONG).show();
            return false;
        }

        if ( jobNo.startsWith( "0" ) ) {
            Toast.makeText(getActivity(), "job numbers cannot start with 0", Toast.LENGTH_LONG).show();
            return false;
        }

        if ( jobExist( Integer.valueOf( jobNo ) ) ) {
            Toast.makeText(getActivity(), "job number already exists", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void setJobNoTV(int jobNo) {
        if ( jobNo == 0 ) {
            jobNoTV.setText(R.string.noJobSelectedTV);
            jobNoTV.setTextSize( 28 );
        } else {
            jobNoTV.setText(String.valueOf( jobNo ) );
            jobNoTV.setTextSize( 28 );
        }
    }

    private void setJobStatus(int jobNo) {
        SharedPreferences prefs;
        if ( getContext() != null ) {
            prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(getResources().getString(R.string.jobStatusString), "draft").apply();
        }
    }

    public int getPosition(int jobNo) {
        String[] jobs = getOrderedJobs();
        int count = 0;
        for ( String s : jobs ) {
            if ( String.valueOf( jobNo ).matches( s ) )
                return count;
            else
                count++;
        }
        return 0;
    }

    public void createJob(EditText result) {
        String num = result.getText().toString();
        int jobNumber = Integer.parseInt(num);
        if ( jobAccepted( num ) ) {
            storeJobNo(jobNumber);
            setJobStatus(jobNumber);
            ArrayAdapter<Integer> dropAdapter;
            if (getActivity() != null) {
                dropAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getJobNumbers());
                dropAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinner.setAdapter(dropAdapter);
            }
            setEditable(true);
            setJobNoTV( jobNumber );
            customerET.setText("");
            firstET.setText("");
            lastET.setText("");
            additionalTextET.setText("");
            spinner.setSelection( getIndex( jobNumber ) );
            setCurrentJob( Integer.valueOf( num ) );
            newJob = true;
        }
    }

}