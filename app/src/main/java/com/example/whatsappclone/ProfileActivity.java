package com.example.whatsappclone;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Profile");

        ImageView roundedImage = findViewById(R.id.roundedimage);

        Picasso.with(getApplicationContext())
                .load("https://firebasestorage.googleapis.com/v0/b/whatsapp-722cb.appspot.com/o/me.jpg?alt=media&token=ce0a2a36-dc7a-48b5-a65a-83023efbea39")
                .into(roundedImage);



    }
}