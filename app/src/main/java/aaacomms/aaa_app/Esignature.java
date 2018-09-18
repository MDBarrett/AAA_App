package aaacomms.aaa_app;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Esignature extends Activity {
    GestureOverlayView gestureView;
    LinearLayout signPad;
    String path;
    File file;
    Bitmap bitmap;
    public boolean gestureTouch=false;

    String imageFilePath = "";

    TextView signLine;
    TextView start, end, total, date;

    LinearLayout buttonBar;

    Boolean appTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs = getApplicationContext().getSharedPreferences( "ApplicationPreferences" , MODE_PRIVATE);
        appTheme = prefs.getBoolean("appTheme", false);

        if ( appTheme ) {
            setTheme( R.style.darkTheme );
        } else {
            setTheme( R.style.lightTheme );
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.esign_main);

        RelativeLayout activity = findViewById(R.id.activity_main);

        if ( appTheme ) {
            activity.setBackgroundResource( R.color.darkBackground );
        } else {
            activity.setBackgroundResource( R.color.lightBackground );
        }

        Button donebutton = (Button) findViewById(R.id.DoneButton);
        donebutton.setText("Done");
        final Button clearButton = (Button) findViewById(R.id.ClearButton);
        clearButton.setText("Clear");
        Button cancel = findViewById(R.id.cancelButton);
        buttonBar = findViewById(R.id.buttonBarRL);
        signLine = findViewById(R.id.signLine);
        start = findViewById(R.id.startTimeESTV);
        end = findViewById(R.id.endTimeESTV);
        total = findViewById(R.id.totalHoursESTV);
        date = findViewById(R.id.dataESTV);


        path= Environment.getExternalStorageDirectory()+"/signature.png";
        file = new File(path);
        file.delete();
        signPad = findViewById(R.id.signPad);
        gestureView = (GestureOverlayView) findViewById(R.id.signaturePad);
        signPad.setDrawingCacheEnabled(true);

        gestureView.setAlwaysDrawnWithCacheEnabled(true);
        gestureView.setHapticFeedbackEnabled(false);
        gestureView.cancelLongPress();
        gestureView.cancelClearAnimation();
        gestureView.addOnGestureListener(new GestureOverlayView.OnGestureListener() {

            @Override
            public void onGesture(GestureOverlayView arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onGestureCancelled(GestureOverlayView arg0,
                                           MotionEvent arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onGestureEnded(GestureOverlayView arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onGestureStarted(GestureOverlayView arg0,
                                         MotionEvent arg1) {
                // TODO Auto-generated method stub
                if (arg1.getAction()==MotionEvent.ACTION_MOVE){
                    gestureTouch=false;
                }
                else
                {
                    gestureTouch=true;
                }
            }});

        donebutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                greenColorAnimation(buttonBar);

                SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( getCurrentJob() ) , MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean( getResources().getString(R.string.signedString), true ).apply();

                try {
//                    Toast.makeText(getApplicationContext(), "SAVED", Toast.LENGTH_LONG).show();

                    bitmap = Bitmap.createBitmap(signPad.getDrawingCache());
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    // compress to specified format (PNG), quality - which is
                    // ignored for PNG, and out stream
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                    MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "signature" , "it is a signature");

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(gestureTouch==false)
                {
                    setResult(0);
                    finish();
                }
                else
                {
                    setResult(1);
                    finish();
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                redColorAnimation(buttonBar);
                gestureView.invalidate();
                gestureView.clear(true);
                gestureView.clearAnimation();
                gestureView.cancelClearAnimation();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                redColorAnimation(buttonBar);
                finish();
            }
        });

        setSignatureLine( getFirstName( getCurrentJob() ), getLastName( getCurrentJob() ), getCurrentJob() );

        start.setText( getStartTime( getCurrentJob() ) );
        end.setText( getEndTime( getCurrentJob() ) );
        total.setText( getTotalTime( getCurrentJob() ) );

        date.setText( getDate() );
    }

    private void redColorAnimation(View v) {
        int colorStart = getResources().getColor(R.color.darkBackground);
        int colorEnd = 0xFFFF0000;

        ValueAnimator colorAnim = ObjectAnimator.ofInt(v,"BackgroundColor", colorStart, colorEnd);

        colorAnim.setDuration(200);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatCount(1);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
    }

    private void greenColorAnimation(View v) {
        int colorStart = getResources().getColor(R.color.darkBackground);
        int colorEnd = 0xFF00FF00;

        ValueAnimator colorAnim = ObjectAnimator.ofInt(v,"BackgroundColor", colorStart, colorEnd);

        colorAnim.setDuration(200);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatCount(1);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
    }

    private void setSignatureLine(String firstName, String lastName, int jobNo) {
        String s;

        s = getString(R.string.signLine, "I, ", firstName, lastName , " confirm that job #", jobNo, " has been completed" );

        signLine.setText( s );

    }

    private int getCurrentJob(){
        SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.jobsPrefsString) , MODE_PRIVATE);
        return prefs.getInt( getResources().getString(R.string.currentJobString) , 0);
    }

    private int getIndex(int jobNo) {
        int index = 0;
        int numJobs = getNumJobs();

        for ( int i = 0; i < numJobs; i++ ) {
            SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + i , MODE_PRIVATE);
            int jobNum = prefs.getInt( getResources().getString(R.string.jobNumString) , 0);
            if ( jobNo == jobNum ) {
                index = i;
            }
        }
        return index;
    }

    private int getNumJobs() {
        SharedPreferences prefs = getSharedPreferences( getResources().getString(R.string.jobsPrefsString) , MODE_PRIVATE);
        return prefs.getInt( getResources().getString(R.string.numJobsString), 0 );
    }

    private String getFirstName(int jobNo) {
        SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , MODE_PRIVATE);
        return prefs.getString( getResources().getString(R.string.firstNameString) , null);
    }

    private String getLastName(int jobNo) {
        SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , MODE_PRIVATE);
        return prefs.getString( getResources().getString(R.string.lastNameString) , null);
    }

    private String getStartTime(int jobNo) {
        SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , MODE_PRIVATE);
        return prefs.getString( getResources().getString(R.string.startTimeString) , null);
    }

    private String getEndTime(int jobNo) {
        SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , MODE_PRIVATE);
        return prefs.getString( getResources().getString(R.string.endTimeString) , null);
    }

    private String getTotalTime(int jobNo) {
        SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , MODE_PRIVATE);
        return prefs.getString( getResources().getString(R.string.totalTimeString) , null);
    }

    private int getDay(int jobNo) {
        SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , MODE_PRIVATE);
        return prefs.getInt( getResources().getString(R.string.dayString) , 0);
    }

    private int getMonth(int jobNo) {
        SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , MODE_PRIVATE);
        return prefs.getInt( getResources().getString(R.string.monthString) , 0) + 1;
    }

    private int getYear(int jobNo) {
        SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , MODE_PRIVATE);
        return prefs.getInt( getResources().getString(R.string.yearString) , 0);
    }

    private String getDate() {
        return getDay( getCurrentJob() ) + "/" + getMonth( getCurrentJob() ) + "/" + getYear( getCurrentJob() );
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();

        return image;
    }


}