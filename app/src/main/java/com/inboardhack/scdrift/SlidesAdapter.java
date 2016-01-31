package com.inboardhack.scdrift;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 * Created by benjaminran on 1/29/16.
 */
public class SlidesAdapter extends BaseAdapter implements Observer {

    private Context context;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    public SlidesAdapter(Context context) {
        super();
        this.context = context;
        SlideHistory.getInstance().registerObserver(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) { //TODO: NotifyDatasetChanged
        View rowView;
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.slide, parent, false);
        }
        else {
            rowView = convertView;
        }
        TextView scoreView = (TextView) rowView.findViewById(R.id.slide_score);
        TextView durationView = (TextView) rowView.findViewById(R.id.slide_duration);
        TextView distanceView = (TextView) rowView.findViewById(R.id.slide_datetime);
        scoreView.setText("" + ((Slide) getItem(position)).getScore());
        durationView.setText("" + (int) Math.round(((Slide) getItem(position)).getDuration()));
        distanceView.setText(DATE_FORMAT.format(((Slide) getItem(position)).getEndTime()));
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

    @Override
    public void observeUpdate(Object o) {
        ((MainActivity) context).runOnUiThread(new Runnable() {
                                                   @Override
                                                   public void run() {
                                                       notifyDataSetChanged();
                                                   }
                                               });
    }
}
