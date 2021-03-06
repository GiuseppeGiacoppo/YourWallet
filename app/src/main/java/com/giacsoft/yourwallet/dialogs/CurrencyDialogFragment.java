package com.giacsoft.yourwallet.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.giacsoft.yourwallet.R;

public class CurrencyDialogFragment extends DialogFragment implements View.OnClickListener {

    Button euro, dollar, sterling, yen, swekr, dankr, czkr, won;
    EditText currencyET;
    SharedPreferences settings;
    SharedPreferences.Editor curr;

    public static CurrencyDialogFragment newInstance() {
        CurrencyDialogFragment frag = new CurrencyDialogFragment();
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view_dialog = inflater.inflate(R.layout.dialog_currency_selector, null);

        settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        curr = settings.edit();

        currencyET = (EditText) view_dialog.findViewById(R.id.personalcur);
        euro = (Button) view_dialog.findViewById(R.id.euro);
        dollar = (Button) view_dialog.findViewById(R.id.dollar);
        sterling = (Button) view_dialog.findViewById(R.id.sterling);
        yen = (Button) view_dialog.findViewById(R.id.yen);
        swekr = (Button) view_dialog.findViewById(R.id.swekr);
        dankr = (Button) view_dialog.findViewById(R.id.dankr);
        czkr = (Button) view_dialog.findViewById(R.id.czkr);
        won = (Button) view_dialog.findViewById(R.id.won);

        euro.setOnClickListener(this);
        dollar.setOnClickListener(this);
        sterling.setOnClickListener(this);
        yen.setOnClickListener(this);
        swekr.setOnClickListener(this);
        dankr.setOnClickListener(this);
        czkr.setOnClickListener(this);
        won.setOnClickListener(this);

        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (currencyET.length() > 0) {
                    curr.putString("currency", currencyET.getText().toString());
                    curr.commit();
                } else {
                    Toast.makeText(getActivity(), R.string.currency_empty, Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        builder.setView(view_dialog);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        if (v == euro) {
            curr.putString("currency", "€");
            curr.commit();
        } else if (v == dollar) {
            curr.putString("currency", "$");
            curr.commit();
        } else if (v == sterling) {
            curr.putString("currency", "£");
            curr.commit();
        } else if (v == yen) {
            curr.putString("currency", "¥");
            curr.commit();
        } else if (v == swekr) {
            curr.putString("currency", "kr");
            curr.commit();
        } else if (v == dankr) {
            curr.putString("currency", "kr");
            curr.commit();
        } else if (v == czkr) {
            curr.putString("currency", "Kč");
            curr.commit();
        } else if (v == won) {
            curr.putString("currency", "₩");
            curr.commit();
        }
        dismiss();
    }

}
