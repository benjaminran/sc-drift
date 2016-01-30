package com.inboardhack.scdrift;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by benjaminran on 1/29/16.
 */
public class SlidesAdapter extends BaseAdapter {

    private Context context;

    public SlidesAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.slide, parent, false);
        }
        else {
            rowView = convertView;
        }
        TextView scoreView = (TextView) rowView.findViewById(R.id.slide_score);
        TextView lengthView = (TextView) rowView.findViewById(R.id.slide_length);
        TextView distanceView = (TextView) rowView.findViewById(R.id.slide_distance);
        scoreView.setText(""+((Slide) getItem(position)).getScore());
//        lengthView.setText(((Slide) getItem(position)).getLength()); // TODO: possible?
//        distanceView.setText(((Slide) getItem(position)).getDistance()); // TODO: possible?
        return rowView;
    }

    @Override
    public int getCount() {

        return SlideHistory.getInstance().size();
    }

    @Override
    public Object getItem(int position) { // TODO: add option to sort by score, not just time
        return SlideHistory.getInstance().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
