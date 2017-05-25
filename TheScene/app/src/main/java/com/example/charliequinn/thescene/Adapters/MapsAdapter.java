package com.example.charliequinn.thescene.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.charliequinn.thescene.ListItems.MapsItem;
import com.example.charliequinn.thescene.ListItems.PlaceItem;
import com.example.charliequinn.thescene.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by charliequinn on 1/18/17.
 */

public class MapsAdapter  extends ArrayAdapter<MapsItem> {


    public MapsAdapter(Context context, ArrayList<MapsItem> places) {
        super(context, 0, places);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        MapsItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_map, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.mapPlaceName);
        TextView address = (TextView) convertView.findViewById(R.id.address);
        TextView rating = (TextView) convertView.findViewById(R.id.rating);

        name.setText(item.name);
        address.setText(item.address);
        rating.setText(item.rating);



        return convertView;
    }
}
