package com.example.socializechatzone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    CheckBox keepSignedIn;
    SharedPreferences preferences;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initializing views
        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        keepSignedIn = findViewById(R.id.keepSignedIn);

        //Initializing variables
        auth = FirebaseAuth.getInstance();
        preferences = getSharedPreferences("com.example.socializechatzone", MODE_PRIVATE);

        //If user wants to store credentials, load credentials from memory
        if(preferences.getBoolean("keepSignedIn", false)){
            email.setText(preferences.getString("username", ""));
            password.setText(preferences.getString("password",  ""));
            keepSignedIn.setChecked(preferences.getBoolean("keepSignedIn", false));
        }
    }

    //Function to validate entered credentials and then attempt log in
    public void validateInputAndLogInUser(View view){
        if(email.getText().length() == 0 && password.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter your email ID and password to proceed!", Toast.LENGTH_SHORT).show();  //Checking if both email and password fields are blank
            email.setBackgroundResource(R.drawable.invalid_edittext_value);
            password.setBackgroundResource(R.drawable.invalid_edittext_value);
        }
        else if(email.getText().length() == 0) {    //Checking if only email field is blank
            Toast.makeText(getApplicationContext(), "Please enter your email ID!", Toast.LENGTH_SHORT).show();
            email.setBackgroundResource(R.drawable.invalid_edittext_value);
        }
        else if(password.getText().length() == 0) { //Checking if only password field is blank
            Toast.makeText(getApplicationContext(), "Please enter your password!", Toast.LENGTH_SHORT).show();
            password.setBackgroundResource(R.drawable.invalid_edittext_value);
        }
        else {  //Fields are not empty
            auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())  //Attempting login with Firebase
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){    //If log in was successful
                        if(keepSignedIn.isChecked()){
                            preferences.edit().putString("username", email.getText().toString()).apply();
                            preferences.edit().putString("password", password.getText().toString()).apply();
                            preferences.edit().putBoolean("keepSignedIn", keepSignedIn.isChecked()).apply();
                        }
                        else {
                            preferences.edit().putString("username", "").apply();
                            preferences.edit().putString("password", "").apply();
                            preferences.edit().putBoolean("keepSignedIn", false).apply();
                        }

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else {  //If login was unsuccessful
                        Toast.makeText(getApplicationContext(), R.string.invalidCredentials, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void startSignUpActivity(View view){
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
    }
}