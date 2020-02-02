package com.example.takepicture;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainActivity extends AppCompatActivity {

    private static final int CODE =123 ;
    private static final int PICK_IMAGE_REQUEST = 11;
    private static final String TAG = MainActivity.class.getSimpleName();
    ImageView imageView;
    Button button,btnChoose,btnUpload;
    String pathfile;
    private Uri imageUri;
    TextView tv;


    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button =(Button) findViewById(R.id.button);
        btnChoose = (Button) findViewById(R.id.btnchoose);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        tv = (TextView)findViewById(R.id.birdname);

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
                Log.d(TAG, "Uploadfile");
                uploadFile();
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inte = new Intent(MainActivity.this, Audio.class);
                startActivity(inte);
            }
        });
    }

    private void uploadFile() {
        if(imageUri != null) {
            StorageReference filereference = storageReference.child(System.currentTimeMillis()
                    + "." + getExtension(imageUri));

            filereference.putFile(imageUri).
                    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(MainActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();

                            Upload upload = new Upload(" ", taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                            String uploadId = databaseReference.getKey();
                            databaseReference.child(uploadId).setValue(upload);

                        }
                    }).
                    addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Cannot Upload", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
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
                Bitmap bitmap = BitmapFactory.decodeFile(pathfile);
                imageView.setImageBitmap(bitmap);
            }

            if(requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null){
                imageUri = data.getData();
                imageView.setImageURI(imageUri);

            }
        }
    }


    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createPhotoFile();
            if(photoFile != null) {
                pathfile = photoFile.getAbsolutePath();
                Uri photoUri = FileProvider.getUriForFile(MainActivity.this, "com.example.takepicture.fileprovider", photoFile);
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


}
