package aaacomms.aaa_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;
import java.util.Set;

public class DraftsListAdapter extends ArrayAdapter<JobsListDataModel> {

    private List<JobsListDataModel> list;

    private Context context;

    int resource;

    DraftsListAdapter(Context context, int resource, List<JobsListDataModel> list) {
        super(context, resource, list);
        this.context = context;
        this.resource = resource;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view;

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(resource, parent, false);
        } else
            view = convertView;

        TextView text = view.findViewById(R.id.textTV);
        ImageButton editBtn = view.findViewById(R.id.editBtn);
        ImageButton removeBtn = view.findViewById(R.id.removeBtn);

        JobsListDataModel customLADataModel = list.get(position);

        String s;
        s = getContext().getString(R.string.jobRow, customLADataModel.getJobNo(), ": ", customLADataModel.getCustomer() );
        text.setText( s );

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editElement(position);
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeElement(position);
            }
        });

        return view;
    }

    private void removeElement(final int position) {
        JobsListDataModel jobsDataModel = list.get( position );
        int jobNo = jobsDataModel.getJobNo();

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        builder.setTitle("Delete job #" + jobNo + " ?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                JobsListDataModel jobsDataModel = list.get( position );
                int jobNo = jobsDataModel.getJobNo();
                list.remove( position );

                SharedPreferences prefs = getContext().getSharedPreferences(getContext().getResources().getString(R.string.jobsPrefsString) + jobNo, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear().apply();

                removeJob( jobNo );  //removes the job number from the Set

                if ( getNumJobs() == 0 ){
                    setCurrentJob( 0 );
                } else {
                    JobsListDataModel jobsListDataModel = list.get( 0 );
                    int jobNum = jobsListDataModel.getJobNo();
                    setCurrentJob( jobNum );
                }

                notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        nbutton.setTextColor(Color.parseColor("#00897B"));
        pbutton.setTextColor(Color.parseColor("#00897B"));
    }

    private void removeJob(int jobNo) {
        Set<String> set = getJobsSet();
        set.remove( String.valueOf( jobNo ) );
        storeSet( set );
    }

    private Set<String> getJobsSet() {
        SharedPreferences prefs = getContext().getSharedPreferences( getContext().getResources().getString(R.string.jobsPrefsString) , Context.MODE_PRIVATE);
        return prefs.getStringSet( getContext().getResources().getString( R.string.jobsListString), null );
    }

    private void storeSet(Set<String> set) {
        SharedPreferences prefs = getContext().getSharedPreferences( getContext().getResources().getString(R.string.jobsPrefsString) , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet( getContext().getResources().getString(R.string.jobsListString), set ).apply();
    }

    private void editElement(final int position) {

        JobsListDataModel jobsDataModel = list.get( position );
        int jobNo = jobsDataModel.getJobNo();

        setCurrentJob( jobNo );

        ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new JobSheetFragment()).commit();
    }

    private int getNumJobs() {
        SharedPreferences prefs = getContext().getSharedPreferences( getContext().getResources().getString(R.string.jobsPrefsString), Context.MODE_PRIVATE );
        Set<String> set = prefs.getStringSet( getContext().getResources().getString(R.string.jobsListString), null );
        if ( set != null )
            return set.size();
        return 0;
    }

    private void setCurrentJob(int jobNo) {
        SharedPreferences jobPrefs = getContext().getSharedPreferences( getContext().getResources().getString(R.string.jobsPrefsString) , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = jobPrefs.edit();
        editor.putInt( getContext().getResources().getString(R.string.currentJobString) , jobNo ).apply();
    }
}