package com.sniryefet.Dogisitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
    private static ArrayList<String> tripsId = new ArrayList<>();

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

        setDisplayName();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /**
                 * This method is called once with the initial value and again
                 * whenever data at this location is updated.
                 */
                final ArrayList<Trip> trips = pullData(dataSnapshot.child("/AdminTrips"));
                final TripAdapter tAdapter = new TripAdapter(AdminMainActivity.this, imageIds, trips);
                itemView = (GridView) findViewById(R.id.gridview);
                itemView.setAdapter(tAdapter);

                // **** On Click item ******
                itemView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        //Toast.makeText(AdminMainActivity.this, "Click ti item: " + position+"|||"+id, Toast.LENGTH_SHORT).show();
                        if (deletingNow) {
                            Toast.makeText(AdminMainActivity.this, "deleting item: " + position +
                                            " key:" + tripsId.get(position), Toast.LENGTH_SHORT).show();

                            trips.remove(position);
                            deleteTrip(tripsId.get(position));
                            tAdapter.notifyDataSetChanged();
                            deletingNow = false;
                            for (int i = 0; i < trips.size(); i++) {
                                itemView.getChildAt(i).setBackgroundColor(Color.WHITE);
                            }

                        }
                        
                    }
                });
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        Log.d("barelele", "3");


        String user_name = getUser().getName();
        Log.d("Dogisitter", "AdminTripView:    " + getUser().getName());
    }

    public static User getUser() {
        return uInfo;
    }


    private ArrayList<Trip> pullData(DataSnapshot dataSnapshot) {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ArrayList<Trip> trips = new ArrayList<>();
        tripsId.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            //Log.d("whoAmI?",ds.toString());
            //Log.d("whoAmI",userID);
            if (ds.getKey().equals(userID)) {
                for (DataSnapshot ds2 : ds.getChildren()) {
                    Trip trip = ds2.getValue(Trip.class);
                    trips.add(trip);
                    tripsId.add(ds2.getKey());
                }
            }
        }

        return trips;

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////


    private void setDisplayName() {
        SharedPreferences prefs = getSharedPreferences(RegisterActivity.CHAT_PREFS, MODE_PRIVATE);
        mDisplayName = prefs.getString(RegisterActivity.DISPLAY_NAME_KEY, null);

        if (mDisplayName == null) mDisplayName = "Annonymous";

    }


    //onClick listener from xml file
    public void addTrip(View v) {
        Intent intent = new Intent(this, AddTripActivity.class);
        finish();
        startActivity(intent);
    }


    //onClick listener from xml file
    public void editProfile(View v) {
        Intent intent = new Intent(this, EditProfileActivity.class);
        finish();
        startActivity(intent);
    }

    public void edit(View view) {
        for (int i = 0; i < tripsId.size(); i++) {
            itemView.getChildAt(i).setBackgroundColor(Color.RED);
        }
        deletingNow = true;
        Context context = getApplicationContext();
        CharSequence text = "Touch to delete a trip!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        String delTrip = "-LxkFQfEnyz0ErdbBufC";
        deleteTrip(delTrip);

    }

    private void deleteTrip(String tripName) {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbNode1 = FirebaseDatabase.getInstance().getReference("Trips").child(tripName);
        DatabaseReference dbNode2 = FirebaseDatabase.getInstance().getReference("AdminTrips").child(userID).child(tripName);
        dbNode1.setValue(null);
        dbNode2.setValue(null);
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        logout();
        /*
        Intent first_intent = new Intent(this, LoginActivity.class);
        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("remember", "false");
        editor.apply();
        finish();
        startActivity(first_intent);

         */
    }

    private void logout() {
        final Intent first_intent = new Intent(this, LoginActivity.class);

        new AlertDialog.Builder(AdminMainActivity.this)
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

}