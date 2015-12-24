package com.giacsoft.yourwallet.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.giacsoft.yourwallet.R;
import com.giacsoft.yourwallet.db.MyDatabase;

public class AccountDetailActivity extends Activity {

    EditText nameET;
    MyDatabase db;
    ImageView deleteIV;
    long accountID;
    ActionBar actionBar;
    CharSequence[] account_actions = {"Tutto", "Solo Transazioni"};
    int radio_selected = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_conto);

        LayoutInflater inflater = (LayoutInflater) getActionBar().getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customActionBarView = inflater.inflate(R.layout.actionbar_custom_view_done_discard, null);

        actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(customActionBarView, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        nameET = (EditText) findViewById(R.id.etnome);
        deleteIV = (ImageView) findViewById(R.id.tab_delete);
        accountID = getIntent().getExtras().getLong("ID");

        customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // "Done"
                if (nameET.length() != 0) {
                    db.open();
                    db.updateAccount(accountID, nameET.getText().toString());
                    Toast.makeText(getApplicationContext(), R.string.toast_successful_account_edit, Toast.LENGTH_SHORT).show();
                    db.close();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.toast_alert_invalid_name, Toast.LENGTH_SHORT).show();
                }
            }
        });

        customActionBarView.findViewById(R.id.actionbar_discard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // "Discard"
                onBackPressed();
            }
        });
        db = new MyDatabase(getApplicationContext());
        db.open();
        nameET.setText(db.getAccount(accountID).name);
        db.close();

        deleteIV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder alt_bld = new AlertDialog.Builder(AccountDetailActivity.this);
                alt_bld.setSingleChoiceItems(account_actions, -1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        radio_selected = which;
                    }
                });
                alt_bld.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switch (radio_selected) {
                            case 0:
                                db.open();
                                db.deleteAccount(accountID);
                                db.close();
                                Toast.makeText(getApplicationContext(), R.string.toast_successful_account_delete, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                break;
                            case 1:
                                db.open();
                                db.cleanAccount(accountID);
                                db.close();

                                Toast.makeText(getApplicationContext(), R.string.toast_successful_deleted_transactions, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                break;
                            default:
                                break;
                        }
                        dialog.cancel();
                    }
                });
                alt_bld.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = alt_bld.create();
                alert.setTitle(R.string.alert_confirm_deleteaccount);
                alert.show();
            }
        });
    }
}
