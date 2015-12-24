package com.giacsoft.yourwallet.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.giacsoft.yourwallet.R;
import com.giacsoft.yourwallet.Utils;
import com.giacsoft.yourwallet.adapters.MyAccountTitleAdapter;
import com.giacsoft.yourwallet.adapters.MyCategoryAdapter;
import com.giacsoft.yourwallet.db.MyDatabase;
import com.giacsoft.yourwallet.types.Account;
import com.giacsoft.yourwallet.types.Category;
import com.giacsoft.yourwallet.types.Transaction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TransactionDialogFragment extends DialogFragment implements View.OnClickListener {

    // int provenienza; // serve per capire se � stato richiamato da
    // mainfragmactivity(0) o accountfragmactivity(1) o
    // viewcontoactivity(2)
    static int posizione;
    Resources res;
    Context ctx;
    MyDatabase db;
    EditText etnome, etimp;
    Spinner spincat, spinconti;
    Button data, cancel_btn, add_btn;
    public DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            cYear = year;
            cMonth = monthOfYear;
            cDay = dayOfMonth;
            String txt = String.format(res.getString(R.string.txt_transaction_date_format), res.getStringArray(R.array.months_filter_spinner)[cMonth + 1], cDay, cYear);
            data.setText(txt);
        }
    };
    int mYear, mMonth, mDay, cYear, cMonth, cDay, poscat;
    long idtrans, idconto, id_nuova_trans;
    boolean doubleBackToExitPressedOnce;
    Transaction t;
    View vert_sep;
    LinearLayout layoutconti;
    OnTransazioneDialogListener tdlistener;

    public static TransactionDialogFragment newInstance(long idc, long idt) {
        TransactionDialogFragment frag = new TransactionDialogFragment();
        Bundle b = new Bundle();
        b.putLong("idc", idc);
        b.putLong("idt", idt);
        // b.putInt("activity", activity);
        frag.setArguments(b);
        return frag;
    }

    // utilizzata per editare, p indica la posizione della transizione al
    // momento del longpress
    public static TransactionDialogFragment newInstance(long idc, long idt, int p) {
        TransactionDialogFragment frag = new TransactionDialogFragment();
        posizione = p;
        Bundle b = new Bundle();
        b.putLong("idc", idc);
        b.putLong("idt", idt);
        // b.putInt("activity", activity);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnTransazioneDialogListener) {
            tdlistener = (OnTransazioneDialogListener) activity;
        } else {
            throw new ClassCastException(activity.toString() + " must implement TransazioneDialogFragment.OnTransazioneDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        idtrans = getArguments().getLong("idt");
        idconto = getArguments().getLong("idc");
        // provenienza = getArguments().getInt("activity");

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        res = getActivity().getResources();
        ctx = getActivity().getApplicationContext();
        db = new MyDatabase(ctx);
        db.open();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view_dialog = inflater.inflate(R.layout.dialog_add_transazione, null);

        etnome = (EditText) view_dialog.findViewById(R.id.ettra);
        etimp = (EditText) view_dialog.findViewById(R.id.etimp);
        spincat = (Spinner) view_dialog.findViewById(R.id.spincat);
        data = (Button) view_dialog.findViewById(R.id.date);
        spinconti = (Spinner) view_dialog.findViewById(R.id.spinconti);
        layoutconti = (LinearLayout) view_dialog.findViewById(R.id.layoutconti);
        add_btn = (Button) view_dialog.findViewById(R.id.add_btn);
        cancel_btn = (Button) view_dialog.findViewById(R.id.cancel_btn);

        doubleBackToExitPressedOnce = false;

        aggiornaCategorie();
        ArrayList<Account> arrayconti = db.getAccounts();
        MyAccountTitleAdapter adconti = new MyAccountTitleAdapter(getActivity(), R.layout.item_spinner1line_spinner, arrayconti);
        adconti.setDropDownViewResource(R.layout.item_spinner1line);
        spinconti.setAdapter(adconti);

        if (idconto != 0) {
            layoutconti.setVisibility(View.GONE);
        }

        if (idtrans != 0) {
            t = db.getTransaction(idtrans);
            layoutconti.setVisibility(View.GONE);
            etnome.setText(t.note);
            etimp.setText(t.amount + "");
            String txt = String.format(res.getString(R.string.txt_transaction_date_format), res.getStringArray(R.array.months_filter_spinner)[t.month], t.day, t.year);
            data.setText(txt);
            poscat = (int) t.category;
            spincat.setSelection(poscat - 1);
            cancel_btn.setText(res.getString(R.string.delete));
            cDay = t.day;
            cMonth = t.month - 1;
            cYear = t.year;
            add_btn.setText(R.string.edit);
        } else {
            String txt = String.format(res.getString(R.string.txt_transaction_date_format), res.getStringArray(R.array.months_filter_spinner)[mMonth + 1], mDay, mYear);
            data.setText(txt);
            cDay = mDay;
            cMonth = mMonth;
            cYear = mYear;
        }

        data.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
        add_btn.setOnClickListener(this);

        builder.setView(view_dialog);
        return builder.create();
    }

    public void aggiornaCategorie() {
        ArrayList<Category> arraycat = db.getCategories();
        MyCategoryAdapter adcat2 = new MyCategoryAdapter(getActivity(), R.layout.item_categoria_spinner, arraycat);
        adcat2.setDropDownViewResource(R.layout.item_categoria);
        spincat.setAdapter(adcat2);
    }

    @Override
    public void onClick(View v) {

        if (v == data) {
            if (idtrans == 0)
                new DatePickerDialog(getActivity(), mDateSetListener, mYear, mMonth, mDay).show();
            else
                new DatePickerDialog(getActivity(), mDateSetListener, t.year, t.month - 1, t.day).show();
        } else if (v == cancel_btn) {
            if (idtrans != 0) {
                if (doubleBackToExitPressedOnce) {
                    Transaction del = db.getTransaction(idtrans);
                    db.deleteTransaction(idtrans);
                    Toast.makeText(ctx, R.string.toast_successful_transaction_delete, Toast.LENGTH_SHORT).show();

                    //tdlistener.onDeleteTransazione(del, posizione);
                    tdlistener.doTransaction(Utils.DELETE, del, 0,posizione);

                    dismiss();
                    return;
                }

                doubleBackToExitPressedOnce = true;
                Toast.makeText(ctx, R.string.toast_confirm_deletetransaction, Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 3000);
            } else {
                dismiss();
            }
        } else if (v == add_btn) {
            if (etnome.length() != 0 && etimp.length() != 0) {
                int mese = cMonth + 1;
                Calendar cal = new GregorianCalendar(cYear, mese, cDay);
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1=Domenica,
                // 2=Lunedi
                // ecc
                if (spinconti.getCount() < 1) {
                    Toast.makeText(ctx, R.string.toast_alert_noaccount, Toast.LENGTH_SHORT).show();
                } else {
                    if (spincat.getCount() < 1) {
                        Toast.makeText(ctx, R.string.toast_alert_nocategory, Toast.LENGTH_SHORT).show();
                    } else {
                        if (idtrans != 0) {
                            t = db.getTransaction(idtrans);
                            double differenza = Double.parseDouble(etimp.getText().toString()) - t.amount;
                            db.editTransaction(idtrans, etnome.getText().toString(), differenza, spincat.getSelectedItemId(), t.accountID, dayOfWeek, cDay, mese, cYear);
                            //tdlistener.onEditTransazione(new Transaction(idtrans, etnome.getText().toString(), t.amount + differenza, spincat.getSelectedItemId(), t.accountID, dayOfWeek, cDay, mese, cYear), differenza, posizione);
                            tdlistener.doTransaction(Utils.EDIT, new Transaction(idtrans, etnome.getText().toString(), t.amount + differenza, spincat.getSelectedItemId(), t.accountID, dayOfWeek, cDay, mese, cYear), differenza, posizione);
                            Toast.makeText(ctx, R.string.toast_successful_transaction_edit, Toast.LENGTH_SHORT).show();

                        } else {
                            if (idconto == 0) {
                                id_nuova_trans = db.newTransaction(etnome.getText().toString(), Double.parseDouble(etimp.getText().toString()), spincat.getSelectedItemId(), spinconti.getSelectedItemId(), dayOfWeek, cDay, mese, cYear);
                                //tdlistener.onAddTransazione(new Transaction(id_nuova_trans, etnome.getText().toString(), Double.parseDouble(etimp.getText().toString()), spincat.getSelectedItemId(), spinconti.getSelectedItemId(), dayOfWeek, cDay, mese, cYear));
                                tdlistener.doTransaction(Utils.ADD, new Transaction(id_nuova_trans, etnome.getText().toString(), Double.parseDouble(etimp.getText().toString()), spincat.getSelectedItemId(), spinconti.getSelectedItemId(), dayOfWeek, cDay, mese, cYear), 0,0);
                            } else {
                                id_nuova_trans = db.newTransaction(etnome.getText().toString(), Double.parseDouble(etimp.getText().toString()), spincat.getSelectedItemId(), idconto, dayOfWeek, cDay, mese, cYear);
                                //tdlistener.onAddTransazione(new Transaction(id_nuova_trans, etnome.getText().toString(), Double.parseDouble(etimp.getText().toString()), spincat.getSelectedItemId(), idconto, dayOfWeek, cDay, mese, cYear));
                                tdlistener.doTransaction(Utils.ADD, new Transaction(id_nuova_trans, etnome.getText().toString(), Double.parseDouble(etimp.getText().toString()), spincat.getSelectedItemId(), idconto, dayOfWeek, cDay, mese, cYear),0,0);
                            }
                            Toast.makeText(ctx, R.string.toast_successful_transaction_add, Toast.LENGTH_SHORT).show();
                        }
                        dismiss();
                    }
                }
            } else {
                Toast.makeText(ctx, R.string.toast_error_completefields, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface OnTransazioneDialogListener {
        void doTransaction(int mode, Transaction t, double diff, int p);
        //p posizione da eliminare e sostituire se EDIT, da eliminare se DELETE
    }
}
