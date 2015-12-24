package com.giacsoft.yourwallet.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.giacsoft.yourwallet.R;
import com.giacsoft.yourwallet.Utils;
import com.giacsoft.yourwallet.db.MyDatabase;
import com.giacsoft.yourwallet.dialogs.TransactionDialogFragment;
import com.giacsoft.yourwallet.types.Transaction;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

//@SuppressLint("NewApi")
public class GraphFragment extends Fragment implements TransactionDialogFragment.OnTransactionDialogListener, AdapterView.OnItemSelectedListener {

    public long accountID;
    LineChart graph;
    MyDatabase db;
    SharedPreferences preferences, edit_preferences;
    SharedPreferences.Editor preferences_editor;
    String cur;
    Spinner spinner_graph_card_header;
    int selected_item_graph_card_header;

    int mYear, mMonth, mese;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        graph = (LineChart) view.findViewById(R.id.graph);
        graph.setDrawGridBackground(true);

        spinner_graph_card_header = (Spinner) view.findViewById(R.id.spinner_graph_card_header);

        ArrayAdapter<CharSequence> adapter_graph_card_header = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.graph_filters_card_header, R.layout.item_spinner_elemento_card_header);
        adapter_graph_card_header.setDropDownViewResource(R.layout.item_spinner_dropdown_card_header);
        spinner_graph_card_header.setAdapter(adapter_graph_card_header);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mese = c.get(Calendar.MONTH);
        mMonth = mese + 1;

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        edit_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        spinner_graph_card_header.setOnItemSelectedListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        db = new MyDatabase(getActivity().getApplicationContext());
        db.open();

        if (getActivity().getIntent().getExtras() != null) {
            accountID = getActivity().getIntent().getExtras().getLong(Utils.ACCOUNT_ID, 0);
        } else
            accountID = 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        cur = preferences.getString("currency", "$");

        selected_item_graph_card_header = preferences.getInt("selected_item_graph_card_header", 0);
        preferences_editor = edit_preferences.edit();

        spinner_graph_card_header.setSelection(selected_item_graph_card_header);
    }

    public void updateGraph(long id, int p) {
        // START CODICE GRAFICO
        ArrayList<Transaction> mesecorr = null;

        switch (p) {
            case 0:
                    mesecorr = db.getMovbyMonth(Utils.AMOUNT_ALL,false,id, mMonth, mYear);
                break;
            case 1:
                    mesecorr = db.getLastMov(Utils.ASC,id, 20);
                break;
            case 2:
                    mesecorr = db.getMovbyMonth(Utils.AMOUNT_POSITIVE, false, id, mMonth, mYear);
                break;
            case 3:
                    mesecorr = db.getMovbyMonth(Utils.AMOUNT_NEGATIVE,false,id, mMonth, mYear);
                break;
        } // prendo i movimenti

        ArrayList<Entry> vals = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();

        Iterator<Transaction> it = mesecorr.iterator();

        vals.add(new Entry(0, 0));
        xVals.add("zero");
        for(int k=1;it.hasNext();k++) {
            vals.add(new Entry((float) ((float) vals.get(vals.size()- 1).getVal() + it.next().amount),k));
            xVals.add("");
        }

        LineDataSet set = new LineDataSet(vals, "Prova");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.positive));
        set.setCircleColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.positive));
        set.setLineWidth(3f);
        set.setCircleSize(5f);
        set.setDrawCircleHole(false);


        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set);
        LineData data = new LineData(xVals, dataSets);
        graph.setData(data);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View view, int i, long l) {
        preferences_editor.putInt("selected_item_graph_card_header", i);
        preferences_editor.commit();
        updateGraph(accountID, i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override
    public void doTransaction(int mode, Transaction t, double diff, int p) {
        switch (mode) {
            case Utils.ADD:
                updateGraph(t.accountID, selected_item_graph_card_header);
                break;
            case Utils.EDIT:
                updateGraph(t.accountID, selected_item_graph_card_header);
                break;
            case Utils.DELETE:
                updateGraph(t.accountID, selected_item_graph_card_header);
                break;
        }
    }
}
