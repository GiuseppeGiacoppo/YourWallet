package com.giacsoft.yourwallet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.giacsoft.yourwallet.R;
import com.giacsoft.yourwallet.types.Account;

import java.util.ArrayList;

public class MyAccountTitleAdapter extends ArrayAdapter<Account> {

    int resid;
    Context context;
    private ArrayList<Account> items;

    public MyAccountTitleAdapter(Context _context, int textViewResourceId, ArrayList<Account> _items) {
        super(_context, textViewResourceId, _items);
        context = _context;
        resid = textViewResourceId;
        items = _items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(resid, null);
        } else {
            v = convertView;
        }

        Account istanzaConto = items.get(position);

        TextView account_title = (TextView) v.findViewById(R.id.spinner1line_title);
        account_title.setText(istanzaConto.name);

        return v;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return items.get(position).id;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return this.getView(position, convertView, parent);
    }
}
