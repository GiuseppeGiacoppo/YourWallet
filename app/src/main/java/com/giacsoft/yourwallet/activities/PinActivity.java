package com.giacsoft.yourwallet.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.giacsoft.yourwallet.R;

public class PinActivity extends Activity {

    EditText etpin;
    String mypin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_pin);

        etpin = (EditText) findViewById(R.id.etpin);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mypin = preferences.getString("pin", "");


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_pin, menu);
        return true;
    }

    // This method is called once the menu is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // We have only one menu option
            case R.id.menu_ok:
                // app icon in action bar clicked; go home
                String pin = etpin.getText().toString();
                if (mypin.equals(pin)) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.toast_error_wrong_pin, Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
