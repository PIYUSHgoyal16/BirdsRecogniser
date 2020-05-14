package com.example.birds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.birds.Audio.RequestPermissionCode;

public class Audio2 extends AppCompatActivity {

    Button btnaudio;
    TextView tv;
    MediaRecorder recorder;
    String mFileName;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio2);

        btnaudio = (Button) findViewById(R.id.btnaudio);
        tv = (TextView) findViewById(R.id.tv);
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"  + "AudioRecording.3gp";

        ActivityCompat.requestPermissions(Audio2.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);

        btnaudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    startRecording();
                    tv.setText("Recording Started ....");
                }

                else if(event.getAction() == MotionEvent.ACTION_UP) {
                    stopRecording();
                    tv.setText("Recording Stopped ....");
                }

                return false;
            }
        });


    }

    private void startRecording() {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(mFileName);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                recorder.prepare();
            } catch (IOException e) {
                Log.e("My TAG", "prepare() failed");
            }

            recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        uploadFile();
    }

    private void uploadFile() {
        StorageReference filepath = mStorage.child("Audio2").child("new_audio.3gp");
        Uri uri = Uri.fromFile(new File(mFileName));

        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Audio2.this, "Uploading Finished", Toast.LENGTH_SHORT).show();
                tv.setText("Uploaded");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Audio2.this, "Could not Upload", Toast.LENGTH_SHORT).show();
            }
        });

    }


}
