package com.giacsoft.yourwallet.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.giacsoft.yourwallet.R;
import com.giacsoft.yourwallet.Utils;
import com.giacsoft.yourwallet.adapters.MyCategoryAdapter;
import com.giacsoft.yourwallet.db.MyDatabase;
import com.giacsoft.yourwallet.dialogs.CategoriesDialogFragment;
import com.giacsoft.yourwallet.types.Category;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;


import java.util.ArrayList;
import java.util.Iterator;

public class CategoriesActivity extends Activity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, CategoriesDialogFragment.OnCategorieDialogListener, OnClickListener {

    private static final int DLG1 = 1;
    private static final int DLG2 = 2;
    ListView cat;
    RelativeLayout newCategory;
    ArrayList<Category> categories;
    PieChart pieChart;
    SharedPreferences preferences;
    MyDatabase db;
    String cur;
    MyCategoryAdapter adapter;
    Toolbar toolbar;
    Context ctx;

    long accountID;
    int provenienza;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_categories);

        accountID = getIntent().getExtras().getLong(Utils.ACCOUNT_ID);
        provenienza = getIntent().getExtras().getInt(Utils.CAT_PREV_ACTIVITY);

        toolbar = (Toolbar) findViewById(R.id.categories_toolbar);
        toolbar.setTitle(R.string.menu_categories);

        ctx = getApplicationContext();
        db = new MyDatabase(ctx);
        db.open();
        categories = db.getCategories();
        db.close();

        cat = (ListView) findViewById(R.id.listcat);
        pieChart = (PieChart) findViewById(R.id.pieChart);
        newCategory = (RelativeLayout) findViewById(R.id.rl_newcategory);

        preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        cur = preferences.getString("currency", "$");

        adapter = new MyCategoryAdapter(this, R.layout.item_category, categories, cur);
        cat.setAdapter(adapter);
        cat.setOnItemLongClickListener(this);
        cat.setOnItemClickListener(this);
        newCategory.setOnClickListener(this);

        ArrayList<Entry> yVals = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        int[] cVals = new int[categories.size()];
        Iterator<Category> it = categories.iterator();

        Resources resources = ctx.getResources();
        int resid;

        for(int k=0;it.hasNext();k++){
            System.out.println("Iterazione: "+k);
            Category c=it.next();
            xVals.add(c.name);
            yVals.add(new Entry((float) c.totalAmount, k));
            resid = resources.getIdentifier(c.color, "color", ctx.getPackageName());
            //cVals.add(Color.parseColor(resources.getString(resid)));
            cVals[k]=Color.parseColor(resources.getString(resid));
        }

        PieDataSet ds1 = new PieDataSet(yVals, "PieDataSet");
        ds1.setColors(cVals);

        PieData d = new PieData(xVals,ds1);
        pieChart.setData(d);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_categorie, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home

                if (provenienza == 0) {// is phone
                    startActivity(new Intent(this, AccountActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(Utils.ACCOUNT_ID, accountID));
                } else if (provenienza == 1) {// is tablet
                    startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                } else if (provenienza == 2) {// from transfer
                    startActivity(new Intent(this, TransferActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {

        if (arg0.getId() == R.id.listcat) {
            showDialog(DLG2, categories.get(position), categories.get(position).getColoreId(ctx), position);
            return false;
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Toast.makeText(ctx, R.string.toast_hint_holdtoedit, Toast.LENGTH_SHORT).show();
    }
/*
    @Override
    public void onBackPressed() {
        if (provenienza == 0) {// is phone
            startActivity(new Intent(this, AccountActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(Utils.ACCOUNT_ID, accountID));
        } else if (provenienza == 1) {// is tablet
            startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else if (provenienza == 2) {// from transfer
            startActivity(new Intent(this, TransferActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }
*/

    public void showDialog(int id, Category c, int color_position, int position) {
        DialogFragment newFragment;
        switch (id) {
            case DLG1:
                newFragment = CategoriesDialogFragment.newInstance();
                newFragment.show(getFragmentManager(), "categoriedialog");
                break;
            case DLG2:
                newFragment = CategoriesDialogFragment.newInstance(c, color_position, position);
                newFragment.show(getFragmentManager(), "categorieeditdialog");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == newCategory)
            showDialog(DLG1, null, 0, 0);
    }

    @Override
    public void doCategory(int mode, Category c, int p) {
        switch (mode) {
            case Utils.ADD:
                categories.add(c);
                adapter.notifyDataSetChanged();
                break;
            case Utils.EDIT:
                categories.set(p, c);
                adapter.notifyDataSetChanged();
/*      myPie.editDoughnutChartCategory(p, c);
        mChartView.repaint();*/
                break;
            case Utils.DELETE:
                categories.remove(p);
                adapter.notifyDataSetChanged();
/*      mChartView = myPie.getDoughnutChart(categories);
        graph.removeAllViews();
        graph.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));*/
                break;
        }
    }
}
