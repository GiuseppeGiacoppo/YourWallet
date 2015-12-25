package com.giacsoft.yourwallet.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import com.giacsoft.yourwallet.R;
import com.giacsoft.yourwallet.Utils;
import com.giacsoft.yourwallet.adapters.MyAccountTitleAdapter;
import com.giacsoft.yourwallet.adapters.MyCategoryAdapter;
import com.giacsoft.yourwallet.db.MyDatabase;
import com.giacsoft.yourwallet.types.Account;
import com.giacsoft.yourwallet.types.Category;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TransferActivity extends BaseActivity implements View.OnClickListener {

    EditText amountET;
    Spinner fromSP, toSP, cat;
    MyDatabase db;
    SharedPreferences preferences;
    String cur;
    ImageView ivlabel;
    int mDay, mMonth, mYear;
    //ActionBar actionBar;
    Toolbar toolbar;
    Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_transfer);

        LayoutInflater inflater = (LayoutInflater) getActionBar().getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customActionBarView = inflater.inflate(R.layout.actionbar_custom_view_done_discard, null);
/*
        actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(customActionBarView, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
*/ toolbar = getActionBarToolbar();
        amountET = (EditText) findViewById(R.id.etimp);
        fromSP = (Spinner) findViewById(R.id.fromaccount);
        toSP = (Spinner) findViewById(R.id.toaccount);
        ivlabel = (ImageView) findViewById(R.id.tab_label);
        cat = (Spinner) findViewById(R.id.spincat);
        res = getResources();
        db = new MyDatabase(getApplicationContext());
        db.open();

        // get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        ivlabel.setOnClickListener(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        cur = preferences.getString("currency", "$");

        ArrayList<Account> arrayconti = db.getAccounts();
        //Conti2Adapter spinconti = new Conti2Adapter(this, arrayconti, cur);
        MyAccountTitleAdapter spinconti = new MyAccountTitleAdapter(this, R.layout.item_1line_spinner, arrayconti);
        spinconti.setDropDownViewResource(R.layout.item_1line_spinner);
        fromSP.setAdapter(spinconti);
        toSP.setAdapter(spinconti);

        customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // "Done"
                        if (amountET.length() == 0) {
                            Toast.makeText(getApplicationContext(), R.string.toast_error_completefields, Toast.LENGTH_SHORT).show();
                        } else {
                            if (cat.getCount() < 1) {
                                Toast.makeText(getApplicationContext(), R.string.toast_alert_nocategory, Toast.LENGTH_SHORT).show();
                            } else {
                                if (fromSP.getSelectedItemId() == toSP.getSelectedItemId()) {
                                    Toast.makeText(getApplicationContext(), R.string.toast_error_select_distinct_account, Toast.LENGTH_SHORT).show();
                                } else {

                                    Calendar cal = new GregorianCalendar(mYear, mMonth, mDay);
                                    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1=Domenica,
                                    // 2=Lunedi
                                    // ecc

                                    double sottrai = Double.parseDouble(amountET.getText().toString());
                                    sottrai *= (-1);
                                    double aggiungi = Double.parseDouble(amountET.getText().toString());
                                    int mese = mMonth + 1;

                                    db.addTransaction(res.getString(R.string.txt_transfer_desc), sottrai, cat.getSelectedItemId(), fromSP.getSelectedItemId(), dayOfWeek, mDay, mese, mYear);
                                    db.addTransaction(res.getString(R.string.txt_transfer_desc), aggiungi, cat.getSelectedItemId(), toSP.getSelectedItemId(), dayOfWeek, mDay, mese, mYear);
                                    finish();
                                }
                            }
                        }
                    }
                });
        customActionBarView.findViewById(R.id.actionbar_discard).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // "Discard"
                        onBackPressed();
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<Category> arraycat = db.getCategories();
        MyCategoryAdapter spincat = new MyCategoryAdapter(this, R.layout.item_category_spinner, arraycat);
        cat.setAdapter(spincat);
    }

    @Override
    public void onClick(View v) {
        if (v == ivlabel) {
            startActivity(new Intent(getApplicationContext(), CategoriesActivity.class).putExtra(Utils.CAT_PREV_ACTIVITY, 2));
        }
    }
}
