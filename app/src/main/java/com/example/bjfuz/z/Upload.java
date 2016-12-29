package com.example.bjfuz.z;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.lzp.floatingactionbuttonplus.FabTagLayout;
import com.lzp.floatingactionbuttonplus.FloatingActionButtonPlus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;

public class Upload extends AppCompatActivity {
    private FloatingActionButtonPlus mActionButtonPlus;
    Camera camera = new Camera();
    ImageView imageView;
    Uri imageUri;
    Uri uri_gallery;

    Uri cropedUri;

    private static final int CAMERA_PICTURE = 1;
    private static final int GALLERY_PICTURE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView = (ImageView)findViewById(R.id.imageView);
        mActionButtonPlus = (FloatingActionButtonPlus) findViewById(R.id.FabPlus);
        events();
        //FloatingActionButtonPlus fab1 = (FloatingActionButtonPlus)findViewById(R.id.FabPlus);
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButtonPlus fab1 = (FloatingActionButtonPlus)findViewById(R.id.FabPlus);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    private void events(){
        mActionButtonPlus.setOnItemClickListener(new FloatingActionButtonPlus.OnItemClickListener() {
            @Override
            public void onItemClick(FabTagLayout tagView, int position) {
                //Toast.makeText(Upload.this, "Click btn" + position, Toast.LENGTH_SHORT).show();
                if(position == 0) {
                    getImageFromCamera();
                }
                else if(position == 1){
                    getImageFromAlbum();
                }
            }
        });
    }

    private void getImageFromCamera(){
        Intent intent1 = new Intent();
        intent1.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent1.addCategory(Intent.CATEGORY_DEFAULT);

        File file = new File(camera.getImagePath());
        if (file.exists())
            file.delete();
        imageUri = Uri.fromFile(file);
        intent1.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent1, CAMERA_PICTURE);
    }

    private void getImageFromAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, GALLERY_PICTURE);
    }

    private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 0)
        {
            cropedUri = data.getData();
            Bitmap bitmap = decodeUriAsBitmap(cropedUri);
            imageView.setImageBitmap(bitmap);
        }
        else if (requestCode == CAMERA_PICTURE){
            Log.v("camera_uri",imageUri.toString());
            File file = new File(camera.getImagePath());
            Uri uri1 = Uri.fromFile(file);
            String absolutePath = camera.getPath(this,uri1);
            int degree = readPictureDegree(absolutePath);
            Bitmap bitmap1 = decodeUriAsBitmap(uri1);
            bitmap1 = rotateBitmap(bitmap1,degree);
            FileUtils fileUtils = new FileUtils();
            fileUtils.saveToSDCard(bitmap1, "pic", camera.getPicName());

            cropImageUri(imageUri, 600, 600, 0);
        }
        else if(requestCode == GALLERY_PICTURE){
           // String imageuri = data.getData();
            uri_gallery = Uri.parse("file://"+Camera.getPath(this,data.getData()));
            Log.v("gallery_uri",uri_gallery.toString());

            cropImageUri(uri_gallery,600,600,0);


        }

    }
    private Bitmap decodeUriAsBitmap(Uri uri){
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return degree;
        }
        return degree;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees == 0 || null == bitmap) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (null != bitmap) {
            bitmap.recycle();
        }
        return bmp;
    }
}
