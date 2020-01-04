package com.sniryefet.Dogisitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.sniryefet.Dogisitter.LoginActivity.uInfo;

public class AdminMainActivity extends AppCompatActivity {

    private String mDisplayName;
    private ListView mTripListView;
    private static boolean deletingNow = false;
    private DatabaseReference mDatabaseReference;

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

    //private DatabaseReference refTrips = FirebaseDatabase.getInstance().getReference("Trips");
    //private String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        Log.d("barelele","1");

        setDisplayName();
        mDatabaseReference= FirebaseDatabase.getInstance().getReference();
        Log.d("barelele","2");


        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /**
                 * This method is called once with the initial value and again
                 * whenever data at this location is updated.
                 */
                ArrayList<Trip> trips = pullData(dataSnapshot.child("/AdminTrips"));
                TripAdapter tAdapter = new TripAdapter(AdminMainActivity.this, imageIds, trips);
                itemView = (GridView) findViewById(R.id.gridview);
                itemView.setAdapter(tAdapter);

                // **** On Click item ******
                itemView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(AdminMainActivity.this, "Click ti item: "+position, Toast.LENGTH_SHORT).show();
                    }
                });
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        Log.d("barelele","3");


        String user_name = getUser().getName();
        Log.d("Dogisitter","AdminTripView:    "+ getUser().getName() );
    }
    public static User getUser(){
        return uInfo;
    }


    private ArrayList<Trip> pullData(DataSnapshot dataSnapshot) {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ArrayList<Trip> trips = new ArrayList<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            //Log.d("whoAmI?",ds.toString());
            //Log.d("whoAmI",userID);
            if (ds.getKey().equals(userID)) {
                for (DataSnapshot ds2 : ds.getChildren()) {
                    Trip trip = ds2.getValue(Trip.class);
                    trips.add(trip);
                }
            }
        }
        return trips;

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////


    private void setDisplayName(){
        SharedPreferences prefs= getSharedPreferences(RegisterActivity.CHAT_PREFS,MODE_PRIVATE);
        mDisplayName = prefs.getString(RegisterActivity.DISPLAY_NAME_KEY,null);

        if(mDisplayName==null) mDisplayName = "Annonymous";

    }


    //onClick listener from xml file
    public void addTrip(View v){
        Intent intent = new Intent(this, AddTripActivity.class);
        finish();
        startActivity(intent);
    }


    //onClick listener from xml file
    public void editProfile(View v){
        Intent intent = new Intent(this, EditProfileActivity.class);
        finish();
        startActivity(intent);
    }

    public void edit(View view) {
        deletingNow = true;
        Context context = getApplicationContext();
        CharSequence text = "Touch to delete a trip!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}