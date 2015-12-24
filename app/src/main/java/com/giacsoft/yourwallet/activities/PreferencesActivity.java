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

import com.giacsoft.yourwallet.R;
import com.giacsoft.yourwallet.dialogs.CurrencyDialogFragment;
import com.giacsoft.yourwallet.dialogs.PinDialogFragment;

public class PreferencesActivity extends Activity {

    private static final int DLG1 = 1;
    private static final int DLG2 = 2;
    FrameLayout pr_currency, pr_database;
    LinearLayout pr_pin;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_preferences);

        pr_currency = (FrameLayout) findViewById(R.id.preferences_currency);
        pr_database = (FrameLayout) findViewById(R.id.preferences_database);
        pr_pin = (LinearLayout) findViewById(R.id.preferences_pin);

        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.menu_settings);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        pr_currency.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mostraDialog(DLG1);
            }
        });
        pr_database.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AdvancedActivity.class));
            }
        });

        pr_pin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mostraDialog(DLG2);
            }
        });
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

    void mostraDialog(int id) {
        switch (id) {
            case DLG1:
                CurrencyDialogFragment cdf2 = CurrencyDialogFragment.newInstance();
                cdf2.show(getFragmentManager(), "currencydialog");
                break;
            case DLG2:
                PinDialogFragment pdf = PinDialogFragment.newInstance();
                pdf.show(getFragmentManager(), "pindialog");
                break;
        }
    }
}
