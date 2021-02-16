package com.example.whatsappclone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    Button signUpButton;
    Button loginButton;
    static FirebaseUser currentUser;
    FirebaseAuth mAuth;
    static String currentUserId;
     Date currentTime = Calendar.getInstance().getTime();
     FirebaseFirestore db = FirebaseFirestore.getInstance();
     CollectionReference cities = db.collection("users");


    private static final String TAG = "INFO";


    public void goToNext() {
        Intent in = new Intent(this, UserListActivity.class);
        finish();
        startActivity(in);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        loginButton = findViewById(R.id.loginButton);


    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            goToNext();
        }

    }

    public void clickSignup(View view) {
        if (password.getText().length() > 5) {
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            currentUserId = user.getUid();
                            Log.d(TAG, "clickSignup:"+currentUserId);
                            addNewUser();
                            goToNext();

//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                    });
        } else {
            Toast.makeText(this, "Please choose password greater than 5 length", Toast.LENGTH_SHORT).show();
        }

    }

    private void addNewUser() {
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("uid", currentUserId);
        userDetails.put("email", email.getText().toString());
        userDetails.put("timestamp", currentTime);

        db.collection("users").document(currentUserId).set(userDetails)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e)
                );
    }

    public void clickLogin(View view) {
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        currentUserId = user.getUid();

                        goToNext();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(getApplicationContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
//
                    }

                });

    }
}