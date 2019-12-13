package com.sniryefet.Dogisitter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import static com.sniryefet.Dogisitter.LoginActivity.uInfo;

public class TripsViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_view);
        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new TripAdapter(this));
        //Log.d("Dogisitter","TripView:    "+ getUser().getName() );

    }
    public static User getUser(){
        return uInfo;
    }

}
