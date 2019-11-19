package com.sniryefet.Dogisitter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddTripActivity extends AppCompatActivity {

    private EditText mTripName;
    private EditText mMeetingPlace;
    private EditText mDate;
    private EditText mTime;
    private EditText mDuration;
    private EditText mCapacity;
    private EditText mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);


    }


    public void createTrip(View v){

    }
}
