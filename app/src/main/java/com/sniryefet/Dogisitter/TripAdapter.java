package com.sniryefet.Dogisitter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

public class TripAdapter extends BaseAdapter {
    private  Context mContext;

    public TripAdapter(Context context){
        this.mContext = context;
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


        }
        ////// not good!!!!
        View view= new View(mContext);
        return view;
    }
}
