package com.sniryefet.Dogisitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;

import com.bumptech.glide.Glide;
// For .gif files

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class LoginActivity extends AppCompatActivity {
    static User uInfo = new User();
    private FirebaseAuth mAuth;
    private EditText mEmailView;
    private EditText mPasswordView;
    private FirebaseUser userRef;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userID;
    private CheckBox mRemember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView = (ImageView) findViewById(R.id.imageViewGif);
        Glide.with(this).asGif().load(R.drawable.a_dog_walker_crop).into(imageView);
        mEmailView = findViewById(R.id.login_email);
        mPasswordView = findViewById(R.id.login_password);
        userRef = FirebaseAuth.getInstance().getCurrentUser();
        mRemember = findViewById(R.id.remember_button);

        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "false");
        String permission = preferences.getString("permission", "");
        Log.d("ayaya", "c: " + checkbox);
        Log.d("ayaya", "p: " + permission);
        if (checkbox.equals("true")) {
            Log.d("ayayayayay", permission + "000");

            if (permission.equals("Admin")) {
                uInfo.setPermission("Admin");
                Intent intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                Toast.makeText(this, "Logged In succesfully!", Toast.LENGTH_SHORT);
                finish();
                startActivity(intent);

            } else if (permission.equals("Client")) {
                uInfo.setPermission("Client");

                Intent intent = new Intent(LoginActivity.this, TripsViewActivity.class);
                Toast.makeText(this, "Logged In succesfully!", Toast.LENGTH_SHORT);
                finish();
                startActivity(intent);

            } else {
                Toast.makeText(this, "Please log in!", Toast.LENGTH_SHORT);

            }
        }


        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "true");
                    editor.putString("permission", uInfo.getPermission());
                    editor.apply();

                } else if (!compoundButton.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.putString("permission", uInfo.getPermission());
                    editor.apply();

                }
            }
        });
    }

    // Executed when Sign in button pressed
    //listener from the xml file
    public void signInExistingUser(View v) {
        attemptLogin();
    }

    // Executed when Register button pressed
    //listener from the xml file
    public void registerNewUser(View v) {
        Intent intent = new Intent(this, com.sniryefet.Dogisitter.RegisterActivity.class);
        finish();
        startActivity(intent);
    }

    private void attemptLogin() {

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        String password = mPasswordView.getText().toString();
        String email = mEmailView.getText().toString();

        Log.d("DogisitterApp", "showDataArray: Email: " + email);
        Log.d("DogisitterApp", "showDataArray: Password: " + password);
        //one of the field is blank
        if (password.length() < 1 || email.length() < 1) return;

        Toast.makeText(this, "Login In Progress...", Toast.LENGTH_SHORT).show();
        if (mRemember.isChecked()) {
            SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("remember", "true");
            editor.putString("permission", uInfo.getPermission());
            editor.apply();

        }
        if (!mRemember.isChecked()) {
            SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("remember", "false");
            editor.putString("permission", "NONE");
            editor.apply();
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String perm;
                        if (!task.isSuccessful()) {
                            Log.d("DogisitterApp", "Login Failed " + task.getException());
                            showErrorDialog("There was an error signing in");
                        } else {
                            FirebaseUser user = mAuth.getCurrentUser();
                            userID = user.getUid();
                            Log.d("DogisitterApp", "Login was successful");
                            //go to the profile page of the admin/client
                            //DatabaseReference mUsersRef = myRef.child("Users");
                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    /**
                                     * This method is called once with the initial value and again
                                     * whenever data at this location is updated.
                                     */
                                    Log.d("dEditProfileActivity", "showData is called again");
                                    ArrayList<String> userInfo = showData(dataSnapshot.child("/Users"));

                                    Intent intent;
                                    if (userInfo.get(2).equals("Admin")) {
                                        intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                                        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("permission", userInfo.get(2));
                                        editor.apply();

                                    } else {
                                        intent = new Intent(LoginActivity.this, TripsViewActivity.class);
                                        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("permission", userInfo.get(2));
                                        editor.apply();
                                    }
                                    finish();
                                    startActivity(intent);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });


    }

    private ArrayList<String> showData(DataSnapshot dataSnapshot) {
        //User userInfo = new User();
        ArrayList<String> array = new ArrayList<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (userID.equals(ds.getKey())) {
                uInfo.setEmail(ds.getValue(User.class).getEmail());
                uInfo.setName(ds.getValue(User.class).getName());
                uInfo.setPermission(ds.getValue(User.class).getPermission());
                Log.d("DogisitterApp", "showData: Email: " + uInfo.getEmail());
                Log.d("DogisitterApp", "showData: Name: " + uInfo.getName());
                Log.d("DogisitterApp", "showData: Permission: " + uInfo.getPermission());
                array.add(uInfo.getEmail());
                array.add(uInfo.getName());
                array.add(uInfo.getPermission());
            }
        }
        return array;

    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}