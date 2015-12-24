package com.giacsoft.yourwallet.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.giacsoft.yourwallet.R;
import com.giacsoft.yourwallet.types.Category;
import com.giacsoft.yourwallet.types.Transaction;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MyTransactionAdapter extends ArrayAdapter<Transaction> {

    Context context;
    int resid;
    ArrayList<Transaction> items;
    String cur;
    DecimalFormat df = new DecimalFormat("0.00");
    DecimalFormat datef = new DecimalFormat("00");
    Resources resources;

    ArrayList<Category> list_cat;
    Category istanzaCategoria;

    public MyTransactionAdapter(Context _context, int textViewResourceId, ArrayList<Transaction> _items, String _cur, ArrayList<Category> _list_cat) {
        super(_context, textViewResourceId, _items);
        context = _context;
        resid = textViewResourceId;
        items = _items;
        cur = _cur;
        list_cat = _list_cat;
        resources = context.getResources();
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

        Transaction istanzaTransazione = items.get(position);

        for (int i = 0; i < list_cat.size(); i++) {
            if (istanzaTransazione.category == list_cat.get(i).id) {
                istanzaCategoria = list_cat.get(i);
                break;
            }
        }

        Calendar cal = new GregorianCalendar(istanzaTransazione.year, istanzaTransazione.month - 1, istanzaTransazione.day);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        TextView day_string = (TextView) v.findViewById(R.id.tvgiorno);
        TextView day = (TextView) v.findViewById(R.id.tvdata);
        TextView month = (TextView) v.findViewById(R.id.tvmese);
        TextView transaction_title = (TextView) v.findViewById(R.id.tvnota);
        TextView transaction_category = (TextView) v.findViewById(R.id.tvconto);
        TextView transaction_amount = (TextView) v.findViewById(R.id.tvimporto);
        View category_color = v.findViewById(R.id.llbgcat);
        int color = resources.getIdentifier(istanzaCategoria.color, "color", context.getPackageName());

        if (color != 0)
            category_color.setBackgroundColor(ContextCompat.getColor(context,color));
        else
            category_color.setBackgroundColor(ContextCompat.getColor(context,R.color.color0));

        day_string.setText(resources.getStringArray(R.array.week)[dayOfWeek]);
        day.setText(datef.format(istanzaTransazione.day));
        month.setText("/" + datef.format(istanzaTransazione.month));
        transaction_title.setText(istanzaTransazione.note);
        transaction_category.setText(istanzaCategoria.name);
        if (istanzaTransazione.amount >= 0) {
            transaction_amount.setText("+" + df.format(Math.abs(istanzaTransazione.amount)) + " " + cur);
            transaction_amount.setTextColor(ContextCompat.getColor(context, R.color.positive));
        } else {
            transaction_amount.setText("-" + df.format(Math.abs(istanzaTransazione.amount)) + " " + cur);
            transaction_amount.setTextColor(ContextCompat.getColor(context, R.color.negative));
        }

        return v;
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).id;
    }

}
