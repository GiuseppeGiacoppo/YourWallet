package com.giacsoft.yourwallet.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;

import com.giacsoft.yourwallet.R;

public class MyColorAdapter implements SpinnerAdapter {

    int res;
    Context context;
    private String colors[];

    public MyColorAdapter(Context context, int resource, String[] colors) {
        this.context = context;
        this.res = resource;
        this.colors = colors;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(res, null);
        } else {
            v= convertView;
        }

        Resources resources = context.getResources();

        int resid[] = new int[colors.length];
        for (int i = 0; i < colors.length; i++) {
            resid[i] = resources.getIdentifier(colors[i], "colors", context.getPackageName());
        }
        String color = colors[position];

        if (color != null) {
            View c = v.findViewById(R.id.col_ll);
            c.setBackgroundColor(ContextCompat.getColor(context, resid[position]));
        }
        return v;
    }

    @Override
    public long getItemId(int position) {
        // super.getItemId(position);
        return position;
    }

    @Override
    public int getCount() {
        return colors.length;
    }

    @Override
    public Object getItem(int position) {
        return colors[position];
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_spinner_elemento;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {}

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {}

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return this.getView(position, convertView, parent);
    }
}
