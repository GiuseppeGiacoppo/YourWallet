package com.giacsoft.yourwallet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.giacsoft.yourwallet.R;
import com.giacsoft.yourwallet.types.Account;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MyAccountAdapter extends ArrayAdapter<Account> {

    int resid;
    Context context;
    String cur;
    DecimalFormat df = new DecimalFormat("0.00");
    private ArrayList<Account> items;

    public MyAccountAdapter(Context _context, int textViewResourceId, ArrayList<Account> _items, String _cur) {
        super(_context, textViewResourceId, _items);
        context = _context;
        resid = textViewResourceId;
        items = _items;
        cur = _cur;
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

        TextView account_title = (TextView) v.findViewById(R.id.tv_conto_name);
        TextView transactions_number = (TextView) v.findViewById(R.id.tv_conto_transactionnum);
        TextView account_total = (TextView) v.findViewById(R.id.tv_conto_amount);

        account_title.setText(istanzaConto.name);

        transactions_number.setText(context.getResources().getQuantityString(R.plurals.transactionPlurals, istanzaConto.transactions_number, istanzaConto.transactions_number));

        account_total.setText(df.format(istanzaConto.total) + " " + cur);

        if (istanzaConto.total >= 0) {
            account_total.setTextColor(context.getResources().getColor(R.color.positive));
        } else {
            account_total.setTextColor(context.getResources().getColor(R.color.negative));
        }

        return v;
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).id;
    }
}
