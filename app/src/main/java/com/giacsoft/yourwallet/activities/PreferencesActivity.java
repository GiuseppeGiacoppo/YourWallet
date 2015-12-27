package com.giacsoft.yourwallet.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.giacsoft.yourwallet.R;
import com.giacsoft.yourwallet.Utils;
import com.giacsoft.yourwallet.dialogs.CurrencyDialogFragment;
import com.giacsoft.yourwallet.dialogs.PinDialogFragment;

public class PreferencesActivity extends BaseActivity implements View.OnClickListener {

    TextView pr_currency, pr_database;
    LinearLayout pr_pin;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_preferences);

        pr_currency = (TextView) findViewById(R.id.preferences_currency);
        pr_database = (TextView) findViewById(R.id.preferences_database);
        pr_pin = (LinearLayout) findViewById(R.id.preferences_pin);

        toolbar = getActionBarToolbar();
        setActionBar(toolbar);
        pr_currency.setOnClickListener(this);
        pr_database.setOnClickListener(this);
        pr_pin.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_preferences, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.preferences_currency:
                showDialog(Utils.CURRENCY_DIALOG,getFragmentManager());
                break;
            case R.id.preferences_database:
                startActivity(new Intent(getApplicationContext(), AdvancedActivity.class));
                break;
            case R.id.preferences_pin:
                showDialog(Utils.PIN_DIALOG,getFragmentManager());
                break;
        }
    }
}
