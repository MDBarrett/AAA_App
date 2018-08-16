package aaacomms.aaa_app;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileOutputStream;

public class Esignature extends Activity {
    GestureOverlayView gestureView;
    LinearLayout signPad;
    String path;
    File file;
    Bitmap bitmap;
    public boolean gestureTouch=false;

    LinearLayout buttonBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.esign_main);

        Button donebutton = (Button) findViewById(R.id.DoneButton);
        donebutton.setText("Done");
        final Button clearButton = (Button) findViewById(R.id.ClearButton);
        clearButton.setText("Clear");
        Button cancel = findViewById(R.id.cancelButton);
        buttonBar = findViewById(R.id.buttonBarRL);

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
                try {
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

}
