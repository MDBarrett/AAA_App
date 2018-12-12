package aaacomms.aaa_app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class LoginFragment extends Fragment {

    TextView login;

    private DrawerLayout drawer;

    ImageButton navBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if ( getView() != null && getActivity() != null ) {
            login = getView().findViewById(R.id.loginButton);
            drawer = getActivity().findViewById(R.id.drawer_layout);
            navBtn = getView().findViewById(R.id.navButton);
        }

        navBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.START);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AzureID azureID = new AzureID();
                azureID.storeLoginStatus(true, getContext() );
                UserSettingsFragment nextFrag= new UserSettingsFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, nextFrag,"findThisFragment")
                        .addToBackStack(null)
                        .commit();
                Toast.makeText(getActivity().getApplicationContext(),"Login successful",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
