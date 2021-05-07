package com.example.socializechatzone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    //Declaring views
    EditText firstNameET, lastNameET, emailET, passwordET, confirmPasswordET, phoneET;
    private String firstName, lastName, email, password, confirmPassword, phone;

    //Declaring variables
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Setting up Firebase variables
        auth = FirebaseAuth.getInstance();

        //Setting up EditTexts
        firstNameET = findViewById(R.id.registerFirstName);
        lastNameET = findViewById(R.id.registerLastName);
        emailET = findViewById(R.id.registerEmail);
        passwordET = findViewById(R.id.registerPassword);
        confirmPasswordET = findViewById(R.id.registerConfirmPassword);
        phoneET = findViewById(R.id.registerPhoneNumber);

        //Adding onTextChangedListeners
        firstNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (firstNameET.getText().length() == 0)
                    firstNameET.setBackgroundResource(R.drawable.invalid_edittext_value);
                else
                    firstNameET.setBackgroundResource(R.drawable.edittext_design);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        lastNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (lastNameET.getText().length() == 0)
                    lastNameET.setBackgroundResource(R.drawable.invalid_edittext_value);
                else
                    lastNameET.setBackgroundResource(R.drawable.edittext_design);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!validateEmail(emailET.getText().toString()))
                    emailET.setBackgroundResource(R.drawable.invalid_edittext_value);
                else
                    emailET.setBackgroundResource(R.drawable.edittext_design);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (passwordET.getText().toString().length() < 8)
                    passwordET.setBackgroundResource(R.drawable.invalid_edittext_value);
                else
                    passwordET.setBackgroundResource(R.drawable.edittext_design);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        confirmPasswordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!matchPassword(confirmPasswordET.getText().toString(), passwordET.getText().toString()))
                    confirmPasswordET.setBackgroundResource(R.drawable.invalid_edittext_value);
                else
                    confirmPasswordET.setBackgroundResource(R.drawable.edittext_design);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        phoneET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!validatePhone(phoneET.getText().toString()))
                    phoneET.setBackgroundResource(R.drawable.invalid_edittext_value);
                else
                    phoneET.setBackgroundResource(R.drawable.edittext_design);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    //Function to start LoginActivity
    public void startLoginActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    //Function to register user
    public void RegisterUser(View view){

        //Getting strings from EditTexts
        firstName = firstNameET.getText().toString();
        lastName = lastNameET.getText().toString();
        email = emailET.getText().toString();
        password = passwordET.getText().toString();
        confirmPassword = confirmPasswordET.getText().toString();
        phone = phoneET.getText().toString();

        //Checking if entered information is valid
        if((firstName.length() > 0 && lastName.length() > 0 && validateEmail(email) && matchPassword(confirmPassword, password)) && validatePhone(phone)) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){        //If user sign up was successful
                        FirebaseUser user = auth.getCurrentUser();

                        String firstNameCaps = firstName.substring(0,1).toUpperCase() + firstName.substring(1).toLowerCase();
                        String lastNameCaps = lastName.substring(0,1).toUpperCase() + lastName.substring(1).toLowerCase();
                        String displayName = firstNameCaps + " " + lastNameCaps;

                        User newUser = new User(firstNameCaps, lastNameCaps, email, password, phone);
                        assert user != null;
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.usersTableName)).child(user.getUid());
                        reference.setValue(newUser);

                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(displayName).build();
                        user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(), String.valueOf(R.string.accountCreated), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    else {      //Sign up failed
                        Toast.makeText(getApplicationContext(), "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {      //If entered information is not valid
            Toast.makeText(getApplicationContext(), "Please verify your provided information!", Toast.LENGTH_SHORT).show();
        }
    }

    //Function to match Password and ConfirmPassword fields
    private boolean matchPassword(String confirmPassword, String password){
        if(confirmPassword.length() != 0 && password.length() != 0)
            return confirmPassword.equals(password);
        return false;
    }

    //Function to validate email structure
    private boolean validateEmail(String email){
        if (TextUtils.isEmpty(email))
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //Function to validate phone number structure
    private boolean validatePhone(String phone){
        if(phone.length() == 0)
            return true;
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }
}