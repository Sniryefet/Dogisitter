package com.sniryefet.Dogisitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import org.w3c.dom.Text;


public class PopActivity extends AppCompatActivity {
    private TextView mTripName;
    private TextView mDate;
    private TextView mTime;
    private TextView mDuration;
    private TextView mCapacity;
    private TextView mLocation;
    private TextView mDescription;

    private String totalCapacity;
    private String mTripKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);


        String keyIdentifier;
        if (savedInstanceState != null)
            keyIdentifier= (String) savedInstanceState.getSerializable("itemView");
        else
            keyIdentifier = getIntent().getExtras().getString("itemView");

        Log.d( "PopActivitiy: ", keyIdentifier.toString());


        //LINKAGE
        mTripName=findViewById(R.id.popName);
        mDate=findViewById(R.id.popDate);
        mTime = findViewById(R.id.popTime);
        mDuration=findViewById(R.id.popDuration);
        mCapacity=findViewById(R.id.popCapacity);
        mLocation=findViewById(R.id.popLocation);
        mDescription=findViewById(R.id.popDescription);

        //LOAD PAGE DATA

        loadData();
    }


    public void joinTrip(View view){


    }

    private void loadData(){
        mTripKey=getIntent().getExtras().getString("key");

        DatabaseReference tripRef = FirebaseDatabase.getInstance().getReference("Trips/"+mTripKey);

            tripRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Trip t = dataSnapshot.getValue(Trip.class);
                    mTripName.setText(t.getTripName());
                    mDate.setText(t.getDate());
                    mTime.setText(t.getTime());
                    mLocation.setText(t.getMeetingPlace());
                    mDuration.setText(t.getDuration());
                    mDescription.setText(t.getDescription());
                    mCapacity.setText(t.getCurrCapacity()+"/"+t.getCapacity());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent first_intent = new Intent(this, TripsViewActivity.class);
        finish();
        startActivity(first_intent);
    }
}
