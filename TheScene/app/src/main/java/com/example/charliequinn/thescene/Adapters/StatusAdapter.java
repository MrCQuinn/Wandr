package com.example.charliequinn.thescene.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.charliequinn.thescene.ListItems.StatusItem;
import com.example.charliequinn.thescene.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by charliequinn on 1/16/17.
 */

public class StatusAdapter extends ArrayAdapter<StatusItem> {
    private HashMap<Integer, Bitmap> photos;
    public StatusAdapter(Context context, ArrayList<StatusItem> statuses, HashMap<Integer, Bitmap> photos) {
        super(context, 0, statuses);
        this.photos = photos;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        StatusItem item = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_status, parent, false);
        }


        TextView status = (TextView) convertView.findViewById(R.id.status);
        Spanned statusString = Html.fromHtml("<b>"+item.name + "</b> Checked in to <b><font color='blue'>" + item.status+"</b></font>");
        status.setText(statusString);

        if(photos.get(item.userIDX)==null){
            Log.i("StatusAdapter", "id "+item.userIDX+" missing picture");
        }


        ImageButton profilePicture = (ImageButton) convertView.findViewById(R.id.statusPicture);

        if(photos.get(item.userIDX)==null){
            item.setPhoto(photos.get(-1));
            profilePicture.setImageBitmap(photos.get(-1));
        }else{
            item.setPhoto(photos.get(item.userIDX));
            profilePicture.setImageBitmap(photos.get(item.userIDX));
        }





//        TextView tvAddress = (TextView) convertView.findViewById(R.id.tvActualAddress);
//        TextView tvDistance = (TextView) convertView.findViewById(R.id.tvActualDistance);
//
//        name.setText(walkItem._name);
//        tvAddress.setText(""+ walkItem._address);
//        tvDistance.setText(walkItem._distance + " miles");
//
//        tvName.setTextSize(24);
//        tvAddress.setTextSize(22);
//        tvDistance.setTextSize(22);
//
//        name.setTypeface(null, Typeface.BOLD);
//        tvDistance.setTypeface(null, Typeface.BOLD);

        return convertView;
    }



}
