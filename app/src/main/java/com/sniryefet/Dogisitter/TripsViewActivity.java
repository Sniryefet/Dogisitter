package com.sniryefet.Dogisitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private static ArrayList<String> tripsId = new ArrayList<>();

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
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /**
                 * This method is called once with the initial value and again
                 * whenever data at this location is updated.
                 */
                ArrayList<Trip> trips = pullData(dataSnapshot.child("/Trips"));
                TripAdapter tAdapter = new TripAdapter(TripsViewActivity.this, imageIds, trips);
                itemView = (GridView) findViewById(R.id.gridview);
                itemView.setAdapter(tAdapter);

                // ** On Click item **
                itemView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(TripsViewActivity.this,PopActivity.class);
                        intent.putExtra("itemView", String.valueOf((GridView) itemView));
                        intent.putExtra("position",position);
                        intent.putExtra("key",tripsId.get(position));
                        finish();
                        startActivity (intent);
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
        tripsId.clear();
        for(DataSnapshot ds: dataSnapshot.getChildren()){
            Trip trip = ds.getValue(Trip.class);
            trips.add(trip);
            tripsId.add(ds.getKey());
        }
        return trips;
    }

    @Override
    public void onBackPressed() {

        logout();
    }

    private void logout() {
        final Intent first_intent = new Intent(this, LoginActivity.class);

        new AlertDialog.Builder(TripsViewActivity.this)
                .setTitle("Alert")
                .setMessage("Confirm to log out")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("remember", "false");
                        editor.apply();
                        finish();
                        startActivity(first_intent);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    public void MyTrips(View view){
        super.onBackPressed();
        Intent intent = new Intent(TripsViewActivity.this,ClientTrips.class);
        finish();
        startActivity (intent);
    }

}