package com.example.chatapp.Login;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.chatapp.Model.Users;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditProfile extends AppCompatActivity {
    private ImageView btnPrev, imgUpload;
    private EditText txtEditName, txtEditAbout;
    private TextView btnSave, btnSaveTag, btnCancel;
    private TextView btnTag, txtPopularity;
    private String tagColor;
    Boolean flag;

    private String profileImageUrl;
    Uri resultUri = null;
    private Uri filePath;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String userid, imageUrl, name, about;
    private FirebaseDatabase mdb;
    private DatabaseReference mreference;
    private final int PICK_IMAGE_REQUEST = 22;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ColorGetter colorGetter;
    ImageView image_icon;
    private List<Users> mUsers;
    TextView txtconnections;


    public String tempname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        RelativeLayout editprofilelayout = findViewById(R.id.editprofile);
        imgUpload = findViewById(R.id.imgUpload);
        image_icon = findViewById(R.id.image_edit);
        txtconnections = findViewById(R.id.txtConnectionsedit);
        btnPrev = findViewById(R.id.btnPrev);
        btnSave = findViewById(R.id.btnSave);
        txtPopularity = findViewById(R.id.pops);
        txtEditName = findViewById(R.id.txtEditName);
        txtEditAbout = findViewById(R.id.txtEditAbout);
        btnTag = findViewById(R.id.btnTag);
        colorGetter = new ColorGetter();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userid = user.getUid();
        name = txtEditName.getText().toString();
        about = txtEditAbout.getText().toString();
        tempname = name;
        mUsers = new ArrayList<>();
        mdb = FirebaseDatabase.getInstance();
        mreference = mdb.getReference();

        StorageReference ref
                = storageReference
                .child(
                        "images/"
                                + userid);


        setValues();


        image_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage();
            }

        });
        Query query1 = FirebaseDatabase.getInstance().getReference("Connections").child(userid).orderByChild("search");

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users req = snapshot.getValue(Users.class);

                    assert req != null;
                    assert userid != null;

                    if (req.getId().equals(userid)) {
                        mUsers.add(req);
                    }

                }
                int size = mUsers.size();
                if (size < 10) txtPopularity.setText("New");
                else if (size >= 10 && size < 50) txtPopularity.setText("Noticed");
                else if (size >= 10 && size < 50) txtPopularity.setText("Recognised");
                else if (size >= 50 && size < 100) txtPopularity.setText("Well Known");
                else if (size >= 100 && size < 500) txtPopularity.setText("Respected");
                else if (size >= 500 && size < 1000) txtPopularity.setText("Famous");
                else if (size >= 1000 && size < 10000) txtPopularity.setText("Popular");
                else if (size >= 10000 && size < 100000) txtPopularity.setText("Influencer");
                else if (size >= 100000 && size < 1000000) txtPopularity.setText("Celebrity");
                else if (size >= 1000000 && size < 10000000) txtPopularity.setText("Star");
                else if (size >= 10000000 && size < 100000000) txtPopularity.setText("Big Shot");
                else if (size >= 100000000 && size < 1000000000) txtPopularity.setText("Icon");
                else if (size >= 1000000000 && size < 1000000000) txtPopularity.setText("Poineer");
                Integer s = new Integer(size);
                txtconnections.setText(s.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater) EditProfile.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View tagSelectionView = layoutInflater.inflate(R.layout.activity_select_tag, null);

                btnCancel = tagSelectionView.findViewById(R.id.btnCancel);
                btnSaveTag = tagSelectionView.findViewById(R.id.btnSaveTag);

                PopupWindow popupWindow = new PopupWindow(tagSelectionView, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

                popupWindow.showAtLocation(editprofilelayout, Gravity.CENTER, 0, 0);

                setCheckedButton(tagSelectionView);

                btnSaveTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RadioGroup radioGroup = tagSelectionView.findViewById(R.id.tagselection);
                        int clicked_btn_id = radioGroup.getCheckedRadioButtonId();

                        RadioButton clicked_button = tagSelectionView.findViewById(clicked_btn_id);

                        String selected_tag = clicked_button.getText().toString();

                        btnTag.setText(selected_tag);

                        tagColor = colorGetter.getColor(selected_tag);
                        setTagColor();

                        popupWindow.dismiss();
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String modified_name;
                flag = true;
                String modified_about;

                modified_name = txtEditName.getText().toString().trim();

                modified_about = txtEditAbout.getText().toString().trim();
                if (modified_name.length() > 15) {
                    Toast.makeText(EditProfile.this, "Name is too large", Toast.LENGTH_SHORT).show();
                } else {
                    if (modified_about.length() < 270)
                        uploadImage(modified_name, modified_about);
                    else
                        Toast.makeText(EditProfile.this, "About should maximum 270 characters", Toast.LENGTH_SHORT).show();
                }


            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfile.this, Profile.class));
            }
        });
    }

    public void setTagColor() {
        btnTag.setTextColor(Color.parseColor(tagColor));
        GradientDrawable myGrad = (GradientDrawable) btnTag.getBackground();
        myGrad.setStroke(convertDpToPx(2), Color.parseColor(tagColor));
    }

    private int convertDpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private void setCheckedButton(View tagSelectionView) {
        RadioButton tag_radio_btn[] = new RadioButton[9];
        int[] btn_id = {R.id.tag1, R.id.tag2, R.id.tag3, R.id.tag4, R.id.tag5, R.id.tag6, R.id.tag7, R.id.tag8, R.id.tag9};

        for (int i = 0; i < btn_id.length; i++) {
            tag_radio_btn[i] = tagSelectionView.findViewById(btn_id[i]);
        }

        for (int i = 0; i < tag_radio_btn.length; i++) {
            String btn_value = tag_radio_btn[i].getText().toString().toLowerCase();
            String tag_value = btnTag.getText().toString().toLowerCase();

            if (btn_value.equals(tag_value)) {
                tag_radio_btn[i].setChecked(true);
                break;
            }
        }
    }

    private void setValues() {
        mreference.child("Users").child(userid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(EditProfile.this, "Error" + task.getException(), Toast.LENGTH_LONG).show();
                } else {
                    String about = task.getResult().child("about").getValue().toString();
                    String username = task.getResult().child("username").getValue().toString();
                    String tagname = task.getResult().child("tag").getValue().toString();
                    String connections = task.getResult().child("connections").getValue().toString();
                    String image = task.getResult().child("imageURL").getValue().toString();


                    txtEditName.setText(username);
                    btnTag.setText(tagname);
                    txtEditAbout.setText(about);
                    txtconnections.setText(connections);

                    Picasso.with(EditProfile.this).load(image).resize(160, 160).into(imgUpload);
                    tagColor = colorGetter.getColor(tagname);
                    setTagColor();
                }
            }
        });
    }

    private boolean isDataChanged(String modifiedUrl, String modifiedName, String modifiedAbout) {
        if (modifiedUrl.equals(imageUrl) && modifiedName.equals(name) && modifiedAbout.equals(about)) {
            return false;
        } else {
            return true;
        }
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
        CropImage.activity().start(EditProfile.this);

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

            // Get the Uri of data
            //filePath = data.getData();

            try {

                // Setting image on image view using Bitmap
                resultUri = result.getUri();
                //   imgUpload.setImageURI(resultUri);
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                resultUri);

//
//                Bitmap resized_image = Bitmap.createScaledBitmap(bitmap,100, 100,true);
                imgUpload.setImageBitmap(bitmap);
            } catch (Exception e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }


    private void uploadImage(String modifiedName, String modifiedAbout) {

        if (resultUri != null) {
            //     changeData(resultUri.toString(),modifiedName,modifiedAbout);
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
                                            changeData(imageUrl, modifiedName, modifiedAbout);
                                        }
                                    });
                                    // Image uploaded successfully
                                    // Dismiss dialog

                                    //           progressDialog.dismiss();
                                    Toast
                                            .makeText(EditProfile.this,
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
                                    .makeText(EditProfile.this,
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
            changeData(modifiedName, modifiedAbout);
        }


    }

    private void changeData(String imageUrl, String modifiedName, String modifiedAbout) {
        mreference.child("Users").child(userid).child("about").setValue(modifiedAbout);
        mreference.child("Users").child(userid).child("tag").setValue(btnTag.getText().toString());
        mreference.child("Users").child(userid).child("imageURL").setValue(imageUrl);
        flag = true;


        Query query1 = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search");

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users req = snapshot.getValue(Users.class);
                    if (!(req.getId().equals(firebaseUser.getUid()))) {


                        if (req.getUsername().toLowerCase().equals(modifiedName) && !(tempname.equals(modifiedName))) {
                            Toast.makeText(EditProfile.this, "User Already Exists", Toast.LENGTH_SHORT).show();
                            flag = false;
                            break;
                        }

                    }
                }


            }


            //
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (flag) {

            mreference.child("users").child(userid).child("username").setValue(modifiedName);
            Toast.makeText(EditProfile.this, "Profile Updated !!", Toast.LENGTH_SHORT).show();
            flag = false;
            startActivity(new Intent(EditProfile.this, Profile.class));
            finish();

        } else {
            // Toast.makeText(EditProfile.this, "User already exists!!", Toast.LENGTH_LONG).show();

        }

//        mreference.child("Users").child(userid).child("imageURL").setValue(imageUrl);
//        mreference.child("users").child(userid).child("username").setValue(modifiedName);
//        mreference.child("Users").child(userid).child("about").setValue(modifiedAbout);
//        mreference.child("Users").child(userid).child("tag").setValue(btnTag.getText().toString());
//
//        startActivity(new Intent(EditProfile.this,Profile.class));
//
    }

    //   }

    public void changeData(String modifiedName, String modifiedAbout) {
        flag = true;
        mreference.child("Users").child(userid).child("about").setValue(modifiedAbout);
        mreference.child("Users").child(userid).child("tag").setValue(btnTag.getText().toString());

        Query query2 = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search");

        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users req = snapshot.getValue(Users.class);
                    if (!(req.getId().equals(firebaseUser.getUid()))) {


                        if (req.getUsername().toLowerCase().equals(modifiedName) && !(tempname.equals(modifiedName))) {
                            Toast.makeText(EditProfile.this, "User Already Exists", Toast.LENGTH_SHORT).show();
                            flag = false;
                            break;
                        }

                    }
                }

//
//
//
//

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (flag) {
            mreference.child("Users").child(userid).child("username").setValue(modifiedName);


            Toast.makeText(EditProfile.this, "Profile Updated !!", Toast.LENGTH_SHORT).show();
            flag = true;
            startActivity(new Intent(EditProfile.this, Profile.class));
            finish();


        }

//

    }
}


