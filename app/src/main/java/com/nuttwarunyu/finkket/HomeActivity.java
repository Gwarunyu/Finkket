package com.nuttwarunyu.finkket;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;

public class HomeActivity extends AppCompatActivity {

    ImageButton newBtn,loadPhotoBtn, cameraBtn ;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY = 2;

    private Uri mHighQualityImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        loadPhotoBtn = (ImageButton) findViewById(R.id.loadPhoto_btn);
        cameraBtn = (ImageButton) findViewById(R.id.camera_btn);
        newBtn = (ImageButton) findViewById(R.id.new_btn);

        newBtn.setBackgroundResource(R.drawable.home_new);
        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        cameraBtn.setBackgroundResource(R.drawable.home_camera);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHighQualityImageUri = generateTimeStampPhotoFileUri();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mHighQualityImageUri);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });

        loadPhotoBtn.setBackgroundResource(R.drawable.home_folder);
        loadPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPick_intent = new Intent(Intent.ACTION_PICK);
                photoPick_intent.setType("image/*");
                startActivityForResult(photoPick_intent, REQUEST_IMAGE_GALLERY);
            }
        });

    }

    private Uri generateTimeStampPhotoFileUri() {
        Uri photoFileUri = null;
        File outputDir = getPhotoDirectory();
        if (outputDir != null) {
            Time t = new Time();
            t.setToNow();
            File photoFile = new File(outputDir, System.currentTimeMillis() + "jpg");
            photoFileUri = Uri.fromFile(photoFile);
        }
        return photoFileUri;
    }

    private File getPhotoDirectory() {
        File outputDir = null;
        String externalStorageState = Environment.getExternalStorageState();
        if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            File photoDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            outputDir = new File(photoDir, getString(R.string.app_name));
            if (!outputDir.exists())
                if (!outputDir.mkdirs()) {
                    Toast.makeText(this, "Failed to create directory "
                                    + outputDir.getAbsolutePath(),
                            Toast.LENGTH_SHORT).show();
                    outputDir = null;
                }
        }
        return outputDir;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            Uri photoUri = data.getData();
            if (photoUri != null) {
                Intent sendUri = new Intent(getApplicationContext(), MainActivity.class);
                sendUri.setData(photoUri);
                Log.d("Send Uri From Home  ", photoUri + "   Go to MainActivity");
                startActivity(sendUri);
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Intent sendUri = new Intent(getApplicationContext(), MainActivity.class);
            sendUri.setData(mHighQualityImageUri);
            startActivity(sendUri);
        }
    }
}

