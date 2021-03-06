package com.robillo.sasuke.dailysuvichar.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.robillo.sasuke.dailysuvichar.R;
import com.robillo.sasuke.dailysuvichar.activity.ProfileActivity;
import com.robillo.sasuke.dailysuvichar.newactivities.YourFeedsActivity;

public class VHFeature extends RecyclerView.ViewHolder{

    public ImageView photo;
    public TextView header;
    public CardView cardView;
    private Context context;

    public VHFeature(View itemView) {
        super(itemView);
        context = itemView.getContext();
        photo = (ImageView) itemView.findViewById(R.id.image);
        header = (TextView) itemView.findViewById(R.id.text);
        cardView = (CardView) itemView.findViewById(R.id.cardView);
    }

    public void intent(int position){
        switch (position){
            case 1:{
                Intent i =new Intent(context, YourFeedsActivity.class);
                i.putExtra("fromHome",1);
                context.startActivity(i);
                break;
            }
            case 2:{
                Intent i = new Intent(context, ProfileActivity.class);
                i.putExtra("fromHome", 1);
                context.startActivity(i);
                break;
            }
            case 3:{
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.firstapp.robinpc.tongue_twisters_deluxe"));
                context.startActivity(i);
                break;
            }
        }
    }
}
