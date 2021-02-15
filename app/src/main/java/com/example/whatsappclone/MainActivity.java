package com.example.whatsappclone;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    Button signUpButton;
    Button loginButton;
    ParseUser parseUser ;
     private static final String TAG="INFO";


     public void goToNext(){
         Intent in = new Intent(this,UserListActivity.class);
         finish();
         startActivity(in);
     }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email=findViewById(R.id.emailEditText);
        password=findViewById(R.id.passwordEditText);
        signUpButton=findViewById(R.id.signUpButton);
        loginButton=findViewById(R.id.loginButton);
        email.setAutofillHints(View.AUTOFILL_HINT_USERNAME);

        if(ParseUser.getCurrentUser()!=null){
            goToNext();
        }




    }

    public void clickSignup(View view) {



    }

    public void clickLogin(View view) {


    }
}