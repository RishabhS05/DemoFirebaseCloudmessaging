package com.example.demofirebasecloudmessaging.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.demofirebasecloudmessaging.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

import static com.example.demofirebasecloudmessaging.constants.UrlConstants.BASE_URL;
import static com.example.demofirebasecloudmessaging.constants.UrlConstants.HERO_NAME;
import static com.example.demofirebasecloudmessaging.constants.UrlConstants.URL_IMG;


public class AddProfile extends AppCompatActivity {
    public static final String TAG = "ADD";
    EditText name;
    Button uploadbtn;
    ImageView imgUpload;
    Uri img;
    private StorageReference storageRef;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_hero_details);
        name = findViewById(R.id.et_name);
        uploadbtn = findViewById(R.id.submit);
        imgUpload = findViewById(R.id.upload_image);
        storageRef = FirebaseStorage.getInstance().getReference(BASE_URL);
        db = FirebaseDatabase.getInstance().getReference(BASE_URL);
        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadData();
//finish();
            }

        });
        imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, 1);
            }
        });
    }

    public void uploadData() {
        final String id = UUID.randomUUID().toString();
        final StorageReference heroRef = storageRef.child(id);
        heroRef.putFile(img)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Toast.makeText(getApplicationContext(),
                                "Data Successfully Uploaded",
                                Toast.LENGTH_SHORT).show();
                        heroRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap<String, String> data = new HashMap<>();
                                data.put(HERO_NAME, ((EditText) findViewById(R.id.et_name)).getText().toString());
                                data.put(URL_IMG, String.valueOf(uri));

                                data.put("id", id);
                                db.child(id)
                                        .setValue(data).addOnSuccessListener(
                                        new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(),
                                                        "finally stored", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }
                                );
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                // ...
                Toast.makeText(getApplicationContext(), "Failed To Uploaded Image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: " + requestCode + "result " + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            img = data.getData();
            Glide.with(this).load(img)
                    .placeholder(R.drawable.defaultprofile)
                    .into(imgUpload);
            // String imgPath = data.getDataString();
            Log.d(TAG, "onActivityResult: " + data.getDataString());

        }
    }
}
