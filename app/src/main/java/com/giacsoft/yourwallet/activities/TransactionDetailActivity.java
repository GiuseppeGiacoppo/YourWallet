package com.giacsoft.yourwallet.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.giacsoft.yourwallet.R;
import com.giacsoft.yourwallet.adapters.MyAccountTitleAdapter;
import com.giacsoft.yourwallet.adapters.MyCategoryAdapter;
import com.giacsoft.yourwallet.db.MyDatabase;
import com.giacsoft.yourwallet.types.Account;
import com.giacsoft.yourwallet.types.Category;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TransactionDetailActivity extends Activity implements OnClickListener {

    ActionBar actionBar;
    MyDatabase db;
    EditText nameET, amountET;
    Spinner categoriesSP, accountsSP;
    Button data;
    public DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            cYear = year;
            cMonth = monthOfYear;
            cDay = dayOfMonth;
            String txt = String.format(getResources().getString(R.string.txt_transaction_date_format), getResources().getStringArray(R.array.months_filter_spinner)[cMonth + 1], cDay, cYear);
            data.setText(txt);
        }
    };
    int mYear, mMonth, mDay, cYear, cMonth, cDay, poscat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_transaction);


        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        LayoutInflater inflater = (LayoutInflater) getActionBar().getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customActionBarView = inflater.inflate(R.layout.actionbar_custom_view_done_discard, null);

        actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(customActionBarView, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        nameET = (EditText) findViewById(R.id.ettra);
        amountET = (EditText) findViewById(R.id.etimp);
        categoriesSP = (Spinner) findViewById(R.id.spincat);
        data = (Button) findViewById(R.id.date);
        accountsSP = (Spinner) findViewById(R.id.spinconti);


        String txt = String.format(getResources().getString(R.string.txt_transaction_date_format), getResources().getStringArray(R.array.months_filter_spinner)[mMonth + 1], mDay, mYear);
        data.setText(txt);
        cDay = mDay;
        cMonth = mMonth;
        cYear = mYear;

        db = new MyDatabase(getApplicationContext());
        db.open();

        ArrayList<Category> arraycat = db.getCategories();
        //TODO provare ad usare lo stesso layout
        MyCategoryAdapter adcat2 = new MyCategoryAdapter(this, R.layout.item_category_spinner, arraycat);
        //adcat2.setDropDownViewResource(R.layout.item_category);
        adcat2.setDropDownViewResource(R.layout.item_category_spinner);
        categoriesSP.setAdapter(adcat2);

        ArrayList<Account> arrayconti = db.getAccounts();
        MyAccountTitleAdapter adconti = new MyAccountTitleAdapter(this, R.layout.item_1line_spinner, arrayconti);
        adconti.setDropDownViewResource(R.layout.item_1line_spinner);
        accountsSP.setAdapter(adconti);

        data.setOnClickListener(this);
        customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (nameET.length() != 0 && amountET.length() != 0) {
                    int mese = cMonth + 1;
                    Calendar cal = new GregorianCalendar(cYear, mese, cDay);
                    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1=Domenica,
                    // 2=Lunedi
                    // ecc
                    if (accountsSP.getCount() < 1) {
                        Toast.makeText(getApplicationContext(), R.string.toast_alert_noaccount, Toast.LENGTH_SHORT).show();
                    } else if (categoriesSP.getCount() < 1) {
                        Toast.makeText(getApplicationContext(), R.string.toast_alert_nocategory, Toast.LENGTH_SHORT).show();
                    } else {
                        db.newTransaction(nameET.getText().toString(), Double.parseDouble(amountET.getText().toString()), categoriesSP.getSelectedItemId(), accountsSP.getSelectedItemId(), dayOfWeek, cDay, mese, cYear);
                        Toast.makeText(getApplicationContext(), R.string.toast_successful_transaction_add, Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.toast_error_completefields, Toast.LENGTH_SHORT).show();
                }
            }
        });

        customActionBarView.findViewById(R.id.actionbar_discard).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == data) {
            new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay).show();
        }
    }
}
