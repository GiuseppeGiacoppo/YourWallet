package com.giacsoft.yourwallet.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.giacsoft.yourwallet.R;
import com.giacsoft.yourwallet.Utils;
import com.giacsoft.yourwallet.activities.AccountDetailActivity;
import com.giacsoft.yourwallet.adapters.MyAccountAdapter;
import com.giacsoft.yourwallet.db.MyDatabase;
import com.giacsoft.yourwallet.types.Account;
import com.giacsoft.yourwallet.types.Transaction;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    RelativeLayout totale_conti;
    ListView accountsLV;
    MyDatabase db;
    Context ctx;
    TextView totalTV;
    SharedPreferences preferences;
    String cur;
    DecimalFormat df = new DecimalFormat("0.00");
    View actualViewSelected;
    long item_selezionato;
    MyAccountAdapter adapter;
    double totalAccountsAmount;
    ArrayList<Account> conti_array;
    private OnItemSelectedListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) activity;
        } else {
            throw new ClassCastException(activity.toString() + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_app, container, false);

        actualViewSelected = new View(getActivity());

        ctx = getActivity().getApplicationContext();
        db = new MyDatabase(ctx);
        db.open();

        accountsLV = (ListView) view.findViewById(R.id.lvconti);
        totale_conti = (RelativeLayout) view.findViewById(R.id.totale_conti);
        totalTV = (TextView) view.findViewById(R.id.tvtot);

        totale_conti.setOnClickListener(this);

        accountsLV.setDividerHeight(1);
        accountsLV.setOnItemClickListener(this);
        accountsLV.setOnItemLongClickListener(this);

        setSelected(totale_conti, 0);
        preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        cur = preferences.getString("currency", "$");
        aggiornaConti();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!cur.equals(preferences.getString("currency", "$"))) {
            cur = preferences.getString("currency", "$");
            aggiornaConti();
        }
    }

    public void aggiornaConti() {

        conti_array = db.getAccounts();
        totalAccountsAmount = db.getTotalAccountsAmount();
        totalTV.setText(df.format(totalAccountsAmount) + " " + cur);
        if (db.getTotalAccountsAmount() >= 0)
            totalTV.setTextColor(ContextCompat.getColor(ctx, R.color.positive));
        else
            totalTV.setTextColor(ContextCompat.getColor(ctx, R.color.negative));

        adapter = new MyAccountAdapter(ctx, R.layout.item_account, conti_array, cur);
        accountsLV.setAdapter(adapter);
        /*
		 * if (conti_array.size() == 0) addaccount.setVisibility(View.VISIBLE);
		 * else addaccount.setVisibility(View.GONE);
		 */
        // setSelezionato(totale_conti, 0);
    }

    @Override
    public void onClick(View v) {
        if (v == totale_conti) {
            setSelected(totale_conti, 0);
            listener.onAccountSelected(0);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id) {
        startActivity(new Intent(ctx, AccountDetailActivity.class).putExtra("ID", id));
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        setSelected(view, id);
        listener.onAccountSelected(id);
    }

    public void setSelected(View view, long id) {
        if (view == totale_conti) {
            //actualViewSelected.setBackgroundResource(R.drawable.selectable_background_yourwallet_greenab);
            //view.setBackgroundResource(R.drawable.spinner_ab_focused_yourwallet_greenab);
        } else {
            if (actualViewSelected == totale_conti) {
                //actualViewSelected.setBackgroundResource(R.drawable.spinner_background_ab_yourwallet_greenab);
            } else {
                //actualViewSelected.setBackgroundResource(R.drawable.selectable_background_yourwallet_greenab);
            }
            //view.setBackgroundResource(R.drawable.list_focused_yourwallet_greenab);
        }
        actualViewSelected = view;
        item_selezionato = id;
    }
    public void doAccount (int mode, Account c) {
        switch (mode) {
            case Utils.ADD:
                conti_array.add(c);
                adapter.notifyDataSetChanged();
                break;
        }
    }
    public void doTransaction(int mode, Transaction t, double diff, int p) {
        switch (mode) {
            case Utils.ADD:
                totalAccountsAmount += t.amount;
                totalTV.setText(df.format(totalAccountsAmount) + cur);

                for (int i = 0; i < accountsLV.getCount(); i++) {
                    if (adapter.getItem(i).id == t.accountID) {
                        Account cc = adapter.getItem(i);
                        cc.transactions_number++;
                        cc.total += t.amount;
                    }
                }
                adapter.notifyDataSetChanged();
                break;
            case Utils.EDIT:
                totalAccountsAmount += diff;
                totalTV.setText(df.format(totalAccountsAmount) + cur);

                for (int i = 0; i < accountsLV.getCount(); i++) {
                    if (adapter.getItem(i).id == t.accountID) {
                        Account cc = adapter.getItem(i);
                        cc.total += diff;
                    }
                }
                adapter.notifyDataSetChanged();
                break;
            case Utils.DELETE:
                totalAccountsAmount -= t.amount;
                totalTV.setText(df.format(totalAccountsAmount) + cur);

                for (int i = 0; i < accountsLV.getCount(); i++) {
                    if (adapter.getItem(i).id == t.accountID) {
                        Account cc = adapter.getItem(i);
                        cc.transactions_number--;
                        cc.total -= t.amount;
                    }
                }
                adapter.notifyDataSetChanged();
                break;
        }
    }
    public interface OnItemSelectedListener {
        void onAccountSelected(long id);
    }
}
