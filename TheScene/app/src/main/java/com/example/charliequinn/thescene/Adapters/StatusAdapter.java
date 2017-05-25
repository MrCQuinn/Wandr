package com.example.charliequinn.thescene.Adapters;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.charliequinn.thescene.ListItems.StatusItem;
import com.example.charliequinn.thescene.R;

import java.util.ArrayList;

/**
 * Created by charliequinn on 1/16/17.
 */

public class StatusAdapter extends ArrayAdapter<StatusItem> {
    public StatusAdapter(Context context, ArrayList<StatusItem> statuses) {
        super(context, 0, statuses);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        StatusItem item = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_status, parent, false);
        }


        TextView status = (TextView) convertView.findViewById(R.id.status);
        Spanned statusString = Html.fromHtml("<b>"+item.name + "</b> Checked in to <b><font color='blue'>" + item.status+"</b></font>");
        status.setText(statusString);




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
