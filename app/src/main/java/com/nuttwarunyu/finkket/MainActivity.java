package com.nuttwarunyu.finkket;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawingView drawingView;
    private ImageButton currPaint, brushBtn, saveBtn, eraseBtn, newBtn, rotateBtn;
    private ImageButton opacityBtn;
    private float smallBrush, mediumBrush, largeBrush;
    private Bitmap bitmapCamera = null;

    Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        photoUri = getIntent().getData();
        setContentView(R.layout.activity_main);

        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        drawingView = (DrawingView) findViewById(R.id.drawing);

        Glide.with(getApplicationContext()).load(photoUri).centerCrop().into(drawingView);

        LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);

        currPaint = (ImageButton) paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        brushBtn = (ImageButton) findViewById(R.id.draw_btn);
        brushBtn.setOnClickListener(this);
        saveBtn = (ImageButton) findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);
        eraseBtn = (ImageButton) findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);
        newBtn = (ImageButton) findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);
        opacityBtn = (ImageButton) findViewById(R.id.opacity_btn);
        opacityBtn.setOnClickListener(this);

    }

    public void paintClicked(View view) {
        if (view != currPaint) {
            ImageButton imageButton = (ImageButton) view;
            String pattern = view.getTag().toString();

            drawingView.setPattern(pattern);
            drawingView.setBrushSize(drawingView.getLastBrushSize());

            imageButton.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint = (ImageButton) view;

        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.opacity_btn) {
            final Dialog seekDialog = new Dialog(this);
            seekDialog.setTitle("Opacity Level");
            seekDialog.setContentView(R.layout.opacity_chooser);

            final TextView seekTxt = (TextView) seekDialog.findViewById(R.id.opq_txt);
            final SeekBar seeOpq = (SeekBar) seekDialog.findViewById(R.id.opacity_seek);
            seeOpq.setMax(100);

            int currentLV = drawingView.getPaintAlpha();
            seekTxt.setText(currentLV + "%");
            seeOpq.setProgress(currentLV);

            seeOpq.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    seekTxt.setText(Integer.toString(progress) + "%");

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            Button opqBtn = (Button) seekDialog.findViewById(R.id.opq_ok);
            opqBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawingView.setPaintAlpha(seeOpq.getProgress());
                    seekDialog.dismiss();
                }
            });
            seekDialog.show();
        }

        if (v.getId() == R.id.new_btn) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New Drawing");
            builder.setMessage("Start new drawing (you will lose the current drawing)?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    drawingView.startNew();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        } else if (v.getId() == R.id.draw_btn) {
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Brush Size");
            brushDialog.setContentView(R.layout.brush_chooser);
            drawingView.setErase(false);

            ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawingView.setErase(false);
                    drawingView.setBrushSize(smallBrush);
                    drawingView.setLastBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawingView.setErase(false);
                    drawingView.setBrushSize(mediumBrush);
                    drawingView.setLastBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawingView.setErase(false);
                    drawingView.setBrushSize(largeBrush);
                    drawingView.setLastBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();
        } else if (v.getId() == R.id.erase_btn) {

            drawingView.setErase(true);
            drawingView.setBrushSize(mediumBrush);


        } else if (v.getId() == R.id.save_btn) {
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to device Gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    drawingView.setDrawingCacheEnabled(true);
                    String imgSaved = MediaStore.Images.Media.insertImage(
                            getContentResolver(),
                            drawingView.getDrawingCache(),
                            UUID.randomUUID().toString() + "png", "drawing");

                    if (imgSaved != null) {
                        Toast savedToast = Toast.makeText(getApplicationContext(),
                                "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                        savedToast.show();
                    } else {
                        Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                        unsavedToast.show();
                    }
                    drawingView.destroyDrawingCache();
                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }
    }

}
