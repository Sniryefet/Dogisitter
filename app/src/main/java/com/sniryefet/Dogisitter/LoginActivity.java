package com.sniryefet.Dogisitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText mEmailView;
    private EditText mPasswordView;
    private FirebaseUser userRef;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView = (ImageView) findViewById(R.id.imageViewGif);
        Glide.with(this).asGif().load(R.drawable.a_dog_walker_crop).into(imageView);
        mEmailView = findViewById(R.id.login_email);
        mPasswordView = findViewById(R.id.login_password);
        userRef=FirebaseAuth.getInstance().getCurrentUser();
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
        mAuth=FirebaseAuth.getInstance();

    }

    // Executed when Sign in button pressed
    //listener from the xml file
    public void signInExistingUser(View v)   {
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
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        String password = mPasswordView.getText().toString();
        String email = mEmailView.getText().toString();

        //one of the field is blank
        if(password.length()<1 || email.length()<1) return;

        Toast.makeText(this,"Login In Progress...",Toast.LENGTH_SHORT).show();

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //User is sign in
                    Log.d("DogisitterApp", "onAuthStateChange:sign_in: " + user.getUid());
                }
                else{
                    //user is sign-out
                    Log.d("DogisitterApp", "onAuthStateChange:sign_out");
                }
            }
        };

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        String perm;
                        if(!task.isSuccessful()){
                            Log.d("DogisitterApp","Login Failed "+task.getException());
                            showErrorDialog("There was an error signing in");
                        }else{
                            Log.d("DogisitterApp","Login was successful");
                            //go to the profile page of the admin/client
                        }

                        myRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                /**
                                 * This method is called once with the initial value and again
                                 * whenever data at this location is updated.
                                 */
                                ArrayList<String> userInfo = showData(dataSnapshot);
                                Log.d("DogisitterApp", "showDataArray: Email: "+ userInfo.get(0));
                                Log.d("DogisitterApp", "showDataArray: Name: "+ userInfo.get(1));
                                Log.d("DogisitterApp", "showDataArray: Permission: "+ userInfo.get(2));
                                Intent intent;
                                if(userInfo.get(2) == "Admin"){
                                    intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                                }else{
                                    intent = new Intent(LoginActivity.this, TripsViewActivity.class);
                                }
                                finish();
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });


    }

    private ArrayList<String> showData(DataSnapshot dataSnapshot) {
        //User userInfo = new User();
        ArrayList<String> array = new ArrayList<>();
        for(DataSnapshot ds: dataSnapshot.getChildren()){
            User uInfo = new User();
            uInfo.setEmail(ds.child(userID).getValue(User.class).getEmail());
            uInfo.setName(ds.child(userID).getValue(User.class).getName());
            uInfo.setPermission(ds.child(userID).getValue(User.class).getPermission());
            Log.d("DogisitterApp", "showData: Email: "+ uInfo.getEmail());
            Log.d("DogisitterApp", "showData: Name: "+ uInfo.getName());
            Log.d("DogisitterApp", "showData: Permission: "+ uInfo.getPermission());

            array.add(uInfo.getEmail());
            array.add(uInfo.getName());
            array.add(uInfo.getPermission());
        }
        return array;

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