package com.sniryefet.Dogisitter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
    }


//    onClick from the xml
    // ** Maybe the View import is not the right one because there was multiply to import View **
    public void backButton(View v){
        // need to change it to the right page according to the user permission
        Intent intent = new Intent(EditProfileActivity.this,AdminMainActivity.class);
        finish();
        startActivity(intent);

    }
}
