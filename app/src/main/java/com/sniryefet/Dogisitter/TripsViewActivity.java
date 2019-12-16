package com.sniryefet.Dogisitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.sniryefet.Dogisitter.LoginActivity.uInfo;

public class TripsViewActivity extends AppCompatActivity {

    private DatabaseReference refTrips = FirebaseDatabase.getInstance().getReference("Trips");
    private String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_view);
        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new TripAdapter(this));
// ************ On Click item **************
//        gridView.setOnItemClickListener(new OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View v,
//                                    int position, long id) {
//                Toast.makeText(getApplicationContext(),
//                        ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
//            }
//        });
        String user_name = getUser().getName();
        Log.d("Dogisitter","TripView:    "+ getUser().getName() );
        refTrips.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<Trip> userInfo = tripsData(dataSnapshot.child("/Users"));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public static User getUser(){
        return uInfo;
    }


    private ArrayList<Trip> tripsData(DataSnapshot dataSnapshot) {
        //User userInfo = new User();
        ArrayList<Trip> array = new ArrayList<>();
        for(DataSnapshot ds: dataSnapshot.getChildren()){
                String name = ds.getValue(Trip.class).getTripName();
                String date = ds.getValue(Trip.class).getDate();
                String capacity = ds.getValue(Trip.class).getCapacity();
                String description = ds.getValue(Trip.class).getDescription();
                String duration = ds.getValue(Trip.class).getDuration();
                String time = ds.getValue(Trip.class).getTime();
                String place = ds.getValue(Trip.class).getMeetingPlace();
                Trip trip = new Trip(name, place, date, time, duration, capacity, description, userID);
                array.add(trip);
        }
        return array;
    }

}
