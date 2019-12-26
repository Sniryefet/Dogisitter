package com.sniryefet.Dogisitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.sniryefet.Dogisitter.LoginActivity.uInfo;

public class TripsViewActivity extends AppCompatActivity {
    private GridView itemView;
    //private ArrayList<Trip> trips;

    final int[] imageIds = {R.drawable.puppy1,
            R.drawable.puppy2,
            R.drawable.puppy3,
            R.drawable.puppy4,
            R.drawable.puppy5,
            R.drawable.puppy6,
            R.drawable.puppy7,
            R.drawable.puppy8,
            R.drawable.puppy9,
            R.drawable.puppy10,
            R.drawable.puppy11,
    };

    private DatabaseReference refTrips = FirebaseDatabase.getInstance().getReference("Trips");
    private String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_view);
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /**
                 * This method is called once with the initial value and again
                 * whenever data at this location is updated.
                 */
                 ArrayList<Trip> trips = pullData(dataSnapshot.child("/Trips"));
                Log.d("getTripsTest: ", trips.toString()+ " | size: " + trips.size());
                 TripAdapter tAdapter = new TripAdapter(TripsViewActivity.this, imageIds, trips);
                itemView = (GridView) findViewById(R.id.gridview);
                itemView.setAdapter(tAdapter);

                // ************ On Click item **************
                itemView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(TripsViewActivity.this, "Click ti item: "+position, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        String user_name = getUser().getName();
        Log.d("Dogisitter","TripView:    "+ getUser().getName() );
    }
    public static User getUser(){
        return uInfo;
    }


    private ArrayList<Trip> pullData(DataSnapshot dataSnapshot){
        ArrayList<Trip> trips = new ArrayList<>();
        for(DataSnapshot ds: dataSnapshot.getChildren()){
            Trip trip = ds.getValue(Trip.class);
            Log.d("TESTTESTTEST: ", " name:"+ trip.getTripName());
            trips.add(trip);
        }
        return trips;
    }



//    private ArrayList<Trip> tripsData(DataSnapshot dataSnapshot) {
//        //User userInfo = new User();
//        ArrayList<Trip> array = new ArrayList<>();
//        for(DataSnapshot ds: dataSnapshot.getChildren()){
//                String name = ds.getValue(Trip.class).getTripName();
//                String date = ds.getValue(Trip.class).getDate();
//                String capacity = ds.getValue(Trip.class).getCapacity();
//                String description = ds.getValue(Trip.class).getDescription();
//                String duration = ds.getValue(Trip.class).getDuration();
//                String time = ds.getValue(Trip.class).getTime();
//                String place = ds.getValue(Trip.class).getMeetingPlace();
//                Trip trip = new Trip(name, place, date, time, duration, capacity, description, userID);
//                array.add(trip);
//        }
//        return array;
//    }

}