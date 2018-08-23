package aaacomms.aaa_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class JobSheetFragmentPhotos extends Fragment {

    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int EXTERNAL_REQUEST_CODE = 200;
    private String imageFilePath = "";

    public static final int REQUEST_IMAGE = 100;

    Button[] buttons = new Button[0];

    LinearLayout photosLL;

    ImageButton navBtn;
    private DrawerLayout drawer;

    Button takePhoto;

    SharedPreferences prefs;
    String imagePrefs = "imagePreferences";

    ImageView img;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_job_sheet_photos, container, false);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        takePhoto = getView().findViewById(R.id.takePhotoButton);
        photosLL = getView().findViewById(R.id.photosLL);
        drawer = getActivity().findViewById(R.id.drawer_layout);
        navBtn = getView().findViewById(R.id.navButton);
        navBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.START);
            }
        });

        img = getView().findViewById(R.id.imageView);

        prefs = this.getActivity().getSharedPreferences(imagePrefs, Context.MODE_PRIVATE);

//        photoIndex = prefs.getInt("photo_index", 0);

        if ( buttons.length == 0 ) {
            newButton();
            Toast.makeText(getActivity(), "button 0 created", Toast.LENGTH_SHORT).show();
        }

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

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                img.setImageURI(Uri.parse(imageFilePath));

//                int id = getView().getId();
//
//
//
//                Button currentBtn = getView().findViewById(R.id.id);

                newButton();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getActivity(), "You cancelled the operation", Toast.LENGTH_SHORT).show();
            }
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

    private void newButton() {
        Button newBtn = new Button( getActivity() );
        newBtn.setId( buttons.length );
        newBtn.setBackgroundResource(R.drawable.ic_add_photo);
        photosLL.addView( newBtn );
        newBtn = (Button) getView().findViewById( buttons.length );

        ViewGroup.LayoutParams params = newBtn.getLayoutParams();
        params.width = 400;
        params.height = 400;
        newBtn.setLayoutParams(params);

        Toast.makeText(getContext(),"Button clicked index = " + (buttons.length ), Toast.LENGTH_SHORT).show();
//        buttons[ buttons.length ] = newBtn;

        if ( buttons.length > 0 ) {
            final Button lastBtn = (Button) getView().findViewById( buttons.length );
            lastBtn.setBackgroundResource(R.drawable.ic_add_photo);
        }

        int id = newBtn.getId();

        newBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"Button clicked index = " + (buttons.length ), Toast.LENGTH_SHORT).show();
            }
        });

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("photo_index", buttons.length ).apply();
    }

}
