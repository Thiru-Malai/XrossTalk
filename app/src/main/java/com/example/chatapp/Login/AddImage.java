package com.example.chatapp.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;

public class AddImage extends AppCompatActivity {

    private Button btnGetStarted, btnSkip;
    private String userid, imageUrl;
    private Uri resultUri = null;
    private EditText txtAbout;
    private ImageView imgUploadProfile;
    private final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase mdb;
    private DatabaseReference mreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);

        txtAbout = findViewById(R.id.txtEditAbout);
        imgUploadProfile = findViewById(R.id.imgProfileUpload);
        btnGetStarted = findViewById(R.id.btnGetStarted);
        btnSkip = findViewById(R.id.btnSkip);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mdb = FirebaseDatabase.getInstance();
        mreference = mdb.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userid = user.getUid();


        imgUploadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String About = txtAbout.getText().toString();
                if (About.length() > 270) {
                    Toast.makeText(AddImage.this, "About is too large", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage(About);
                }

            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddImage.this, com.example.chatapp.MainActivity.class));
            }
        });

    }

    private void selectImage() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(
//                Intent.createChooser(
//                        intent,
//                        "Select Image from here..."),
//                PICK_IMAGE_REQUEST);
        CropImage.activity().start(AddImage.this);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            try {

                // Setting image on image view using Bitmap
                resultUri = result.getUri();
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                resultUri);

                //Bitmap resized_image = Bitmap.createScaledBitmap(bitmap,180,180,true);
                imgUploadProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(String About) {

        if (resultUri != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Updating Profile");
            progressDialog.show();

            // Defining the child of storageReference

            StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + userid);

            // adding listeners on upload
            // or failure of image
            ref.putFile(resultUri)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Uri downloadUri = uri;
                                            imageUrl = downloadUri.toString();
                                            changeData(imageUrl, About);
                                        }
                                    });
                                    // Image uploaded successfully
                                    // Dismiss dialog

                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(AddImage.this,
                                                    "Profile Updated !!",
                                                    Toast.LENGTH_SHORT)
                                            .show();

                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(AddImage.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Updating Profile "
                                                    + (int) progress + "%");
                                }
                            });

        } else {
            changeData(About);
        }


    }

    private void changeData(String imageUrl, String modifiedAbout) {
        if (modifiedAbout.length() > 270) {
            Toast.makeText(AddImage.this, "About is too large", Toast.LENGTH_SHORT).show();
        } else {
            mreference.child("Users").child(userid).child("imageURL").setValue(imageUrl);
            mreference.child("Users").child(userid).child("about").setValue(modifiedAbout);

            startActivity(new Intent(AddImage.this, com.example.chatapp.MainActivity.class));
        }


    }

    public void changeData(String modifiedAbout) {
        if (modifiedAbout.length() > 270) {
            Toast.makeText(AddImage.this, "About should maximum 270 characters", Toast.LENGTH_SHORT).show();
        } else {
            mreference.child("Users").child(userid).child("about").setValue(modifiedAbout);

            Toast.makeText(AddImage.this, "Profile Updated !!", Toast.LENGTH_LONG).show();

            startActivity(new Intent(AddImage.this, com.example.chatapp.MainActivity.class));
        }


    }

}