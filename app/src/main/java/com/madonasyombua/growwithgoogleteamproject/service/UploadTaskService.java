package com.madonasyombua.growwithgoogleteamproject.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by Ayo on 4/8/2018.
 */

public class UploadTaskService extends Service {
    private static final String TAG = "UploadTaskService";
    public static final String EXTRA_FILE_URI = "extra_file_uri";
    public static final String ACTION_UPLOAD = "action_upload";
    private StorageReference storageReference;

    @Override
    public void onCreate() {
        super.onCreate();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: " + intent);
        if (ACTION_UPLOAD.equals(intent.getAction())) {
            Uri fileUri = intent.getParcelableExtra(EXTRA_FILE_URI);
            uploadFromUri(fileUri);
        }
        return START_REDELIVER_INTENT;
    }

    private void uploadFromUri(Uri fileUri) {
        Log.d(TAG, "uploadFromUri: " + fileUri.toString());

        //creates a directory for the user images
        final StorageReference userRef = storageReference.child("feed_photos").child(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .child(fileUri.getLastPathSegment());

        Log.d(TAG, "uploadFromUri: " + userRef.getPath());
        userRef.putFile(fileUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    Toast.makeText(UploadTaskService.this, "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "onComplete: " + task.getResult().toString() );
                    Toast.makeText(UploadTaskService.this, "Failed", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
