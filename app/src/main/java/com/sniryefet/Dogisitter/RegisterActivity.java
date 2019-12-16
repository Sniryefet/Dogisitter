package com.sniryefet.Dogisitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    // Constants
    public static final String DISPLAY_NAME_KEY = "username";
    public static final String PERMISSION_KEY = "permission";
    public static final String CHAT_PREFS = "ChatPrefs";

    // UI references.
    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private CheckBox mAdmin;
    private CheckBox mClient;
    private String mPermission;
    private boolean mCheck;


    // Firebase instance variables
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmailView = findViewById(R.id.register_email);
        mPasswordView =  findViewById(R.id.register_password);
        mConfirmPasswordView =  findViewById(R.id.register_confirm_password);
        mUsernameView =  findViewById(R.id.register_username);
        mAdmin=findViewById(R.id.Admin);
        mClient=findViewById(R.id.Client);

        // Keyboard sign in action
        mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.register_form_finished || id == EditorInfo.IME_NULL) {
                    attemptRegistration();
                    return true;
                }
                return false;
            }
        });

        mAuth = FirebaseAuth.getInstance();



    }

    //OnClick listener on the CheckBoxes from the xml
    public void toggleCheckBox(View v){



    }
    public void adminCheckBox(View v){
        if (mAdmin.isChecked()) {
            mPermission = "Admin";
            mClient.setChecked(false);
            Log.d("checkbox", "permission: " + mPermission);
            Log.d("checkbox", "mAdmin onClick");

        }else {
            mPermission = "";
        }
    }
    public void clientCheckBox(View v){
        if (mClient.isChecked()){
            mPermission="Client";
            mAdmin.setChecked(false);
            Log.d("checkbox","permission: "+mPermission);
            Log.d("checkbox","mClient onClick");
        }else{
            mPermission="";
        }
    }

    // Executed when Sign Up button is pressed.
    //listener from the xml file
    public void signUp(View v) {
        attemptRegistration();
    }

    private void attemptRegistration() {

        // Reset errors displayed in the form.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();

        }else if(mPermission ==null || mPermission=="" ) {
            //Checks if the user chose a user permission
            showErrorDialog("Please choose user permission");
        }else {
            createFirebaseUser();
        }
    }

    //Check if the email that was given is valid
    private boolean isEmailValid(String email) {
        // You can add more checking logic here.
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        String confirmPassword = mConfirmPasswordView.getText().toString();

        return confirmPassword.equals(password) && password.length()>5;
    }

    private void createFirebaseUser(){
        final String name = mUsernameView.getText().toString();
        final String email = mEmailView.getText().toString().toLowerCase();
        final String permission=mPermission;
        String password =mPasswordView.getText().toString();

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                        Log.d("DogisitterApp","createUser onComplete"+task.isSuccessful());

                        if(!task.isSuccessful()){
                            Log.d("DogisitterApp","user creation failed");
                            showErrorDialog("Create : Registration failed");

                        }
                        else{
                            //Creates user Object and user node in the Database
                            User user = new User(name,email,permission);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful()){
                                        Log.d("DogisitterApp","Registration failed");
                                        Toast.makeText(RegisterActivity.this,
                                                "User : Registration failed",
                                                Toast.LENGTH_SHORT)
                                                .show();
                                    }else{
                                        Log.d("Dogisitter","Registered successfully");
                                        Toast.makeText(RegisterActivity.this,
                                                        "Registered successfully",
                                                        Toast.LENGTH_SHORT)
                                                        .show();
                                    }
                                }
                            });

                            //Go back to the LoginActivity
                            saveDisplayeName();
                            Intent intent= new Intent(RegisterActivity.this,LoginActivity.class);
                            finish();
                            startActivity(intent);
                        }

                    }
                });


    }


    // Storing data locally using SharedPreferences
    private void saveDisplayeName(){
        String displayName = mUsernameView.getText().toString();
        SharedPreferences pref= getSharedPreferences(CHAT_PREFS,0);
        pref.edit().putString(DISPLAY_NAME_KEY,displayName).apply();
        pref.edit().putString(PERMISSION_KEY,mPermission).apply();
    }


    private void showErrorDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}
