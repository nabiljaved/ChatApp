package com.nabeeltech.chatapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    MaterialEditText username, email, password;
    Button btn_register;
    FirebaseAuth auth;
    ImageView profile_image;
    DatabaseReference databaseReference;
    ProgressBar progressBar;
    private int GALLERY_INTENT = 2;
    StorageReference imagePath;
    StorageReference filePath;
    String downloadUri ="default";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_register = findViewById(R.id.btn_register);
        profile_image = findViewById(R.id.profile_image);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        auth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.GONE);
        imagePath = FirebaseStorage.getInstance().getReference();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txt_username = username.getText().toString();
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();



                if(TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password))
                {
                    Toast.makeText(RegisterActivity.this, "All Fields Are Required", Toast.LENGTH_SHORT).show();
                }else if (txt_password.length() < 6)
                    {
                        Toast.makeText(RegisterActivity.this, "Password Must be atleast 6 Characters", Toast.LENGTH_SHORT).show();
                    }else{
                    register(txt_username, txt_email, txt_password);
                }
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {
            progressBar.setVisibility(View.VISIBLE);
            Uri uri = data.getData();
            profile_image.setImageURI(uri);

            filePath = null;
            filePath = imagePath.child("photos").child(uri.getLastPathSegment());

            final UploadTask uploadTask = filePath.putFile(uri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();

                            }
                            // Continue with the task to get the download URL
                            return filePath.getDownloadUrl();


                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                downloadUri = task.getResult().toString();
                                Toast.makeText(RegisterActivity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Image Failed to upload", Toast.LENGTH_SHORT).show();
                }
            });

        }



    }

    public void register (final String username, String email, String password)
    {
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();

                            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                            HashMap<String, String> registerobject = new HashMap<>();
                            registerobject.put("id", userid);
                            registerobject.put("username", username);
                            registerobject.put("imageURL", downloadUri);
                            registerobject.put("status", "offline");
                            registerobject.put("search", username.toLowerCase());

                            databaseReference.setValue(registerobject).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        progressBar.setVisibility(View.GONE);
                                        Intent mainActivity = new Intent(RegisterActivity.this, MainActivity.class);
                                        Toast.makeText(RegisterActivity.this, "Registration is successfull", Toast.LENGTH_SHORT).show();
                                        mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(mainActivity);
                                        finish();
                                    }
                                }
                            });
                        } else
                            {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(RegisterActivity.this, "You Cannot Register with this email or Password", Toast.LENGTH_SHORT).show();
                            }
                    }
                });
    }



}
