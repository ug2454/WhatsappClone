package com.example.whatsappclone;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText email;
    EditText password;
    EditText nickName;
    Button signUpButton;
    Button loginButton;
    static FirebaseUser currentUser;
    FirebaseAuth mAuth;
    static String currentUserId;
    TextInputLayout emailTextInputLayout, passwordTextInputLayout, nickNameTextInputLayout;
    Date currentTime = Calendar.getInstance().getTime();
    FirebaseFirestore db = FirebaseFirestore.getInstance();


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
        nickName = findViewById(R.id.nickNameEditText);
        emailTextInputLayout = findViewById(R.id.input_layout_email);
        passwordTextInputLayout = findViewById(R.id.input_layout_password);
        nickNameTextInputLayout = findViewById(R.id.input_layout_name);
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        constraintLayout.setOnClickListener(this);


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
        submitForm();
        if (password.getText().length() > 5) {
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            currentUserId = user.getUid();
                            Log.d(TAG, "clickSignup:" + currentUserId);
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
        userDetails.put("nickname", nickName.getText().toString());
        userDetails.put("imageUrl", "");
        userDetails.put("lastMessage","");

        db.collection("users").document(currentUserId).set(userDetails)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e)
                );
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

    private void submitForm() {
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        Log.i(TAG, "submitForm: ALL VALID");
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

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

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