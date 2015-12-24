package com.giacsoft.yourwallet.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.giacsoft.yourwallet.R;
import com.giacsoft.yourwallet.db.MyDatabase;
import com.giacsoft.yourwallet.types.Category;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MyCategoryAdapter extends ArrayAdapter<Category> {

    int posizioneCategoria;
    Context context;
    int resid;
    MyDatabase db;
    String cur;
    boolean isSpinner;
    DecimalFormat df = new DecimalFormat("0.00");
    Category istanzaCategoria;
    private ArrayList<Category> items;

    public MyCategoryAdapter(Context _context, int textViewResourceId, ArrayList<Category> _items) {
        super(_context, textViewResourceId, _items);
        items = _items;
        context = _context;
        isSpinner = true;
        resid = textViewResourceId;
    }

    public MyCategoryAdapter(Context _context, int textViewResourceId, ArrayList<Category> _items, String _cur) {
        super(_context, textViewResourceId, _items);
        items = _items;
        context = _context;
        cur = _cur;
        isSpinner = false;
        resid = textViewResourceId;
        db = new MyDatabase(context);
        db.open();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        posizioneCategoria = position;
        View v;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(resid, null);
        } else {
            v = convertView;
        }
        istanzaCategoria = items.get(position);

        TextView tc = (TextView) v.findViewById(R.id.tv_category_name);
        View bg = (View) v.findViewById(R.id.view_category_color);

        Resources resources = context.getResources();
        int resid = resources.getIdentifier(istanzaCategoria.color, "color", context.getPackageName());

        if (resid != 0)
            bg.setBackgroundColor(resources.getColor(resid));
        else
            bg.setBackgroundColor(resources.getColor(R.color.color2));

        tc.setText(istanzaCategoria.name);

        if (!isSpinner) {
            TextView tci = (TextView) v.findViewById(R.id.tv_category_amount);
            TextView tct = (TextView) v.findViewById(R.id.tv_category_trans_num);
            double catTotale = db.getTotalAmountbyCategory(istanzaCategoria.id);
            tci.setText(df.format(catTotale) + " " + cur);
            int catTotaleTrans = db.getTransactionsNumberByCategory(istanzaCategoria.id);
            tct.setText(context.getResources().getQuantityString(R.plurals.transactionPlurals, catTotaleTrans, catTotaleTrans));
            if (catTotale >= 0)
                tci.setTextColor(resources.getColor(R.color.positive));
            else
                tci.setTextColor(resources.getColor(R.color.negative));
        }

        return v;
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).id;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return this.getView(position, convertView, parent);
    }

}
