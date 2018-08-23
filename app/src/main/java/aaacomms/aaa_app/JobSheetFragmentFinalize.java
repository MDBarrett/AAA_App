package aaacomms.aaa_app;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

public class JobSheetFragmentFinalize extends Fragment {

    ImageButton navBtn;
    Button sign;
    private DrawerLayout drawer;

    TimePicker startTimeTP, endTimeTP;

    int startMinute, startHour, endMinute, endHour;

    TextView totalHours;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_job_sheet_finalize, container, false);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        startTimeTP = getView().findViewById(R.id.startTimeTP);
        endTimeTP = getView().findViewById(R.id.endTimeTP);
        drawer = getActivity().findViewById(R.id.drawer_layout);
        navBtn = getView().findViewById(R.id.navButton);
        totalHours = getView().findViewById(R.id.totalHoursText);
        sign = getView().findViewById(R.id.signButton);

        startTimeTP.setIs24HourView( true );
        endTimeTP.setIs24HourView( true );

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

    }

    private void setTotalHours() {

        if ( ( endMinute > 0 | endHour > 0 ) & ( startMinute > 0 | startHour > 0 ) ) {
            if (startMinute > endMinute) {
                totalHours.setText(((endHour - startHour) - 1) + "h " + (60 + (endMinute - startMinute)) + "m");
            } else {
                totalHours.setText((endHour - startHour) + "h " + (endMinute - startMinute) + "m");
            }
        }

    }

}
