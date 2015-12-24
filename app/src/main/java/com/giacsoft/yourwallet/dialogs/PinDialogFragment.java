package com.giacsoft.yourwallet.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.giacsoft.yourwallet.R;

public class PinDialogFragment extends DialogFragment implements OnClickListener {

    Button add_btn;
    EditText inputpin;
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

        inputpin = (EditText) view_dialog.findViewById(R.id.etpin);

        String pin = preferences.getString("pin", "");
        if (pin.length() == 0)
            ;
        else
            inputpin.setText(pin);
        add_btn = (Button) view_dialog.findViewById(R.id.addc_btn);

        add_btn.setOnClickListener(this);

        builder.setView(view_dialog);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        if (v == add_btn) {
            pinn.putString("pin", inputpin.getText().toString());
            pinn.commit();
            dismiss();
        }
    }
}
