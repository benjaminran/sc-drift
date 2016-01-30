package com.inboardhack.scdrift;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by benjaminran on 1/29/16.
 */
public class SlidesView extends ListView implements AdapterView.OnItemClickListener {

    private SlidesAdapter adapter;

    public SlidesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        adapter = new SlidesAdapter(context);
        setOnItemClickListener(this);
        setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getContext(), "Clicked Slide Number " + position, Toast.LENGTH_LONG).show();
    }
}
