package com.nuttwarunyu.finkket;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class HomeActivity extends AppCompatActivity {

    private Button loadPhotoBtn, cameraBtn;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        loadPhotoBtn = (Button) findViewById(R.id.loadPhoto_btn);
        cameraBtn = (Button) findViewById(R.id.camera_btn);

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Log.d(" REQUEST_IMAGE_CAPTURE "," RESULT_OK ");
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            if (bitmap != null) {
                Log.d("BITMAP != null","STATUS OK");
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] bytesArray = byteArrayOutputStream.toByteArray();

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("imageBG", bytesArray);
                Log.d("Intent i put Extras","GO to MainActivity");
                startActivity(i);
            }
            Toast.makeText(getApplicationContext(),"Don't have Photo",Toast.LENGTH_SHORT).show();
        }
    }
}
