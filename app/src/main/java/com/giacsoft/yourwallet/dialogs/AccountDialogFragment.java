package com.giacsoft.yourwallet.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
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

public class AccountDialogFragment extends DialogFragment {

    MyDatabase db;
    Context ctx;
    long accountID;
    EditText accountET;
    OnAccountDialogListener cdlistener;

    public static AccountDialogFragment newIstance() {
        AccountDialogFragment frag = new AccountDialogFragment();
        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnAccountDialogListener) {
            cdlistener = (OnAccountDialogListener) activity;
        } else {
            throw new ClassCastException(activity.toString() + " must implement AccountDialogFragment.OnAccountDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ctx = getActivity().getApplicationContext();

        db = new MyDatabase(ctx);
        db.open();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.action_addaccount);

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view_dialog = inflater.inflate(R.layout.dialog_add_account, null);

        accountET = (EditText) view_dialog.findViewById(R.id.etnome);

        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (accountET.length() > 0) {
                    accountID = db.addAccount(accountET.getText().toString());
                    Toast.makeText(ctx, R.string.toast_successful_account_add, Toast.LENGTH_SHORT).show();
                    cdlistener.doAccount(Utils.ADD, new Account(accountID, accountET.getText().toString(), 0));
                    dismiss();
                } else {
                    Toast.makeText(ctx, R.string.toast_alert_invalid_name, Toast.LENGTH_SHORT).show();
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

    public interface OnAccountDialogListener {
        void doAccount(int mode, Account c);
    }
}
