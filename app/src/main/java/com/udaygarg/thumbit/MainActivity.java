package com.udaygarg.thumbit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

//AIzaSyB1MOezmEwEeQKVd9ZTZ3p154t_27gKhdI

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText email;
    EditText password;
    EditText nickName;
    Button signUpButton;
    Button loginButton;
    static FirebaseUser currentUser;
    FirebaseAuth mAuth;
    ProgressBar progressBarSignUp;
    static String currentUserId;
    TextInputLayout emailTextInputLayout, passwordTextInputLayout, nickNameTextInputLayout;
    Date currentTime = Calendar.getInstance().getTime();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uniqueUserId = "";
    String uniqueId = "";

    private static final String TAG = MainActivity.class.getSimpleName();


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
        nickName = findViewById(R.id.nickNameEditText);
        emailTextInputLayout = findViewById(R.id.input_layout_email);
        passwordTextInputLayout = findViewById(R.id.input_layout_password);
        nickNameTextInputLayout = findViewById(R.id.input_layout_name);
        progressBarSignUp = findViewById(R.id.progressBarSignUp);
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        constraintLayout.setOnClickListener(this);
        uniqueId = UUID.randomUUID().toString();
        uniqueId = uniqueId.substring(0, 4);

        progressBarSignUp.setVisibility(View.GONE);


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
        progressBarSignUp.setVisibility(View.VISIBLE);
        if (validateEmail() && validatePassword() && validateName() && isValidEmail(email.getText().toString())) {
            if (password.getText().length() > 5) {
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                currentUserId = Objects.requireNonNull(user).getUid();
                                Log.d(TAG, "clickSignup:" + currentUserId);
                                addNewUser();
                                progressBarSignUp.setVisibility(View.GONE);
                                goToNext();

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        });
            }

        }

    }

    private void addNewUser() {

        uniqueUserId = nickName.getText().toString() + uniqueId;
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("uid", currentUserId);
        userDetails.put("email", email.getText().toString());
        userDetails.put("timestamp", currentTime);
        userDetails.put("nickname", nickName.getText().toString());
        userDetails.put("imageUrl", "");
        userDetails.put("uniqueID", uniqueUserId.toUpperCase());
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.udaygarg.thumbit", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("nickname", nickName.getText().toString()).apply();
        sharedPreferences.edit().putString("uniqueID", uniqueUserId.toUpperCase()).apply();


//        userDetails.put("lastMessage", "");

        db.collection("users").document(currentUserId).set(userDetails)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e)
                );

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                      db.collection("users").document(currentUserId).update("androidNotificationToken",token);

                    }
                });
    }

    public void clickLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.constraintLayout) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }


    private boolean validateName() {
        if (nickName.getText().toString().trim().isEmpty()) {
            nickNameTextInputLayout.setError("Please enter name");
            requestFocus(nickName);
            return false;
        } else {
            nickNameTextInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String emailString = email.getText().toString().trim();

        if (emailString.isEmpty() || !isValidEmail(emailString)) {
            emailTextInputLayout.setError("Please enter email");
            requestFocus(email);
            return false;
        } else {
            emailTextInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private boolean validatePassword() {
        if (password.getText().toString().trim().isEmpty()) {
            passwordTextInputLayout.setError("Please enter name");
            requestFocus(password);
            return false;
        } else {
            passwordTextInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private final View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @SuppressLint("NonConstantResourceId")
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.nickNameEditText:
                    validateName();
                    break;
                case R.id.emailEditText:
                    validateEmail();
                    break;
                case R.id.passwordEditText:
                    validatePassword();
                    break;
            }
        }
    }
}