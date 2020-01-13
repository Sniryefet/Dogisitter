package com.sniryefet.Dogisitter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


import android.os.Bundle;

public class ClientTrips extends AppCompatActivity {
    private DatabaseReference mDatabaseReference;
    private static ArrayList<String> tripsId;
    private GridView itemView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("barel", "1");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_trips);
        Log.d("barel", "2");
        tripsId = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /**
                 * This method is called once with the initial value and again
                 * whenever data at this location is updated.
                 */
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                fillIds(dataSnapshot.child("/ClientTrips/"+userID));
                final ArrayList<Trip> trips = pullData(dataSnapshot.child("/Trips"));
                final TripAdapter tAdapter = new TripAdapter(ClientTrips.this, imageIds, trips);
                itemView = (GridView) findViewById(R.id.gridview);
                itemView.setAdapter(tAdapter);

                // ** On Click item **
                itemView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(ClientTrips.this, PopActivity.class);
                        intent.putExtra("itemView", String.valueOf((GridView) itemView));
                        intent.putExtra("position", position);
                        intent.putExtra("key", tripsId.get(position));
                        finish();
                        startActivity(intent);
                    }
                });
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }

    private void fillIds(DataSnapshot dataSnapshot){
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                String id = ds.getKey();
                tripsId.add(id);
        }
    }
    private ArrayList<Trip> pullData(DataSnapshot dataSnapshot) {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ArrayList<Trip> trips = new ArrayList<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (tripsId.contains(ds.getKey())) {
                Trip trip = ds.getValue(Trip.class);
                trips.add(trip);
            }
        }


        return trips;

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent first_intent = new Intent(this, TripsViewActivity.class);

        finish();
        startActivity(first_intent);


    }

}
