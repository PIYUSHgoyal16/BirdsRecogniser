package com.example.birds;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class Picture extends AppCompatActivity {
    private static final int CODE =123 ;
    private static final int PICK_IMAGE_REQUEST = 11;
    private static final String TAG = MainActivity.class.getSimpleName();
    ImageView imageView;
    Button button,btnChoose,btnUpload;
    String pathfile;
    private Uri imageUri;
    TextView tv;
    ProgressBar progressBar;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        button =(Button) findViewById(R.id.button);
        btnChoose = (Button) findViewById(R.id.btnchoose);
        btnUpload = (Button) findViewById(R.id.btnUpload);

        progressBar=(ProgressBar)findViewById(R.id.progressbar);

        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");
        storageReference = FirebaseStorage.getInstance().getReference("uploads");


        if(Build.VERSION.SDK_INT >= 23){
            requestPermissions(new String[]{Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("TAG = " + TAG);
                dispatchTakePictureIntent();
            }
        });

        imageView = (ImageView) findViewById(R.id.imageView);

        btnChoose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG,"Choose Button" );
                openfileChooser();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Log.d(TAG, "Uploadfile");
                uploadFile();
            }
        });

    }

    private void uploadFile() {
        RequestQueue queue = Volley.newRequestQueue(Picture.this);
        String url = "http://birds.iitmandi.ac.in/birds/Picture/result.php";

        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        progressBar.setVisibility(View.INVISIBLE);
                        Intent in = new Intent(Picture.this, finalResult.class);
                        in.putExtra("response", response);
                        startActivity(in);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.error.VolleyError error) {
                Toast.makeText(Picture.this, "Error", Toast.LENGTH_SHORT).show();
            }

        });
        smr.addFile("file", pathfile);
        RequestQueue mRequestQueue = Volley.newRequestQueue(Picture.this);
        mRequestQueue.add(smr);
        mRequestQueue.start();
    }

    private String getExtension(Uri imageUri) {
        Log.d(TAG, "getExtension");
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(imageUri));
    }

    private void openfileChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i , PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 1){
//                Bitmap bitmap = BitmapFactory.decodeFile(pathfile);
//                imageView.setImageBitmap(bitmap);
                rotateImage(BitmapFactory.decodeFile(pathfile));
            }

            if(requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null){
                imageUri = data.getData();
                imageView.setImageURI(imageUri);
//                try {
//                    rotateImage(MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                
            }
        }
    }

    private Bitmap setReducedImageSize() {
        int targetimageviewgetWidth = imageView.getWidth();
        int targetimageviewgetheight = imageView.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathfile, bmOptions);
        int cameraimagewidth = bmOptions.outWidth;
        int cameraimageheight = bmOptions.outHeight;

        int scalefactor = Math.min(cameraimagewidth/targetimageviewgetWidth, cameraimageheight/targetimageviewgetheight);
        bmOptions.inSampleSize = scalefactor;
        bmOptions.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(pathfile,bmOptions);
    }


    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createPhotoFile();
            if(photoFile != null) {
                pathfile = photoFile.getAbsolutePath();
                Uri photoUri = FileProvider.getUriForFile(Picture.this, "com.example.takepicture.fileprovider", photoFile);
                imageUri = photoUri;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }

    private File createPhotoFile()  {
        // Create an image file name
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(name, ".jpg", storageDir);
        } catch (IOException e) {
            Log.d("myLog", "Excep: " + e.toString() );
        }
        return image;
    }

    private void rotateImage(Bitmap bitmap){
        ExifInterface exifInterface = null;
        try{
            exifInterface = new ExifInterface(pathfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();
        switch (orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
                default:
        }
        Bitmap rotatebitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        imageView.setImageBitmap(rotatebitmap);


    }
}
