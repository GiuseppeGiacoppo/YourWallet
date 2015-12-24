package com.giacsoft.yourwallet.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.giacsoft.yourwallet.R;
import com.giacsoft.yourwallet.Utils;
import com.giacsoft.yourwallet.db.MyDatabase;
import com.giacsoft.yourwallet.types.Account;

public class AccountDialogFragment extends DialogFragment implements OnClickListener {

    MyDatabase db;
    Context ctx;
    long idconto;
    Button add_btn, cancel_btn;
    EditText inputconto;
    OnContoDialogListener cdlistener;

    public static AccountDialogFragment newIstance() {
        AccountDialogFragment frag = new AccountDialogFragment();
        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnContoDialogListener) {
            cdlistener = (OnContoDialogListener) activity;
        } else {
            throw new ClassCastException(activity.toString() + " must implement ContoDialogFragment.OnContoDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ctx = getActivity().getApplicationContext();

        db = new MyDatabase(ctx);
        db.open();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view_dialog = inflater.inflate(R.layout.dialog_add_conto, null);

        inputconto = (EditText) view_dialog.findViewById(R.id.etnome);
        add_btn = (Button) view_dialog.findViewById(R.id.addc_btn);
        cancel_btn = (Button) view_dialog.findViewById(R.id.cancel_btn);

        add_btn.setOnClickListener(this);

        builder.setView(view_dialog);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        if (v == add_btn) {
            if (inputconto.length() > 0) {
                idconto = db.addAccount(inputconto.getText().toString());
                Toast.makeText(ctx, R.string.toast_successful_account_add, Toast.LENGTH_SHORT).show();
                cdlistener.doAccount(Utils.ADD, new Account(idconto, inputconto.getText().toString(), 0));
                dismiss();
            } else {
                Toast.makeText(ctx, R.string.toast_alert_invalid_name, Toast.LENGTH_SHORT).show();
            }
        } else if (v == cancel_btn) {
            dismiss();
        }
    }

    public interface OnContoDialogListener {
        void doAccount(int mode, Account c);
    }
}
