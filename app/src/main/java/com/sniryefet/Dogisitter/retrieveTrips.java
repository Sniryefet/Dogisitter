package com.sniryefet.Dogisitter;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class retrieveTrips {
    private ArrayList<Trip> trips = null;

    public retrieveTrips(final String path) { // "Trips"




    }

    public Trip getTrips(int i){
        return this.trips.get(i);
    }
    public ArrayList<Trip> getArrayList(){
        return this.trips;
    }
    public int getLength(){
        if(trips!=null && trips.size()>0) {
            return this.trips.size();
        }
        return 0;
    }


}
