package com.example.charliequinn.thescene.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.charliequinn.thescene.ListItems.PlaceItem;
import com.example.charliequinn.thescene.R;

import java.util.ArrayList;

/**
 * Created by charliequinn on 1/18/17.
 */

public class PlaceAdapter extends ArrayAdapter<PlaceItem> {


    public PlaceAdapter(Context context, ArrayList<PlaceItem> places) {
        super(context, 0, places);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        PlaceItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_place, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.place_name);

        name.setText(item.placeName);

        return convertView;
    }
}
