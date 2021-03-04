package com.example.whatsappclone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {
    Uri uri1;
    Bitmap bitmap;
    String imageName;
    ImageView roundedImage;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "INFO";
    EditText nickname;
    TextView uniqueIdTextView;
    String uid = "";
    ProgressBar progressBar;
    Button cameraButton,saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageName = UUID.randomUUID().toString() + ".jpg";
        setContentView(R.layout.activity_profile);
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        setTitle("Profile");
        cameraButton=findViewById(R.id.cameraButton);
        saveButton=findViewById(R.id.saveButton);
        uniqueIdTextView = findViewById(R.id.uniqueIdTextView);
        nickname = findViewById(R.id.nicknameEditText);
        roundedImage = findViewById(R.id.roundedimage);
        progressBar = findViewById(R.id.progressBarActivity);
        progressBar.setVisibility(View.VISIBLE);
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.whatsappclone", Context.MODE_PRIVATE);
        uniqueIdTextView.setText("Unique ID: " + sharedPreferences.getString("uniqueID", ""));
        cameraButton.setEnabled(false);
        saveButton.setEnabled(false);
        db.collection("users").whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            nickname.setText(document.getString("nickname"));
                            if (document.get("imageUrl") != "") {
                                String url = (String) document.get("imageUrl");
                                Picasso.with(getApplicationContext())
                                        .load(url)

                                        .into(roundedImage);
                                progressBar.setVisibility(View.GONE);
                                cameraButton.setEnabled(true);
                                saveButton.setEnabled(true);
                            } else {
                                roundedImage.setImageResource(R.drawable.blankimage);
                                roundedImage.setBackgroundColor(0xFF172228);
                                progressBar.setVisibility(View.GONE);
                                cameraButton.setEnabled(true);
                                saveButton.setEnabled(true);
                            }

                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });


    }

    public static final int PICK_IMAGE = 1;

    public void selectImage(View view) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            progressBar.setVisibility(View.VISIBLE);
            roundedImage = findViewById(R.id.roundedimage);
            saveButton.setEnabled(false);
            cameraButton.setEnabled(false);
            SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.whatsappclone", Context.MODE_PRIVATE);
            sharedPreferences.edit().putString("imageName", imageName).apply();
            assert data != null;
            uri1 = data.getData();
            System.out.println(uri1.toString());
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri1);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] byteData = baos.toByteArray();

            FirebaseStorage.getInstance().getReference().child("images").child(imageName).putBytes(byteData)
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()).addOnSuccessListener(taskSnapshot -> {

                Toast.makeText(this, "Image uploaded", Toast.LENGTH_LONG).show();

                FirebaseStorage.getInstance().getReference().child("images").child(imageName).getDownloadUrl().addOnSuccessListener(uri -> {
                    String url = uri.toString();
                    System.out.println(url);
                    db.collection("users").document(uid).update("imageUrl", url);
                    Picasso.with(getApplicationContext())
                            .load(url)

                            .into(roundedImage);
                    cameraButton.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    saveButton.setEnabled(true);
                });
            });

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }

    public void save(View view) {
        db.collection("users").document(uid).update("nickname", nickname.getText().toString())
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Details Saved", Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Save Failed", Toast.LENGTH_LONG).show());


    }
}