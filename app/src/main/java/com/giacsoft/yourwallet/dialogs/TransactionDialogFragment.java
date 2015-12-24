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
    static int position;
    Resources res;
    Context ctx;
    MyDatabase db;
    EditText nameET, amountET;
    Spinner categoriesSP, accountsSP;
    Button dateBTN, cancelBTN, addBTN;
    public DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            cYear = year;
            cMonth = monthOfYear;
            cDay = dayOfMonth;
            String txt = String.format(res.getString(R.string.txt_transaction_date_format), res.getStringArray(R.array.months_filter_spinner)[cMonth + 1], cDay, cYear);
            dateBTN.setText(txt);
        }
    };
    int mYear, mMonth, mDay, cYear, cMonth, cDay, categoryPosition;
    long transactionID, accountID, newTransactionID;
    boolean doubleBackToExitPressedOnce;
    Transaction t;
    View vert_sep;
    LinearLayout accountsLayout;
    OnTransactionDialogListener tdlistener;

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
        position = p;
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
        if (activity instanceof OnTransactionDialogListener) {
            tdlistener = (OnTransactionDialogListener) activity;
        } else {
            throw new ClassCastException(activity.toString() + " must implement TransactionDialogFragment.OnTransactionDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        transactionID = getArguments().getLong("idt");
        accountID = getArguments().getLong("idc");
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
        View view_dialog = inflater.inflate(R.layout.dialog_add_transaction, null);

        nameET = (EditText) view_dialog.findViewById(R.id.ettra);
        amountET = (EditText) view_dialog.findViewById(R.id.etimp);
        categoriesSP = (Spinner) view_dialog.findViewById(R.id.spincat);
        dateBTN = (Button) view_dialog.findViewById(R.id.date);
        accountsSP = (Spinner) view_dialog.findViewById(R.id.spinconti);
        accountsLayout = (LinearLayout) view_dialog.findViewById(R.id.layoutconti);
        addBTN = (Button) view_dialog.findViewById(R.id.add_btn);
        cancelBTN = (Button) view_dialog.findViewById(R.id.cancel_btn);

        doubleBackToExitPressedOnce = false;

        updateCategories();
        ArrayList<Account> mAccounts = db.getAccounts();
        MyAccountTitleAdapter accountsAdapter = new MyAccountTitleAdapter(getActivity(), R.layout.item_1line_spinner, mAccounts);
        accountsAdapter.setDropDownViewResource(R.layout.item_1line_spinner);
        accountsSP.setAdapter(accountsAdapter);

        if (accountID != 0) {
            accountsLayout.setVisibility(View.GONE);
        }

        if (transactionID != 0) {
            t = db.getTransaction(transactionID);
            accountsLayout.setVisibility(View.GONE);
            nameET.setText(t.note);
            amountET.setText(t.amount + "");
            String txt = String.format(res.getString(R.string.txt_transaction_date_format), res.getStringArray(R.array.months_filter_spinner)[t.month], t.day, t.year);
            dateBTN.setText(txt);
            categoryPosition = (int) t.category;
            categoriesSP.setSelection(categoryPosition - 1);
            cancelBTN.setText(res.getString(R.string.delete));
            cDay = t.day;
            cMonth = t.month - 1;
            cYear = t.year;
            addBTN.setText(R.string.edit);
        } else {
            String txt = String.format(res.getString(R.string.txt_transaction_date_format), res.getStringArray(R.array.months_filter_spinner)[mMonth + 1], mDay, mYear);
            dateBTN.setText(txt);
            cDay = mDay;
            cMonth = mMonth;
            cYear = mYear;
        }

        dateBTN.setOnClickListener(this);
        cancelBTN.setOnClickListener(this);
        addBTN.setOnClickListener(this);

        builder.setView(view_dialog);
        return builder.create();
    }

    public void updateCategories() {
        ArrayList<Category> arraycat = db.getCategories();
        MyCategoryAdapter adcat2 = new MyCategoryAdapter(getActivity(), R.layout.item_category_spinner, arraycat);
        //adcat2.setDropDownViewResource(R.layout.item_category);
        adcat2.setDropDownViewResource(R.layout.item_category_spinner);
        categoriesSP.setAdapter(adcat2);
    }

    @Override
    public void onClick(View v) {

        if (v == dateBTN) {
            if (transactionID == 0)
                new DatePickerDialog(getActivity(), mDateSetListener, mYear, mMonth, mDay).show();
            else
                new DatePickerDialog(getActivity(), mDateSetListener, t.year, t.month - 1, t.day).show();
        } else if (v == cancelBTN) {
            if (transactionID != 0) {
                if (doubleBackToExitPressedOnce) {
                    Transaction del = db.getTransaction(transactionID);
                    db.deleteTransaction(transactionID);
                    Toast.makeText(ctx, R.string.toast_successful_transaction_delete, Toast.LENGTH_SHORT).show();

                    tdlistener.doTransaction(Utils.DELETE, del, 0,position);

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
        } else if (v == addBTN) {
            if (nameET.length() != 0 && amountET.length() != 0) {
                int mese = cMonth + 1;
                Calendar cal = new GregorianCalendar(cYear, mese, cDay);
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1=Domenica,
                // 2=Lunedi
                // ecc
                if (accountsSP.getCount() < 1) {
                    Toast.makeText(ctx, R.string.toast_alert_noaccount, Toast.LENGTH_SHORT).show();
                } else {
                    if (categoriesSP.getCount() < 1) {
                        Toast.makeText(ctx, R.string.toast_alert_nocategory, Toast.LENGTH_SHORT).show();
                    } else {
                        if (transactionID != 0) {
                            t = db.getTransaction(transactionID);
                            double differenza = Double.parseDouble(amountET.getText().toString()) - t.amount;
                            db.editTransaction(transactionID, nameET.getText().toString(), differenza, categoriesSP.getSelectedItemId(), t.accountID, dayOfWeek, cDay, mese, cYear);
                            tdlistener.doTransaction(Utils.EDIT, new Transaction(transactionID, nameET.getText().toString(), t.amount + differenza, categoriesSP.getSelectedItemId(), t.accountID, dayOfWeek, cDay, mese, cYear), differenza, position);
                            Toast.makeText(ctx, R.string.toast_successful_transaction_edit, Toast.LENGTH_SHORT).show();

                        } else {
                            if (accountID == 0) {
                                newTransactionID = db.addTransaction(nameET.getText().toString(), Double.parseDouble(amountET.getText().toString()), categoriesSP.getSelectedItemId(), accountsSP.getSelectedItemId(), dayOfWeek, cDay, mese, cYear);
                                tdlistener.doTransaction(Utils.ADD, new Transaction(newTransactionID, nameET.getText().toString(), Double.parseDouble(amountET.getText().toString()), categoriesSP.getSelectedItemId(), accountsSP.getSelectedItemId(), dayOfWeek, cDay, mese, cYear), 0,0);
                            } else {
                                newTransactionID = db.addTransaction(nameET.getText().toString(), Double.parseDouble(amountET.getText().toString()), categoriesSP.getSelectedItemId(), accountID, dayOfWeek, cDay, mese, cYear);
                                tdlistener.doTransaction(Utils.ADD, new Transaction(newTransactionID, nameET.getText().toString(), Double.parseDouble(amountET.getText().toString()), categoriesSP.getSelectedItemId(), accountID, dayOfWeek, cDay, mese, cYear),0,0);
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

    public interface OnTransactionDialogListener {
        void doTransaction(int mode, Transaction t, double diff, int p);
        //p posizione da eliminare e sostituire se EDIT, da eliminare se DELETE
    }
}
