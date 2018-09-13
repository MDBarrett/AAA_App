package aaacomms.aaa_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class JobSheetFragmentPhotos extends Fragment {

    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int EXTERNAL_REQUEST_CODE = 200;

    private static int RESULT_LOAD_IMAGE = 1;

    String imageFilePath = "";

    public static final int REQUEST_IMAGE = 100;

    TextView jobNoTV;

    LinearLayout photosLL;

    SharedPreferences prefs;
    String imagePrefs = "imagePreferences";

    ArrayList<Button> buttons = new ArrayList<>();

    Boolean appTheme;


    ImageView img;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_job_sheet_photos, container, false);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences( "ApplicationPreferences" , Context.MODE_PRIVATE);
        appTheme = prefs.getBoolean("appTheme", false);
        LinearLayout activity = getView().findViewById(R.id.activity_main);

        if ( appTheme ) {
            getActivity().setTheme( R.style.darkTheme );
            activity.setBackgroundResource( R.color.darkBackground );
        } else {
            getActivity().setTheme( R.style.lightTheme );
            activity.setBackgroundResource( R.color.lightBackground );
        }

        super.onActivityCreated(savedInstanceState);

        jobNoTV = getView().findViewById(R.id.jobNoTV);
        photosLL = getView().findViewById(R.id.photosLL);

        img = getView().findViewById(R.id.imageView);

        prefs = this.getActivity().getSharedPreferences(imagePrefs, Context.MODE_PRIVATE);

        setJobNoTV( getCurrentJob() );

        if ( getCurrentJob() != 0 ) {
            if ( getNumImages() > 0 ) {
                newImage(null, false);
                setStoredImages( getCurrentJob() );
//                Toast.makeText(getActivity(), "Images: " + getNumImages(), Toast.LENGTH_SHORT).show();
            } else {
                newImage(null, false);
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                img.setImageURI(Uri.parse(imageFilePath));
                newImage( imageFilePath, false );
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getActivity(), "You cancelled the operation", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            img.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            newImage( picturePath, false );

        }

    }

    public void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Uri photoUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() +".provider", photoFile);
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(pictureIntent, REQUEST_IMAGE);
        }
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();

        return image;
    }

    private void newImage(final String imageFilePath, boolean storedImages ) {
        final Button newBtn = new Button( getActivity() );               //Create the new button
        newBtn.setId( buttons.size() );

        if ( appTheme ) {
            newBtn.setBackgroundResource(R.drawable.ic_add_photo_dark_background);
        } else {
            newBtn.setBackgroundResource(R.drawable.ic_add_photo);
        }

        photosLL.addView( newBtn );                                //new button is added to the listView

        final ViewGroup.LayoutParams params = newBtn.getLayoutParams();
        photosLL.post(new Runnable(){
            public void run(){
//                Toast.makeText(getActivity(), "height: " + photosLL.getHeight(), Toast.LENGTH_SHORT).show();
                params.width = photosLL.getHeight();
                params.height = photosLL.getHeight();
                newBtn.setLayoutParams(params);
                if ( appTheme ) {
                    newBtn.setBackgroundResource(R.drawable.ic_add_photo);
                } else {
                    newBtn.setBackgroundResource(R.drawable.ic_add_photo_light);
                }
            }
        });

        setButtonListener( newBtn );

        if ( imageFilePath != null ) {

            final Button lastBtn = getView().findViewById(buttons.size() - 1);

            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);

            if (bitmap != null) {
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);
                BitmapDrawable ob = new BitmapDrawable(getResources(), resizedBitmap);

                if (buttons.size() > 1) {
                    ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) lastBtn.getLayoutParams();
                    params2.leftMargin = 20;
                }
                Toast.makeText(getActivity(), "IMAGE SET", Toast.LENGTH_SHORT).show();
                lastBtn.setBackground( ob );
                if ( appTheme ) {
                    newBtn.setBackgroundResource(R.drawable.ic_add_photo);
                } else {
                    newBtn.setBackgroundResource(R.drawable.ic_add_photo_light);
                }
                setButtonListener( lastBtn );
            }

            if ( !storedImages ) {
                storeImage(imageFilePath, getCurrentJob());

            } else {

            }

        }

        buttons.add( newBtn );

//            Toast.makeText(getActivity(), "Buttons: " + buttons.size() + " Images: " + getNumImages(), Toast.LENGTH_SHORT).show();
    }

    public void showDialog(Activity activity, String title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.DialogTheme);

        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton("Take Photo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                takePhoto();
            }
        });
        builder.setNeutralButton("Choose from Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                chooseGallery();
            }
        });
        builder.show();
    }

    private void takePhoto() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_CAMERA_PERMISSION_CODE);
        } else if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    EXTERNAL_REQUEST_CODE);
        } else {
            openCameraIntent();
        }
    }

    private void chooseGallery() {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
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

    private int getImageIndex(String filePath) {
        int index = 0;
        int numImages = getNumImages();

        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString), Context.MODE_PRIVATE);
        for ( int i = 0; i < numImages; i++ ) {
            String storedFilePath = prefs.getString( getResources().getString(R.string.imagePrefsString) + i , null);
            if ( filePath.equals( storedFilePath ) ) {
                index = i;
            }
        }
        return index;
    }

    private int getCurrentJob(){
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) , Context.MODE_PRIVATE);
        return prefs.getInt( getResources().getString(R.string.currentJobString) , 0);
    }

    private void storeImage( String filePath, int jobNo ){
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        int index = getNumImages();

        editor.putString( getResources().getString(R.string.imagePrefsString) + index , filePath ).apply();

        incrementNumImages();
    }

    private void setStoredImages( int jobNo ) {
        int numImages = getNumImages();
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( jobNo ) , Context.MODE_PRIVATE);

        for ( int i = 0; i < numImages; i++ ) {
            String filePath = prefs.getString( getResources().getString(R.string.imagePrefsString) + i , null);
            newImage( filePath , true );
        }

    }

    private int getNumImages(){
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( getCurrentJob() ) , Context.MODE_PRIVATE);
        return prefs.getInt( getResources().getString(R.string.numImagesString), 0);
    }

    private int getNumJobs() {
        SharedPreferences prefs = getContext().getSharedPreferences( getResources().getString(R.string.jobsPrefsString) , Context.MODE_PRIVATE);
        return prefs.getInt( getResources().getString(R.string.numJobsString), 0 );
    }

    private void incrementNumImages() {
        SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex( getCurrentJob() ), Context.MODE_PRIVATE);

        int numJobs = prefs.getInt( getResources().getString(R.string.numImagesString), 0 );

        SharedPreferences.Editor editor2 = prefs.edit();
        editor2.putInt( getResources().getString(R.string.numImagesString), numJobs + 1 ).apply();
    }

    private void setButtonListener(final Button button) {

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                int index = 0;

                switch (view.getId()) {
                    case 0:
                        index = 0;
                        break;
                    case 1:
                        index = 1;
                        break;
                    case 2:
                        index = 2;
                        break;
                    case 3:
                        index = 3;
                        break;
                    case 4:
                        index = 4;
                        break;
                    case 5:
                        index = 5;
                        break;
                    case 6:
                        index = 6;
                        break;
                    case 7:
                        index = 7;
                        break;
                    case 8:
                        index = 8;
                        break;
                    case 9:
                        index = 9;
                        break;
                    default:
                        break;
                }

                if ( index != ( buttons.size() -1 ) ) {
                    SharedPreferences prefs = getContext().getSharedPreferences(getResources().getString(R.string.jobsPrefsString) + getIndex(getCurrentJob()), Context.MODE_PRIVATE);
                    img.setImageURI(Uri.parse(prefs.getString(getResources().getString(R.string.imagePrefsString) + index, null)));
                } else {

                    showDialog(getActivity(), null, null);

                }
            }
        });

    }

    private void setJobNoTV(int jobNo) {
        if ( jobNo == 0 ) {
            jobNoTV.setText(R.string.noJobSelectedTV);
            jobNoTV.setTextSize( 28 );
        } else {
            jobNoTV.setText(String.valueOf(jobNo));
            jobNoTV.setTextSize( 28 );
        }
    }

}