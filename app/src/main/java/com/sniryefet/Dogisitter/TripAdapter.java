package com.sniryefet.Dogisitter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.view.LayoutInflater;

public class TripAdapter extends BaseAdapter {
    private  Context mContext;
    private static LayoutInflater inflater=null;

    public TripAdapter(Context context){
        this.mContext = context;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertview, ViewGroup viewGroup) {
        TripsViewActivity tripView;
        if(convertview == null){
            //  tripView = new TripsViewActivity(mContext);
            //  tripView.setLayoutParam
            View itemView = inflater.inflate(R.layout.trip_item, null);


        }
        ////// not good!!!!
        View view= new View(mContext);
        return view;
    }
}
