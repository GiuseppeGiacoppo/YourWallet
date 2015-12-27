package com.giacsoft.yourwallet.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.giacsoft.yourwallet.R;
import com.giacsoft.yourwallet.Utils;
import com.giacsoft.yourwallet.dialogs.ImportDBDialogFragment;
import com.giacsoft.yourwallet.dialogs.ImportLiteDialogFragment;

import java.io.File;
import java.io.IOException;


public class AdvancedActivity extends BaseActivity implements OnClickListener {

    boolean clicked;
    Button exportdb, importlite, importdb;
    ActionBar actionBar;
    File exportDir2 = new File(Environment.getExternalStorageDirectory() + "/YourWallet", "");
    File prevback = new File(exportDir2, "yourwallet");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_advanced);
        clicked = false;
        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.activity_title_database);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

        exportdb = (Button) findViewById(R.id.esportadb);
        importdb = (Button) findViewById(R.id.importadb);
        importlite = (Button) findViewById(R.id.importalite);

        exportdb.setOnClickListener(this);
        importdb.setOnClickListener(this);
        importlite.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_advanced, menu);
        return true;
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
        if (v == exportdb) {
            if (clicked == false) {
                if (prevback.exists()) {
                    exportdb.setText(R.string.btn_alert_existing_backup);
                    clicked = true;
                } else {
                    new ExportDatabaseFileTask().execute();
                }
            } else {
                clicked = false;
                new ExportDatabaseFileTask().execute();
            }

        } else if (v == importdb) {
            showDialog(Utils.IMPORT_DIALOG,getFragmentManager());
        } else if (v == importlite) {
            showDialog(Utils.IMPORTLITE_DIALOG,getFragmentManager());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private class ExportDatabaseFileTask extends AsyncTask<String, Void, Boolean> {
        File exportDir;

        // can use UI thread here
        protected void onPreExecute() {
        }

        // automatically done on worker thread (separate from UI thread)
        protected Boolean doInBackground(final String... args) {

            File dbFile = new File(Environment.getDataDirectory() + "/data/com.giacsoft.yourwallet/databases/yourwallet");

            exportDir = new File(Environment.getExternalStorageDirectory() + "/YourWallet", "");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            File file = new File(exportDir, dbFile.getName());
            try {
                file.createNewFile();
                Utils.copyFile(dbFile, file);
                return true;
            } catch (IOException e) {
                Log.e("yourwallet", e.getMessage(), e);
                return false;
            }

        }

        // can use UI thread here
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Toast.makeText(getApplicationContext(), R.string.toast_successful_dbexport, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.toast_failed_dbexport, Toast.LENGTH_SHORT).show();
            }
            exportdb.setText(R.string.btn_export_currentdb);
        }
    }
}
