package aaacomms.aaa_app;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

public class JobSheetFragment extends Fragment {

    ImageButton navBtn;
    private DrawerLayout drawer;

    EditText additionalTextET, firstET, lastET, customerET, jobNoET;

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

        additionalTextET = getView().findViewById(R.id.additionalText);
        firstET = getView().findViewById(R.id.firstNameET);
        lastET = getView().findViewById(R.id.lastNameET);
        customerET = getView().findViewById(R.id.customerET);
        jobNoET = getView().findViewById(R.id.jobNumberET);

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

    }
}
