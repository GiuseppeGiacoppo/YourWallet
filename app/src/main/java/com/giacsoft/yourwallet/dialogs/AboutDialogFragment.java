package com.giacsoft.yourwallet.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.giacsoft.yourwallet.R;

public class AboutDialogFragment extends DialogFragment {

    Resources res;
    SharedPreferences settings;
    TextView message;

    public static AboutDialogFragment newInstance() {
        AboutDialogFragment frag = new AboutDialogFragment();
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        settings = getActivity().getSharedPreferences("RATING", 0);
        res = getActivity().getApplicationContext().getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.menu_about);

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view_dialog = inflater.inflate(R.layout.dialog_about, null);

        //builder.setMessage(Html.fromHtml(res.getString(R.string.dialog_about_text)));
        message = (TextView) view_dialog.findViewById(R.id.about_message);
        message.setText(Html.fromHtml(res.getString(R.string.dialog_about_text)));
        //message.setText(R.string.dialog_about_text);
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
        builder.setView(view_dialog);
        return builder.create();
    }

}
