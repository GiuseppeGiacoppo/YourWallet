package com.giacsoft.yourwallet.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.DialogFragment;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.giacsoft.yourwallet.R;

import java.io.File;
import java.io.IOException;

import static com.giacsoft.yourwallet.Utils.*;

public class ImportLiteDialogFragment extends DialogFragment {

    Resources res;
    Context ctx;

    public static ImportLiteDialogFragment newInstance() {
        ImportLiteDialogFragment frag = new ImportLiteDialogFragment();
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ctx = getActivity().getApplicationContext();
        res = ctx.getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(Html.fromHtml(res.getString(R.string.alert_confirm_overwrite_litetofull)));
        builder.setCancelable(true);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new ImportDatabaseLiteFileTask().execute();
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

    private class ImportDatabaseLiteFileTask extends AsyncTask<String, Void, Boolean> {
        // can use UI thread here
        protected void onPreExecute() {
        }

        // automatically done on worker thread (separate from UI thread)
        protected Boolean doInBackground(final String... args) {

            File dbFile = new File(Environment.getExternalStorageDirectory() + "/YourWallet/lite/yourwallet");
            if (dbFile.exists()) {
                File exportDir = new File(Environment.getDataDirectory() + "/data/com.giacsoft.yourwallet/databases", "");
                File file = new File(exportDir, "yourwallet");

                try {
                    file.createNewFile();
                    copyFile(dbFile, file);
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
                Toast.makeText(ctx, R.string.toast_successful_dbimport, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ctx, R.string.toast_failed_dbimport, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
