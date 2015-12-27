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
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.giacsoft.yourwallet.R;
import com.giacsoft.yourwallet.Utils;

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
        settings = getActivity().getSharedPreferences(Utils.RATING, 0);
        res = getActivity().getApplicationContext().getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.menu_about);

       /* LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view_dialog = inflater.inflate(R.layout.dialog_about, null);

        message = (TextView) view_dialog.findViewById(R.id.about_message);
        message.setText(Html.fromHtml(res.getString(R.string.dialog_about_text)));*/
        builder.setMessage(Html.fromHtml(res.getString(R.string.dialog_about_text)));
        builder.setPositiveButton(R.string.dialog_btn_rate, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("opzione", "1");
                editor.commit();

                Intent intentRate = new Intent(Intent.ACTION_VIEW);
                intentRate.setData(Uri.parse(Utils.APP_PLAYSTORE));
                startActivity(intentRate);
            }
        });
        builder.setNegativeButton(R.string.dialog_btn_contact, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intentMail = new Intent(Intent.ACTION_SEND);
                intentMail.setType("message/rfc822");
                intentMail.putExtra(Intent.EXTRA_EMAIL, new String[]{Utils.APP_MAIL});
                intentMail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intentMail, "Send Email"));
            }
        });
        //builder.setView(view_dialog);
        return builder.create();
    }

}
