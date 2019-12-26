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
    private int[] images;

    public TripAdapter(Context context, int[] images, ArrayList<Trip> trips){
        this.mContext = context;
        this.retTrips = trips;
        this.images = images;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return retTrips.size();
    }

    @Override
    public Trip getItem(int i) {
        return retTrips.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

//    public class Holder
//    {
//        TextView tName;
//        TextView tPlace;
//        TextView tDate;
//        TextView tHour;
//        ImageView tLogo;
//    }
    @Override
    public View getView(int position, View convertview, ViewGroup viewGroup) {
//        Holder holder=new Holder();
        View itemView = new View(mContext);
        //itemView = (itemView == null) ? inflater.inflate(R.layout.trip_item, null): itemView;
        //  tripView = new TripsViewActivity(mContext);
        //  tripView.setLayoutParam
        //itemView = new View(mContext);
        itemView = inflater.inflate(R.layout.trip_item, null);

        ImageView tLogo = (ImageView) itemView.findViewById(R.id.logoUserItem);
        TextView tName = (TextView) itemView.findViewById(R.id.tripNameItem);
        TextView tPlace = (TextView) itemView.findViewById(R.id.meetPlaceItem);
        TextView tDate = (TextView) itemView.findViewById(R.id.dateTripItem);
        TextView tHour = (TextView) itemView.findViewById(R.id.timeTripItem);

        Trip selectedTrip = retTrips.get(position);
        tLogo.setImageResource(images[position % 11]);
//        Glide.with(mContext)
//                .load(images[position % 11])
//                .into(tLogo);

        tName.setText(selectedTrip.getTripName());
        tPlace.setText(selectedTrip.getMeetingPlace());
        tDate.setText(selectedTrip.getDate());
        tHour.setText(selectedTrip.getTime());

        return itemView;


    }
}