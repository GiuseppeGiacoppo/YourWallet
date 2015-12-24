package com.giacsoft.yourwallet.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.giacsoft.yourwallet.R;
import com.giacsoft.yourwallet.Utils;

import java.io.File;
import java.io.IOException;

public class ImportDBDialogFragment extends DialogFragment {

    Resources res;

    public static ImportDBDialogFragment newInstance() {
        ImportDBDialogFragment frag = new ImportDBDialogFragment();
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        res = getActivity().getApplicationContext().getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(Html.fromHtml(res.getString(R.string.alert_confirm_overwritedb)));
        builder.setCancelable(true);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new ImportDatabaseFileTask().execute();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return builder.create();
        // return super.onCreateDialog(savedInstanceState);
    }

    private class ImportDatabaseFileTask extends AsyncTask<String, Void, Boolean> {
        // can use UI thread here
        protected void onPreExecute() {
        }

        // automatically done on worker thread (separate from UI thread)
        protected Boolean doInBackground(final String... args) {


            File dbFile = new File(Environment.getExternalStorageDirectory() + "/YourWallet/yourwallet");
            if (dbFile.exists()) {
                File exportDir = new File(Environment.getDataDirectory() + "/data/com.giacsoft.yourwallet/databases", "");
                File file = new File(exportDir, dbFile.getName());

                try {
                    file.createNewFile();
                    Utils.copyFile(dbFile, file);
                    return true;
                } catch (IOException e) {
                    Log.e("yourwallet", e.getMessage(), e);
                    return false;
                }
            } else {
                return false;
            }
        }

        // can use UI thread here
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.toast_successful_dbimport, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), R.string.toast_failed_dbimport, Toast.LENGTH_SHORT).show();
            }
        }

    }
}
