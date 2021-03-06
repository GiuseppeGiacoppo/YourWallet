package com.giacsoft.yourwallet.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.giacsoft.yourwallet.R;
import com.giacsoft.yourwallet.Utils;
import com.giacsoft.yourwallet.adapters.MyTransactionAdapter;
import com.giacsoft.yourwallet.db.MyDatabase;
import com.giacsoft.yourwallet.dialogs.TransactionDialogFragment;
import com.giacsoft.yourwallet.types.Category;
import com.giacsoft.yourwallet.types.Transaction;

import java.util.ArrayList;
import java.util.Calendar;

public class AccountFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemSelectedListener, OnClickListener {

    private static final int DLG3 = 3;
    public long accountID;
    ListView transactionsLV;
    MyDatabase db;
    //int fromtab = 1; // se 0 proviene da tablet
    SharedPreferences preferences, edit_preferences;
    SharedPreferences.Editor preferences_editor;
    Spinner spinner_account_card_header;
    String cur;
    ArrayList<Transaction> mTransactions = new ArrayList<Transaction>();
    int mDay, mYear, mMonth, mese;
    double[] impmov, imptot;
    MyTransactionAdapter adapter;
    int selected_item_account_card_header;
    ArrayList<Category> mCategories;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Apro la connessione col database
        db = new MyDatabase(getActivity().getApplicationContext());
        db.open();

        // Prendi la valuta preferita, in caso non sia stata settata mostra l'euro

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        cur = preferences.getString("currency", "$");
        edit_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        preferences_editor = edit_preferences.edit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account_total, container, false);

        if (getActivity().getIntent().getExtras() != null) {
            accountID = getActivity().getIntent().getExtras().getLong(Utils.ACCOUNT_ID, 0);
        } else
            accountID = 0;

        // Data corrente
        final Calendar c = Calendar.getInstance();
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mYear = c.get(Calendar.YEAR);
        mese = c.get(Calendar.MONTH);
        mMonth = mese + 1;

        // Istanzia le viste
        transactionsLV = (ListView) view.findViewById(R.id.transactionsLV);
        spinner_account_card_header = (Spinner) view.findViewById(R.id.spinner_account_card_header);

        ArrayAdapter<CharSequence> adapter_account_card_header = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.account_filters_card_header, R.layout.item_spinner_elemento_card_header);
        adapter_account_card_header.setDropDownViewResource(R.layout.item_spinner_dropdown_card_header);
        spinner_account_card_header.setAdapter(adapter_account_card_header);

        transactionsLV.setOnItemClickListener(this);
        transactionsLV.setOnItemLongClickListener(this);
        spinner_account_card_header.setOnItemSelectedListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        selected_item_account_card_header = preferences.getInt("selected_item_account_card_header", 0);
        spinner_account_card_header.setSelection(selected_item_account_card_header);
    }

    public void updateTransactions(long id, int p) {
        switch (p) {
            case 0:
                    mTransactions = db.getLastTransactions(Utils.DESC, id, 20);
                break;
            case 1:
                    mTransactions = db.getTransactions(Utils.AMOUNT_ALL, true, id, mMonth, mYear);
                break;
            case 2:
                    mTransactions = db.getTransactions(Utils.AMOUNT_POSITIVE, false, id, mMonth, mYear);
                break;
            case 3:
                    mTransactions = db.getTransactions(Utils.AMOUNT_NEGATIVE, false, id, mMonth, mYear);
                break;
        }

        mCategories = db.getCategories();
        adapter = new MyTransactionAdapter(getActivity().getApplicationContext(), R.layout.item_transaction, mTransactions, cur, mCategories);
        transactionsLV.setAdapter(adapter);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showDialog(DLG3, id, position);
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity().getApplicationContext(), R.string.toast_hint_holdtoedit, Toast.LENGTH_SHORT).show();
    }

    void showDialog(int id, long transactionID, int position) {
        switch (id) {
            case DLG3:
                DialogFragment newFragment = TransactionDialogFragment.newInstance(accountID, transactionID, position);
                newFragment.show(getActivity().getFragmentManager(), "transazionedialog");
                break;
        }
    }

    public void doTransaction(int mode, Transaction t, int p) {
        switch (mode) {
            case Utils.ADD:
                updateTransactions(accountID, selected_item_account_card_header);
                break;
            case Utils.EDIT:
                mTransactions.set(p, t);
                adapter.notifyDataSetChanged();
                break;
            case Utils.DELETE:
                mTransactions.remove(p);
                adapter.notifyDataSetChanged();
                break;
            default:
                return;
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> arg0, View view, int i, long l) {
        preferences_editor.putInt("selected_item_account_card_header", i);
        preferences_editor.commit();
        updateTransactions(accountID, i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {}

    @Override
    public void onClick(View v) {}
}
