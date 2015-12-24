package com.giacsoft.yourwallet.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.giacsoft.yourwallet.R;
import com.giacsoft.yourwallet.Utils;
import com.giacsoft.yourwallet.adapters.MyTransactionAdapter;
import com.giacsoft.yourwallet.db.MyDatabase;
import com.giacsoft.yourwallet.dialogs.TransactionDialogFragment;
import com.giacsoft.yourwallet.types.Account;
import com.giacsoft.yourwallet.types.Category;
import com.giacsoft.yourwallet.types.Transaction;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AccountFiltersActivity extends Activity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, TransactionDialogFragment.OnTransazioneDialogListener {

    static final int DLG3 = 3;
    ListView listtr;
    Account c;
    MyDatabase db;
    int mYear, mMonth, mDay;
    long idconto;
    int provenienza;
    TextView tvtot;
    Spinner filtro, filtroanno;
    SharedPreferences preferences;
    String cur;
    ActionBar actionBar;
    MyTransactionAdapter adapter;
    ArrayList<Transaction> nuove_transazioni;
    DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_view_conto);

        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = new MyDatabase(getApplicationContext());
        db.open();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        cur = preferences.getString("currency", "$");

        listtr = (ListView) findViewById(R.id.listtr);
        tvtot = (TextView) findViewById(R.id.tvtot);
        filtro = (Spinner) findViewById(R.id.spinfiltro);
        filtroanno = (Spinner) findViewById(R.id.spinfiltroanno);

        idconto = getIntent().getExtras().getLong(Utils.ACCOUNT_ID);
        provenienza = getIntent().getExtras().getInt(Utils.FILTRO_PREV_ACTIVITY);

        c = db.getAccount(idconto);
        actionBar.setTitle(c.name);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        ArrayAdapter<CharSequence> spinfiltro = ArrayAdapter.createFromResource(getApplicationContext(), R.array.months_filter_spinner, R.layout.item_spinner_elemento);
        filtro.setAdapter(spinfiltro);

        ArrayAdapter<CharSequence> spinfiltroanno = ArrayAdapter.createFromResource(getApplicationContext(), R.array.years_filter_spinner, R.layout.item_spinner_elemento);
        filtroanno.setAdapter(spinfiltroanno);

        listtr.setDividerHeight(1);
        listtr.setOnItemClickListener(this);
        listtr.setOnItemLongClickListener(this);

        filtro.setSelection(mMonth + 1);
        filtro.setOnItemSelectedListener(this);

        filtroanno.setSelection(1);
        filtroanno.setOnItemSelectedListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        aggiornaTransazioni(mMonth + 1, 0);

    }

    public void aggiornaTransazioni(int mese, int anno) {
        new AggiornaTransazioniSyncTask().execute(new ItemSelezionati(mese, anno));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_view_conto, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home

                if (provenienza == 0) { // is phone
                    startActivity(new Intent(this, AccountActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(Utils.ACCOUNT_ID, idconto));
                } else if (provenienza == 1) {// is tablet
                    startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View view, int i, long l) {
        if (arg0.getId() == R.id.spinfiltro) {
            aggiornaTransazioni(i, filtroanno.getSelectedItemPosition());
        } else if (arg0.getId() == R.id.spinfiltroanno) {
            aggiornaTransazioni(filtro.getSelectedItemPosition(), i);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id) {
        mostraDialog(DLG3, id);
        return false;
    }

    void mostraDialog(int id, long idtra) {
        switch (id) {
            case DLG3:
                DialogFragment newFragment = TransactionDialogFragment.newInstance(idconto, idtra);
                newFragment.show(getFragmentManager(), "transazionedialog");
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Toast.makeText(getApplicationContext(), R.string.toast_hint_holdtoedit, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (provenienza == 0) { // is phone
            startActivity(new Intent(this, AccountActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(Utils.ACCOUNT_ID, idconto));
        } else if (provenienza == 1) {// is tablet
            startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }

    @Override
    public void doTransaction(int mode, Transaction t, double diff, int p) {
        switch (mode){
            case Utils.EDIT:
                nuove_transazioni.set(p, t);
                adapter.notifyDataSetChanged();
                break;
            case Utils.DELETE:
                nuove_transazioni.remove(p);
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private static class ItemSelezionati {
        int filtro;
        int filtroanno;

        public ItemSelezionati(int f, int fa) {
            this.filtro = f;
            this.filtroanno = fa;
        }
    }

    private class AggiornaTransazioniSyncTask extends AsyncTask<ItemSelezionati, Void, Void> {

        ArrayList<Category> list_cat;
        double totale;

        protected Void doInBackground(ItemSelezionati... arg0) {

            nuove_transazioni = db.getTransazionibyFiltroMY(idconto, arg0[0].filtro, arg0[0].filtroanno);
            list_cat = db.getCategories();
            totale = db.getTotaleTransazionibyFiltroMY(idconto, arg0[0].filtro, arg0[0].filtroanno);
            return null;
        }

        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            adapter = new MyTransactionAdapter(getApplicationContext(), R.layout.item_transaction, nuove_transazioni, cur, list_cat);
            listtr.setAdapter(adapter);

            tvtot.setText(df.format(totale) + " " + cur);
            if (totale >= 0)
                tvtot.setTextColor(getResources().getColor(R.color.positive));
            else
                tvtot.setTextColor(getResources().getColor(R.color.negative));
        }
    }
}
