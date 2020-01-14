package com.sniryefet.Dogisitter;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;

import static com.sniryefet.Dogisitter.LoginActivity.uInfo;

public class TripsViewActivity extends AppCompatActivity {
    private GridView itemView;
    private ArrayList<Trip> FilteredTrips;
    private TripAdapter tAdapter;
    private static ArrayList<String> tripsId = new ArrayList<>();
    final static int[] imageIds = {R.drawable.puppy1,
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
                tAdapter = new TripAdapter(TripsViewActivity.this, imageIds, trips);
                itemView = (GridView) findViewById(R.id.gridview);
                itemView.setAdapter(tAdapter);

                // ** On Click item **
                itemView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(TripsViewActivity.this, PopActivity.class);
                        intent.putExtra("itemView", String.valueOf((GridView) itemView));
                        intent.putExtra("position", position+"");
                        intent.putExtra("key", tripsId.get(position));
                        //intent.putExtra("imageKey",position % 11);
                        finish();
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottom_navigation);
        BottomBarTab dummy = bottomBar.getTabWithId(R.id.dummy_id);
        dummy.setVisibility(View.GONE);
        bottomBar.setDefaultTab(R.id.dummy_id);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.ic_profile:
                        Toast.makeText(TripsViewActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                        editProfile();
                        break;
                    case R.id.ic_progress:
                        Toast.makeText(TripsViewActivity.this, "My trips", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(TripsViewActivity.this, ClientTrips.class);
                        finish();
                        startActivity(intent);
                        break;
                    case R.id.log_out:
                        Toast.makeText(TripsViewActivity.this, "Log out", Toast.LENGTH_SHORT).show();
                        logout(); // if not the first default active tab.
                        break;
                }
            }
        });

// ** search bar listener **
        SearchView searchView = findViewById(R.id.simpleSearchView);
        searchView.setQueryHint("Search trips by place..");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String text = newText;
                tAdapter.filter(text);
                return false;
            }

        });

    }

    public static User getUser() {
        return uInfo;
    }

    //onClick listener from xml file
    public void editProfile() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        finish();
        startActivity(intent);
    }

    private ArrayList<Trip> pullData(DataSnapshot dataSnapshot) {
        ArrayList<Trip> trips = new ArrayList<>();
        tripsId.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
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


}