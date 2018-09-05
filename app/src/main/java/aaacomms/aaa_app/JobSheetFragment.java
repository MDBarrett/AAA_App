package aaacomms.aaa_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class JobSheetFragment extends Fragment {

    ImageButton navBtn;
    private DrawerLayout drawer;

    TextView jobNoTV;
    EditText additionalTextET, firstET, lastET, customerET;
    Spinner spinner;
    Button jobNoBtn;

    Boolean newJob = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_job_sheet, container, false);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
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

        ArrayAdapter<Integer> dropAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getJobNumbers());
        dropAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(dropAdapter);

        jobNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LayoutInflater li = LayoutInflater.from(getContext());
                View promptsView = li.inflate(R.layout.stat_change, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
                alertDialogBuilder.setView(promptsView);

                final EditText result = promptsView.findViewById(R.id.editTextResult);
                TextView prompt = promptsView.findViewById(R.id.prompt);
                prompt.setText("Job Number:");

                alertDialogBuilder.setCancelable(false).setPositiveButton("Create Job",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String num = result.getText().toString();
                                int jobNumber = Integer.parseInt(num);
                                if ( jobAccepted( num ) ) {
                                    storeJobNo(jobNumber);
                                    ArrayAdapter<Integer> dropAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getJobNumbers());
                                    dropAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                                    spinner.setAdapter(dropAdapter);
                                    setEditable(true);
                                    setJobNoTV( jobNumber );
                                    customerET.setText("");
                                    firstET.setText("");
                                    lastET.setText("");
                                    additionalTextET.setText("");
                                    spinner.setSelection(getIndex(Integer.valueOf(num)));
                                    setCurrentJob(Integer.valueOf(num));
                                    newJob = true;
                                }
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
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
                storeCustomer( String.valueOf( s ), Integer.parseInt( String.valueOf( jobNoTV.getText() ) ) );
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
                storeFirstName( String.valueOf( s ), Integer.parseInt( String.valueOf( jobNoTV.getText() ) ) );
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
                storeLastName( String.valueOf( s ), Integer.parseInt( String.valueOf( jobNoTV.getText() ) ) );
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
                storeAdditionalComments( String.valueOf( s ), Integer.parseInt( String.valueOf( jobNoTV.getText() ) ) );
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });   //additional comments LISTENER

        setJobNoTV( getCurrentJob() );
        spinner.setSelection( getIndex( getCurrentJob() ) );

    }

    public void storeJobNo(int jobNo) {
        SharedPreferences prefs;
        SharedPreferences jobPrefs = getContext().getSharedPreferences( getResources().getString(R.string.jobsPrefsString) , Context.MODE_PRIVATE);
        if ( !( jobExist( jobNo ) ) ) {  //if the job number does NOT exist
            if ( getNumJobs() != 0 ) {
                prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getNumJobs(), Context.MODE_PRIVATE);
            } else {
                prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + 0, Context.MODE_PRIVATE);
            }
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt( getResources().getString(R.string.jobNumString) , jobNo ).apply();

            int numberJobs = getNumJobs();
            SharedPreferences.Editor editor2 = jobPrefs.edit();
            editor2.putInt( getResources().getString(R.string.numJobsString) , numberJobs + 1 ).apply();
        }
    }

    private ArrayList<Integer> getJobNumbers() {
        ArrayList<Integer> jobNumbers = new ArrayList<>();
        int numJobs = getNumJobs();

        for ( int i = 0; i < numJobs; i++ ) {
            SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + i , Context.MODE_PRIVATE);
            int jobNo = prefs.getInt( getResources().getString(R.string.jobNumString) , 0);
            jobNumbers.add( jobNo );
        }
        return jobNumbers;
    }

    private int getNumJobs() {
        SharedPreferences prefs = getContext().getSharedPreferences( getResources().getString(R.string.jobsPrefsString) , Context.MODE_PRIVATE);
        return prefs.getInt( getResources().getString(R.string.numJobsString), 0 );
    }

    private boolean jobExist(int jobNo) {
        int numJobs = getNumJobs();

        for ( int i = 0; i < numJobs; i++ ) {
            SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + i , Context.MODE_PRIVATE);
            int jobNum = prefs.getInt( getResources().getString(R.string.jobNumString) , 0);
            if ( jobNo == jobNum ) {
                return true;
            }
        }
        return false;
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

    private void storeCustomer(String customer, int jobNo) {
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString( getResources().getString(R.string.customerString) , customer).apply();
    }

    private String getCustomer(int jobNo) {
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , Context.MODE_PRIVATE);
        return prefs.getString( getResources().getString(R.string.customerString) , null);
    }

    private void storeFirstName(String firstName, int jobNo) {
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString( getResources().getString(R.string.firstNameString) , firstName).apply();
    }

    private String getFirstName(int jobNo) {
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , Context.MODE_PRIVATE);
        return prefs.getString( getResources().getString(R.string.firstNameString) , null);
    }

    private void storeLastName(String lastName, int jobNo) {
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString( getResources().getString(R.string.lastNameString) , lastName).apply();
    }

    private String getLastName(int jobNo) {
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , Context.MODE_PRIVATE);
        return prefs.getString( getResources().getString(R.string.lastNameString) , null);
    }

    private void storeAdditionalComments(String additionalComments, int jobNo) {
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString( getResources().getString(R.string.additionalCommentsString) , additionalComments).apply();
    }

    private String getAdditionalComments(int jobNo) {
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , Context.MODE_PRIVATE);
        return prefs.getString( getResources().getString(R.string.additionalCommentsString) , null);
    }

    private void setEditable(boolean editable) {
        if ( editable ) {
            customerET.setFocusableInTouchMode(true);
            firstET.setFocusableInTouchMode(true);
            lastET.setFocusableInTouchMode(true);
            additionalTextET.setFocusableInTouchMode(true);
        } else {
            customerET.setFocusable(false);
            firstET.setFocusable(false);
            lastET.setFocusable(false);
            additionalTextET.setFocusable(false);
        }
    }

    private void loadFields(int jobNo) {
        customerET.setText( getCustomer( jobNo ) );
        firstET.setText( getFirstName( jobNo ) );
        lastET.setText( getLastName( jobNo ) );
        additionalTextET.setText( getAdditionalComments( jobNo ) );
    }

    private void setCurrentJob(int jobNo) {
        SharedPreferences jobPrefs = getContext().getSharedPreferences( getResources().getString(R.string.jobsPrefsString) , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = jobPrefs.edit();
        editor.putInt( getResources().getString(R.string.currentJobString) , jobNo ).apply();
    }

    private int getCurrentJob(){
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) , Context.MODE_PRIVATE);
        return prefs.getInt( getResources().getString(R.string.currentJobString) , 0);
    }

    private Boolean jobAccepted(String jobNo) {
        ArrayList<Integer> jobNumbers = getJobNumbers();

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

        int num = Integer.parseInt( jobNo );

        for ( int i = 0; i < getNumJobs(); i++ )
            if ( jobNumbers.get( i ) == num ) {
                Toast.makeText(getActivity(), "job number already exists", Toast.LENGTH_LONG).show();
                return false;
            }

        return true;
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

}
