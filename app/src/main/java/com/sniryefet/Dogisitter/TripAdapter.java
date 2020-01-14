package com.sniryefet.Dogisitter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class TripAdapter extends BaseAdapter {
    private Context mContext;//activity
    private LayoutInflater inflater = null;
    private ArrayList<Trip> retTrips;
    private ArrayList<Trip> filteredTrips;
    private int[] images;

    public TripAdapter(Context context, int[] images, ArrayList<Trip> trips) {
        this.mContext = context;
        this.retTrips = trips;
        this.images = images;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.filteredTrips = new ArrayList<>();
        this.filteredTrips.addAll(retTrips);

    }

    @Override
    public int getCount() {
        return filteredTrips.size();
    }

    @Override
    public Trip getItem(int i) {
        return filteredTrips.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup viewGroup) {
        View itemView = new View(mContext);
        itemView = inflater.inflate(R.layout.trip_item, null);

        ImageView tLogo = (ImageView) itemView.findViewById(R.id.logoUserItem);
        TextView tName = (TextView) itemView.findViewById(R.id.tripNameItem);
        TextView tPlace = (TextView) itemView.findViewById(R.id.meetPlaceItem);
        TextView tDate = (TextView) itemView.findViewById(R.id.dateTripItem);
        TextView tHour = (TextView) itemView.findViewById(R.id.timeTripItem);

        Trip selectedTrip = filteredTrips.get(position);
        tLogo.setImageResource(images[position % 11]);

        tName.setText(selectedTrip.getTripName());
        tPlace.setText(selectedTrip.getMeetingPlace());
        tDate.setText(selectedTrip.getDate());
        tHour.setText(selectedTrip.getTime());

        return itemView;
    }

    public void filter(String txt) {
        txt = txt.toLowerCase();
        filteredTrips.clear();
        if (txt.length() == 0) {
            filteredTrips.addAll(retTrips);
        } else {
            for (Trip tp : retTrips) {
                if (tp.getMeetingPlace().toLowerCase().contains(txt)) {
                    filteredTrips.add(tp);
                }
            }
        }
        notifyDataSetChanged();


    }
}