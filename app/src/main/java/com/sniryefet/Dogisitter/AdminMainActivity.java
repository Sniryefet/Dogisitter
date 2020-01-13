package com.sniryefet.Dogisitter;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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
import android.widget.Button;
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
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;
import com.sniryefet.Dogisitter.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.sniryefet.Dogisitter.LoginActivity.uInfo;

public class AdminMainActivity extends AppCompatActivity {

    private String mDisplayName;
    private ListView mTripListView;
    private static boolean deletingNow = false;
    private DatabaseReference mDatabaseReference;
    private static ArrayList<String> tripsId = new ArrayList<>();
    private Button deleteTripBtn ;
    private GridView itemView;
    private Set<String> mTripParticipants;
    private BottomBar bottomBar;
    private int count = 2;

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

    public AdminMainActivity() {
    }

    //private DatabaseReference refTrips = FirebaseDatabase.getInstance().getReference("Trips");
    //private String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        bottomBar = (BottomBar) findViewById(R.id.bottom_navigation_admin);

        mTripParticipants=new HashSet<>();

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

                // * On Click item *
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
                            deleteTripBtn.setText("Remove trip");

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


        BottomBarTab dummy = bottomBar.getTabWithId(R.id.dummy_id_admin);
        dummy.setVisibility(View.GONE);
        Log.d("abababab", "id: "+bottomBar.getCurrentTabPosition());
        bottomBar.setDefaultTab(R.id.dummy_id_admin);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            Fragment selected = null;
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.dummy_id_admin:
                        if(count == 1){
                            count = 0;
                            startActivity(new Intent(getApplicationContext(),AdminMainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        }
                        break;
                    case R.id.ic_profile_admin:
                        count = 1;
                        Toast.makeText(AdminMainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                        editProfile();
                        break;
                    case R.id.ic_progress_admin:
                        count = 1;
                        Toast.makeText(AdminMainActivity.this, "Add Trip", Toast.LENGTH_SHORT).show();
                        addTrip();
                        break;
                    case R.id.remove_trip: // NEED TO BE CHANGED
                        count = 1;
                        Toast.makeText(AdminMainActivity.this, "Click to remove a trip", Toast.LENGTH_SHORT).show();
                        edit();
                        break;
                    case R.id.log_out_admin:
                        count = 1;
                        Toast.makeText(AdminMainActivity.this, "Log out", Toast.LENGTH_SHORT).show();
                        logout(); // if not the first default active tab.
                        break;
                }
            }
        });
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(int tabId) {
                switch (tabId){
                    case R.id.dummy_id_admin:
                        break;
                    case R.id.remove_trip:
                        BottomBarTab rt = bottomBar.findViewById(R.id.remove_trip);
                        for (int i = 0; i < tripsId.size(); i++) {
                            itemView.getChildAt(i).setBackgroundColor(Color.WHITE);
                        }
                        rt.setTitle("Remove trip");
                        bottomBar.setSelected(false);
                        bottomBar.selectTabWithId(R.id.dummy_id_admin);

                        break;
                    case R.id.log_out_admin:
                        logout();
                        break;
                }
            }
        });
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
    public void addTrip() {
        Intent intent = new Intent(this, AddTripActivity.class);
        finish();
        startActivity(intent);
    }


    //onClick listener from xml file
    public void editProfile() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        finish();
        startActivity(intent);
    }

    public void edit() {
        for (int i = 0; i < tripsId.size(); i++) {
            itemView.getChildAt(i).setBackgroundColor(Color.RED);
        }
        deletingNow = true;
        BottomBarTab rt = bottomBar.findViewById(R.id.remove_trip);
        rt.setTitle("Cancel");
        Context context = getApplicationContext();
        CharSequence text = "Touch to delete a trip!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();


    }

    private void deleteTrip(final String tripName) {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbNode1 = FirebaseDatabase.getInstance().getReference("Trips").child(tripName);
        DatabaseReference dbNode2 = FirebaseDatabase.getInstance().getReference("AdminTrips").child(userID).child(tripName);
        DatabaseReference dbNode3 = FirebaseDatabase.getInstance().getReference("TripsParticipants").child(tripName);

        dbNode1.setValue(null);
        dbNode2.setValue(null);
        dbNode3.setValue(null);

        final DatabaseReference dbNode4 = FirebaseDatabase.getInstance().getReference("ClientTrips");
        dbNode4.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    for(DataSnapshot neDs : ds.getChildren()){
                        if(neDs.getKey().equals(tripName)){
                            Log.d("dAdminMain", ds.getKey()+"/"+neDs.getKey());
                            dbNode4.child(ds.getKey()).child(neDs.getKey()).removeValue();
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    public void onBackPressed() {
        logout();
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