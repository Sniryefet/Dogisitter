package com.sniryefet.Dogisitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class PopActivity extends AppCompatActivity {
    private TextView mTripName;
    private TextView mDate;
    private TextView mTime;
    private TextView mDuration;
    private TextView mCapacity;
    private TextView mLocation;
    private TextView mDescription;
    private ImageView mTripImage;
    private String tripAdminKey;
    private Set<String> mMyTrips;
    private String mClickedTripKey;
    private final String TAG = "dPopActivity";
    private final String mUserID= FirebaseAuth.getInstance().getCurrentUser().getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);

        mMyTrips=new HashSet<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ClientTrips").child(mUserID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    mMyTrips.add(ds.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //LINKAGE
        mTripName=findViewById(R.id.popName);
        mDate=findViewById(R.id.popDate);
        mTime = findViewById(R.id.popTime);
        mDuration=findViewById(R.id.popDuration);
        mCapacity=findViewById(R.id.popCapacity);
        mLocation=findViewById(R.id.popLocation);
        mDescription=findViewById(R.id.popDescription);
        mTripImage=findViewById(R.id.imageView5);
        String position = getIntent().getExtras().getString("position");
        mTripImage.setImageResource(TripsViewActivity.imageIds[Integer.parseInt(position)%11]);
        //LOAD PAGE DATA
        mClickedTripKey=getIntent().getExtras().getString("key");
        loadData();
    }

    //onClick
    public void joinTrip(View view){

        if(mMyTrips.contains(mClickedTripKey)){
            Toast
                    .makeText(PopActivity.this, "Hey you are already joined this trip", Toast.LENGTH_SHORT)
                    .show();

            Log.d(TAG,"This trip id is"+mClickedTripKey);
            Iterator<String> iter = mMyTrips.iterator();
            while (iter.hasNext()) {
               Log.d(TAG,iter.next());
            }

            Log.d(TAG,"DEBUG : ENTER IF STATEMENT");
            return;
        }


        Log.d(TAG , "join Trip key activated");


        Log.d(TAG,"my id is : "+ mUserID);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Trips/"+mClickedTripKey);


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Trip t = dataSnapshot.getValue(Trip.class);
                String capacity = t.getCapacity();
                String currCapacity = t.getCurrCapacity();

                Log.d(TAG,"capacity: "+capacity);
                Log.d(TAG , "curCapacity: "+currCapacity);

                int intCapacity = Integer.parseInt(capacity);
                int intCurCapacity= Integer.parseInt(currCapacity);

                if(intCurCapacity<intCapacity){
                    int newCapacity = ++intCurCapacity;
                    t.setCurrCapacity(""+newCapacity);

                    //Update the database
                    ref.setValue(t);

                    //Show it to the user
                    mCapacity.setText(""+newCapacity+"/"+capacity);

                    //Update the user through toast message
                    Toast
                            .makeText(PopActivity.this, "Congratulation !!! You have joined the trip", Toast.LENGTH_SHORT)
                            .show();

                    //Update TripsParticipants Table
                    FirebaseDatabase.getInstance().getReference("TripsParticipants").child(mClickedTripKey).child(mUserID)
                            .setValue(mUserID);

                    //Update ClientTrips Table
                    FirebaseDatabase.getInstance().getReference("ClientTrips").child(mUserID).child(mClickedTripKey)
                            .setValue(mClickedTripKey);

                    //Update Set - NOT SURE IF REQUIRED
                    mMyTrips.add(mClickedTripKey);

                }else{

                    Toast
                            .makeText(PopActivity.this, "We are sorry but the trip is full of participants", Toast.LENGTH_SHORT)
                            .show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    //onClick
    public void leaveTrip(View view){
        if(!mMyTrips.contains(mClickedTripKey)){
            Toast
                    .makeText(PopActivity.this, "Hey you are not register for this trip", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Trips/"+mClickedTripKey);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Trip t = dataSnapshot.getValue(Trip.class);
                int capacity = Integer.parseInt(t.getCapacity());
                int curCap = Integer.parseInt(t.getCurrCapacity());
                curCap--;
                t.setCurrCapacity(curCap+"");

                //UPDATE DATABASE "Trips"
                ref.setValue(t);
                Toast
                        .makeText(PopActivity.this, "You have just left the trip", Toast.LENGTH_SHORT)
                        .show();
                //REMOVE THE TRIP FROM THE SET
                mMyTrips.remove(mClickedTripKey);

                //UPDATE SCREEN
                mCapacity.setText(""+curCap+"/"+capacity);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        FirebaseDatabase.getInstance()
                .getReference("ClientTrips/"+mUserID+"/"+mClickedTripKey)
                .setValue(null);

        FirebaseDatabase.getInstance()
                .getReference("TripsParticipants/"+mClickedTripKey+"/"+mUserID)
                .setValue(null);
        
    }
    public void showAdmin(View view){
        Intent intent = new Intent(PopActivity.this,AdminProfile.class);
        intent.putExtra("adminKey",tripAdminKey);
        finish();
        startActivity (intent);
    }
    private void loadData(){

        DatabaseReference tripRef = FirebaseDatabase.getInstance().getReference("Trips/"+mClickedTripKey);

            tripRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    tripAdminKey=t.getAdminID();
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
