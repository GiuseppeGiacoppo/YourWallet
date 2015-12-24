package com.giacsoft.yourwallet.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;

import com.giacsoft.yourwallet.R;

public class OverwriteDialogFragment extends DialogFragment {

    Resources res;
    SharedPreferences settings;
    Context ctx;

    public static OverwriteDialogFragment newInstance() {
        OverwriteDialogFragment frag = new OverwriteDialogFragment();
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        settings = getActivity().getSharedPreferences("RATING", 0);
        ctx = getActivity().getApplicationContext();
        res = ctx.getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.menu_about);
        builder.setMessage(Html.fromHtml(res.getString(R.string.dialog_about_text)));
        builder.setCancelable(true);

        builder.setPositiveButton(R.string.dialog_btn_rate, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences.Editor editor = settings.edit();
                editor.putString("opzione", "1");
                editor.commit();

                Intent intentrate = new Intent(Intent.ACTION_VIEW);
                intentrate.setData(Uri.parse("market://details?id=com.giacsoft.yourwallet"));
                startActivity(intentrate);
            }
        });
        builder.setNegativeButton(R.string.dialog_btn_contact, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intentmail = new Intent(Intent.ACTION_SEND);
                intentmail.setType("message/rfc822");
                intentmail.putExtra(Intent.EXTRA_EMAIL, new String[]{"giacsoft@gmail.com"});
                intentmail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intentmail, "Send Email"));
            }
        });
        return builder.create();
        // return super.onCreateDialog(savedInstanceState);
    }

}
