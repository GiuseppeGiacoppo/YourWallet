package com.giacsoft.yourwallet.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.giacsoft.yourwallet.R;
import com.giacsoft.yourwallet.Utils;
import com.giacsoft.yourwallet.db.MyDatabase;
import com.giacsoft.yourwallet.dialogs.AboutDialogFragment;
import com.giacsoft.yourwallet.dialogs.AccountDialogFragment;
import com.giacsoft.yourwallet.fragments.AccountFragment;
import com.giacsoft.yourwallet.fragments.GraphFragment;
import com.giacsoft.yourwallet.fragments.MainFragment;
import com.giacsoft.yourwallet.dialogs.TransactionDialogFragment;
import com.giacsoft.yourwallet.types.Account;
import com.giacsoft.yourwallet.types.Transaction;

public class MainActivity extends Activity implements MainFragment.OnItemSelectedListener, AccountDialogFragment.OnAccountDialogListener, TransactionDialogFragment.OnTransactionDialogListener {

    private static final int DLG1 = 1;
    private static final int DLG2 = 2;
    private static final int DLG3 = 3;
    MyDatabase db;
    long accountID;
    View V;
    long item_selezionato;
    MainFragment mainFragment;
    AccountFragment accountFragment;
    GraphFragment graphFragment;

    int selected_item_account_card_header, selected_item_graph_card_header;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        db = new MyDatabase(getApplicationContext());
        db.open();

        mainFragment = (MainFragment) getFragmentManager().findFragmentById(R.id.listfragment);
        accountFragment = (AccountFragment) getFragmentManager().findFragmentById(R.id.accountfragment);
        graphFragment = (GraphFragment) getFragmentManager().findFragmentById(R.id.graphfragment);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setLogo(R.drawable.ic_launcher);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle("prova");
        toolbar.inflateMenu(R.menu.menu_main_app);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_transfer:
                        startActivity(new Intent(getApplicationContext(), CategoriesActivity.class));
                        return true;
                    case R.id.menu_settings:
                        return true;
                    case R.id.menu_about:
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onAccountSelected(long id) {
        accountID = id;

        if (accountFragment != null && accountFragment.isInLayout()) {
            selected_item_account_card_header = preferences.getInt("selected_item_account_card_header", 0);
            selected_item_graph_card_header = preferences.getInt("selected_item_graph_card_header", 0);
            accountFragment.aggiornaTransazioni(id, selected_item_account_card_header);
            accountFragment.accountID = id;
        } else {
            startActivity(new Intent(getApplicationContext(), AccountActivity.class).putExtra(Utils.ACCOUNT_ID, id));
        }

        if (graphFragment != null && graphFragment.isInLayout()) {
            graphFragment.updateGraph(id, selected_item_graph_card_header);
            graphFragment.accountID = id;
        }
    }

    void mostraDialog(int id, long idc) {
        switch (id) {
            case DLG1:
                AccountDialogFragment cdf = AccountDialogFragment.newIstance();
                cdf.show(getFragmentManager(), "contodialog");
                break;
            case DLG2:
                DialogFragment newFragment = AboutDialogFragment.newInstance();
                newFragment.show(getFragmentManager(), "aboutdialog");
                break;
            case DLG3:
                DialogFragment tdf = TransactionDialogFragment.newInstance(idc, 0);
                tdf.show(getFragmentManager(), "transazionedialog");
                break;
        }
    }

    @Override
    public void doTransaction(int mode, Transaction t, double diff, int p) {
        switch (mode) {
            case Utils.ADD:
                if (mainFragment != null && mainFragment.isInLayout())
                    mainFragment.doTransaction(Utils.ADD, t,0,0);
                if (accountFragment != null && accountFragment.isInLayout())
                    if (accountID == t.accountID || accountID == 0)
                        accountFragment.doTransaction(Utils.ADD, t, 0);

                if (graphFragment != null && graphFragment.isInLayout())
                    if (accountID == t.accountID || accountID == 0) {
                        selected_item_graph_card_header = preferences.getInt("selected_item_graph_card_header", 0);
                        graphFragment.updateGraph(accountID, selected_item_graph_card_header);
                    }
                break;
            case Utils.EDIT:
                if (mainFragment != null && mainFragment.isInLayout())
                mainFragment.doTransaction(Utils.EDIT, t, diff, p);

                if (accountFragment != null && accountFragment.isInLayout())
                    if (accountID == t.accountID || accountID == 0)
                        accountFragment.doTransaction(Utils.EDIT, t, p);

                if (graphFragment != null && graphFragment.isInLayout())
                    if (accountID == t.accountID || accountID == 0) {
                        selected_item_graph_card_header = preferences.getInt("selected_item_graph_card_header", 0);
                        graphFragment.updateGraph(accountID, selected_item_graph_card_header);
                    }
                break;
            case Utils.DELETE:
                if (mainFragment != null && mainFragment.isInLayout())
                mainFragment.doTransaction(Utils.DELETE, t,0, p);

                if (accountFragment != null && accountFragment.isInLayout())
                    if (accountID == t.accountID || accountID == 0)
                        accountFragment.doTransaction(Utils.DELETE, t, p);

                if (graphFragment != null && graphFragment.isInLayout())
                    if (accountID == t.accountID || accountID == 0) {
                        selected_item_graph_card_header = preferences.getInt("selected_item_graph_card_header", 0);
                        graphFragment.updateGraph(accountID, selected_item_graph_card_header);
                    }
                break;
            default:
                return;
        }
    }

    @Override
    public void doAccount(int mode, Account c) {
        switch (mode) {
            case Utils.ADD:
                if (mainFragment != null && mainFragment.isInLayout())
                    mainFragment.doAccount(Utils.ADD,c);
                break;
            default:
                return;
        }
    }
}
