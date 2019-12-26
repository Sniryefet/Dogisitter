package com.sniryefet.Dogisitter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminMainActivity extends AppCompatActivity {

    private String mDisplayName;
    private TextView mWelcome;
    private ListView mTripListView;
    private static boolean deletingNow = false;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        mWelcome= findViewById(R.id.welcome);

        setDisplayName();
        mDatabaseReference= FirebaseDatabase.getInstance().getReference();

    }


    private void setDisplayName(){
        SharedPreferences prefs= getSharedPreferences(RegisterActivity.CHAT_PREFS,MODE_PRIVATE);
        mDisplayName = prefs.getString(RegisterActivity.DISPLAY_NAME_KEY,null);

        if(mDisplayName==null) mDisplayName = "Annonymous";

    }


    //onClick listener from xml file
    public void addTrip(View v){
        Intent intent = new Intent(this, AddTripActivity.class);
        finish();
        startActivity(intent);
    }


    //onClick listener from xml file
    public void editProfile(View v){
        Intent intent = new Intent(this, EditProfileActivity.class);
        finish();
        startActivity(intent);
    }

    public void edit(View view) {
        deletingNow = true;
        Context context = getApplicationContext();
        CharSequence text = "Touch to delete a trip!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();


    }

}