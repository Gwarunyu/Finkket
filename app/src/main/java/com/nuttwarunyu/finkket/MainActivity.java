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
import android.graphics.Typeface;
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
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawingView drawingView;
    private ImageButton currPaint;
    private float smallBrush, mediumBrush, largeBrush;
    Bitmap bitmap;
    Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Fabric.with(this, new Crashlytics());
        photoUri = getIntent().getData();
        setContentView(R.layout.activity_main);

        Button brushBtn = (Button) findViewById(R.id.draw_btn);
        Button saveBtn = (Button) findViewById(R.id.save_btn);
        Button eraseBtn = (Button) findViewById(R.id.erase_btn);
        Button newBtn = (Button) findViewById(R.id.new_btn);
        Button opacityBtn = (Button) findViewById(R.id.opacity_btn);
        drawingView = (DrawingView) findViewById(R.id.drawing);
        LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);


        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        Glide.with(getApplicationContext()).load(photoUri).fitCenter().into(drawingView);

        currPaint = (ImageButton) paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        brushBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        eraseBtn.setOnClickListener(this);
        newBtn.setOnClickListener(this);
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

            final InterstitialAd interstitialAd = new InterstitialAd(getApplicationContext());
            interstitialAd.setAdUnitId("ca-app-pub-9597737983882274/9164522949");

            AdRequest.Builder builder = new AdRequest.Builder();
            AdRequest adRequest = builder.build();
            interstitialAd.loadAd(adRequest);
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    Log.d("Ad", "onAdLoaded");
                    super.onAdLoaded();
                    interstitialAd.show();
                }
            });

            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_alert_dialog);
            dialog.setCancelable(true);

            Typeface typeface = Typeface.createFromAsset(getAssets(), "boonjot.ttf");
            Button btn_savedevice = (Button) dialog.findViewById(R.id.save_device);
            btn_savedevice.setTypeface(typeface);
            btn_savedevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
            drawingView.setDrawingCacheEnabled(true);
            Bitmap aa = drawingView.getDrawingCache();
            SharePhoto sharePhoto = new SharePhoto.Builder().setBitmap(aa).build();
            SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder().addPhoto(sharePhoto).build();
            ShareButton shareButton = (ShareButton) dialog.findViewById(R.id.save_facebook);
            shareButton.setTypeface(typeface);
            shareButton.setShareContent(sharePhotoContent);

            dialog.show();
        }
    }

}
