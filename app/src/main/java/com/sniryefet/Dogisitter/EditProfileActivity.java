package com.sniryefet.Dogisitter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG =  "EditProfileActivity";
    private ArrayList<String> mNames= new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ImageView mUserImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // look for the user image and animal's images in the database
        //meanwhile loading random images from the web
        initImages();

        mUserImage = findViewById(R.id.imageView);
        mUserImage.setImageResource(R.drawable.sugarmountain);


    }

    private void initImages(){
        //get the images from the right admin/client
        mImageUrls.add("https://thumbs.dreamstime.com/z/emilia-clarke-cannes-france-may-emilia-clarke-gala-screening-solo-star-wars-story-st-festival-de-cannes-%C2%A9-166810618.jpg");
        mNames.add("Emilia clarke");

        mImageUrls.add("https://thumbs.dreamstime.com/z/majestic-autumn-fall-landscape-red-deer-stag-cervus-elaphus-foreground-vibrant-forest-lake-background-stunning-epic-166661560.jpg");
        mNames.add("deer");

        mImageUrls.add("https://thumbs.dreamstime.com/z/schauzer-sam-166501063.jpg");
        mNames.add("dog");

        mImageUrls.add("https://thumbs.dreamstime.com/z/tourists-skiing-bukovel-bukovel-ivano-frankivsk-ukraine-february-ski-resort-bukovel-carpathian-mountains-ukraine-166427242.jpg");
        mNames.add("ski");

        mImageUrls.add("https://thumbs.dreamstime.com/z/gorilla-dangerous-look-dark-background-166410063.jpg");
        mNames.add("gorilla");

        mImageUrls.add("https://thumbs.dreamstime.com/z/green-snake-zoo-liberec-morelia-viridis-tree-python-best-photo-detail-166406538.jpg");
        mNames.add("snake");

        mImageUrls.add("https://thumbs.dreamstime.com/z/little-red-kitten-wearing-knitted-scarf-sits-snow-winter-little-red-kitten-wearing-knitted-scarf-sits-snow-166301940.jpg");
        mNames.add("cat");

        initRecyclerView();

    }
    private void initRecyclerView(){

        Log.d(TAG,"initRecyeclerView: ");

        LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        RecyclerView recyclerView= findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mNames,mImageUrls,this);
        recyclerView.setAdapter(adapter);

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
