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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.giacsoft.yourwallet.R;

public class PinDialogFragment extends DialogFragment  {

    EditText inputET;
    SharedPreferences settings;
    SharedPreferences.Editor pinn;
    SharedPreferences preferences;

    public static PinDialogFragment newInstance() {
        PinDialogFragment frag = new PinDialogFragment();
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.preferences_pin_title);

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view_dialog = inflater.inflate(R.layout.dialog_pin, null);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        pinn = settings.edit();

        inputET = (EditText) view_dialog.findViewById(R.id.etpin);

        String pin = preferences.getString("pin", "");
        if (pin.length() == 0)
            ;
        else
            inputET.setText(pin);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pinn.putString("pin", inputET.getText().toString());
                pinn.apply();
                dismiss();
            }
        });
        builder.setView(view_dialog);
        return builder.create();
    }
}
