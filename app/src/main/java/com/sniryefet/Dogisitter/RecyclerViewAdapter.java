package com.sniryefet.Dogisitter;

import android.content.Context;
import android.util.LayoutDirection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "dRecyclerViewAdapter";
    private ArrayList<UploadImage> mImageUrls = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter( ArrayList<UploadImage> mImageUrls, Context mContext) {
        this.mImageUrls = mImageUrls;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG,"onCreateViewHolder:called");
        View view = LayoutInflater.from(
                mContext)
                .inflate(R.layout.circle_image_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        UploadImage currentUpload=mImageUrls.get(position);

        Picasso.get()
                .load(currentUpload.getmImageUrl())
                .fit()
                .rotate(90)
                .into(holder.image);



    }

    @Override
    public int getItemCount() {
        return mImageUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;

        public ViewHolder(View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.circle_image); // MAYBE NOT THE RIGHT LINKAGE

        }
    }
}