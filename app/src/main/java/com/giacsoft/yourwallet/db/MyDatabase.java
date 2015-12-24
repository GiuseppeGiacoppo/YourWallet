package com.giacsoft.yourwallet.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.giacsoft.yourwallet.Utils;
import com.giacsoft.yourwallet.types.Account;
import com.giacsoft.yourwallet.types.Category;
import com.giacsoft.yourwallet.types.Transaction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MyDatabase {

    private static final String DB_NAME = "yourwallet";
    private static final int DB_VERSION = 2;
    private static final String TRANSAZIONI_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TransactionsTable.TABLE_NAME + " ( " + TransactionsTable._ID + " integer primary key autoincrement, " + TransactionsTable.NOTE + " text not null, "
            + TransactionsTable.AMOUNT + " double not null, " + TransactionsTable.CATEGORY + " integer not null, " + TransactionsTable.ACCOUNT + " integer not null, " + TransactionsTable.DAYOFWEEK + " integer not null, " + TransactionsTable.DAY
            + " integer not null, " + TransactionsTable.MONTH + " integer not null, " + TransactionsTable.YEAR + " integer not null );";
    private static final String CONTI_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + AccountsTable.TABLE_NAME + " ( " + AccountsTable._ID + " integer primary key autoincrement, " + AccountsTable.NAME + " text not null, " + AccountsTable.TOTAL
            + " double not null );";
    private static final String CATEGORIA_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + CategoriesTable.TABLE_NAME + " ( " + CategoriesTable._ID + " integer primary key autoincrement, " + CategoriesTable.NAME + " text not null, "
            + CategoriesTable.COLOR + " text not null default 'color0' );";

	/*
     * Versione 1: Tabelle dei conti, delle transazioni e delle categorie.
	 * Versione 2: aggiunta colonna colore nella tabella categorie.
	 */
    private static final String CATEGORIA_TABLE_UPDATE = "ALTER TABLE " + CategoriesTable.TABLE_NAME + " ADD " + CategoriesTable.COLOR + " text not null default 'color0' ";
    SQLiteDatabase mDb;
    DbHelper mDbHelper;
    Context mContext;

    public MyDatabase(Context ctx) {
        mContext = ctx;
        mDbHelper = new DbHelper(ctx, DB_NAME, null, DB_VERSION);
    }

    public void open() {
        mDb = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDb.close();
    }

    //OPERAZIONI SUI CONTI

    public long addAccount(String nome) {
        ContentValues v = new ContentValues();
        v.put(AccountsTable.NAME, nome);
        v.put(AccountsTable.TOTAL, 0);
        return mDb.insert(AccountsTable.TABLE_NAME, null, v);
    }

    public ArrayList<Account> getAccounts() {
        Cursor c = mDb.query(AccountsTable.TABLE_NAME, null, null, null, null, null, null);
        ArrayList<Account> res = new ArrayList<Account>();

        while (c.moveToNext()) {
            res.add(new Account(c.getLong(0), c.getString(1), c.getDouble(2), getTransactionsNumberByAccount(c.getLong(0))));
        }

        return res;
    }

    public Account getAccount(long accountID) {
        Cursor c = mDb.query(AccountsTable.TABLE_NAME, null, AccountsTable._ID + " = " + accountID, null, null, null, null);
        c.moveToFirst();
        Account res = new Account(c.getLong(0), c.getString(1), c.getDouble(2));
        return res;
    }

    public double getTotalAccountsAmount() {
        double tot = 0;
        Cursor c = mDb.query(AccountsTable.TABLE_NAME, null, null, null, null, null, null);
        while (c.moveToNext()) {
            tot += c.getDouble(2);
        }
        return tot;
    }

    private void updateTotalAccountsAmount(long id, double amount) {
        Account account = getAccount(id);
        double tot = account.total + amount;

        ContentValues v = new ContentValues();
        v.put(AccountsTable.TOTAL, tot);
        String[] wargs = {String.valueOf(id)};
        mDb.update(AccountsTable.TABLE_NAME, v, AccountsTable._ID + " = ?", wargs);
    }

    public void updateAccount(long id, String name) {
        ContentValues v = new ContentValues();
        v.put(AccountsTable.NAME, name);
        String[] args = {String.valueOf(id)};
        mDb.update(AccountsTable.TABLE_NAME, v, AccountsTable._ID + " = ?", args);
    }

    private int getTransactionsNumberByAccount(long id) {
        final String QUERY_NUMTRANSCONTO = "SELECT COUNT(" + TransactionsTable.ACCOUNT + ") FROM " + TransactionsTable.TABLE_NAME + " WHERE " + TransactionsTable.ACCOUNT + " = ? GROUP BY " + TransactionsTable.ACCOUNT;
        String[] wargs = {String.valueOf(id)};
        Cursor c = mDb.rawQuery(QUERY_NUMTRANSCONTO, wargs);
        if (c.moveToFirst())
            return c.getInt(0);
        else
            return 0;
    }

    public void deleteAccount(long accountID) {
        delTransactionFromDBByAccount(accountID);
        delAccountFromDB(accountID);
    }

    public void delAccountFromDB(long id) {
        mDb.delete(AccountsTable.TABLE_NAME, AccountsTable._ID + " = \'" + id + "\'", null);
    }

    public void cleanAccount(long accountID) {
        delTransactionFromDBByAccount(accountID);

        ContentValues v = new ContentValues();
        v.put(AccountsTable.TOTAL, 0);
        String[] args = {String.valueOf(accountID)};
        mDb.update(AccountsTable.TABLE_NAME, v, AccountsTable._ID + " = ?", args);
    }

    // OPERAZIONI SULLE TRANSAZIONI

    public long addTransaction(String nota, double amount, long categoryID, long accountID, int dayofWeek, int day, int month, int year) {
        long id = addTransactionToDB(nota, amount, categoryID, accountID, dayofWeek, day, month, year);
        updateTotalAccountsAmount(accountID, amount);
        return id;
    }

    private long addTransactionToDB(String nota, double amount, long categoryID, long accountID, int dayofWeek, int day, int month, int year) {
        ContentValues v = new ContentValues();
        v.put(TransactionsTable.NOTE, nota);
        v.put(TransactionsTable.AMOUNT, amount);
        v.put(TransactionsTable.CATEGORY, categoryID);
        v.put(TransactionsTable.ACCOUNT, accountID);
        v.put(TransactionsTable.DAYOFWEEK, dayofWeek);
        v.put(TransactionsTable.DAY, day);
        v.put(TransactionsTable.MONTH, month);
        v.put(TransactionsTable.YEAR, year);
        return mDb.insert(TransactionsTable.TABLE_NAME, null, v);
    }

    public void editTransaction(long id, String nota, double differenza, long categoryID, long accountID, int dayofWeek, int day, int month, int year) {
        Transaction t = getTransaction(id);
        double tot = t.amount+ differenza;
        ContentValues v = new ContentValues();
        v.put(TransactionsTable.NOTE, nota);
        v.put(TransactionsTable.AMOUNT, tot);
        v.put(TransactionsTable.CATEGORY, categoryID);
        v.put(TransactionsTable.ACCOUNT, accountID);
        v.put(TransactionsTable.DAYOFWEEK, dayofWeek);
        v.put(TransactionsTable.DAY, day);
        v.put(TransactionsTable.MONTH, month);
        v.put(TransactionsTable.YEAR, year);
        String[] wargs = {String.valueOf(id)};
        mDb.update(TransactionsTable.TABLE_NAME, v, TransactionsTable._ID + " = ?", wargs);
        updateTotalAccountsAmount(accountID, differenza);
    }

    public Transaction getTransaction(long id) {
        Cursor c = mDb.query(TransactionsTable.TABLE_NAME, null, TransactionsTable._ID + " = " + id, null, null, null, null);
        c.moveToFirst();
        Transaction res = new Transaction(c.getLong(0), c.getString(1), c.getDouble(2), c.getLong(3), c.getLong(4), c.getInt(5), c.getInt(6), c.getInt(7), c.getInt(8));

        return res;
    }

    public ArrayList<Transaction> getLastTransactions(int order, double accountID, int limit) {
        Cursor c = null;
        long id = (long) accountID;
        switch (order) {
            case Utils.ASC:
                if(accountID==0)
                    c = mDb.query(TransactionsTable.TABLE_NAME, null, null, null, null, null, TransactionsTable._ID + " ASC", "" + limit);
                else
                    c = mDb.query(TransactionsTable.TABLE_NAME, null, TransactionsTable.ACCOUNT + " = " + id, null, null, null, TransactionsTable._ID + " ASC", "" + limit);
                break;
            case Utils.DESC:
                if(accountID==0)
                    c = mDb.query(TransactionsTable.TABLE_NAME, null, null, null, null, null, TransactionsTable._ID + " DESC", "" + limit);
                else
                    c = mDb.query(TransactionsTable.TABLE_NAME, null, TransactionsTable.ACCOUNT + " = " + id, null, null, null, TransactionsTable._ID + " DESC", "" + limit);
                break;
        }
        ArrayList<Transaction> res = new ArrayList<Transaction>();

        while (c.moveToNext()) {
            res.add(new Transaction(c.getLong(0), c.getString(1), c.getDouble(2), c.getLong(3), c.getLong(4), c.getInt(5), c.getInt(6), c.getInt(7), c.getInt(8)));
        }

        return res;
    }

    public ArrayList<Transaction> getTransactions(int amount_filter, boolean reverse, double accountID, int m, int y) {
        long id = (long) accountID;
        Cursor c =null;
        switch (amount_filter) {
            case Utils.AMOUNT_ALL:
                if(accountID==0) {
                    if (reverse)
                        c = mDb.query(TransactionsTable.TABLE_NAME, null, TransactionsTable.YEAR + " = " + y + " AND " + TransactionsTable.MONTH + " = " + m, null, null, null, TransactionsTable.DAY + " DESC, " + TransactionsTable._ID + " DESC", null);
                    else
                        c = mDb.query(TransactionsTable.TABLE_NAME, null, TransactionsTable.YEAR + " = " + y + " AND " + TransactionsTable.MONTH + " = " + m, null, null, null, TransactionsTable.DAY + " ASC, " + TransactionsTable._ID + " ASC", null);
                } else {
                    if (reverse)
                        c = mDb.query(TransactionsTable.TABLE_NAME, null, TransactionsTable.YEAR + " = " + y + " AND " + TransactionsTable.MONTH + " = " + m + " AND " + TransactionsTable.ACCOUNT + " = " + id, null, null, null, TransactionsTable.DAY + " DESC, " + TransactionsTable._ID + " DESC", null);
                    else
                        c = mDb.query(TransactionsTable.TABLE_NAME, null, TransactionsTable.YEAR + " = " + y + " AND " + TransactionsTable.MONTH + " = " + m + " AND " + TransactionsTable.ACCOUNT + " = " + id, null, null, null, TransactionsTable.DAY + " ASC, " + TransactionsTable._ID + " ASC", null);
                }
                break;
            case Utils.AMOUNT_POSITIVE:
                if(accountID==0)
                    c = mDb.query(TransactionsTable.TABLE_NAME, null, TransactionsTable.YEAR + " = " + y + " AND " + TransactionsTable.MONTH + " = " + m + " AND " + TransactionsTable.AMOUNT + " >= " + 0, null, null, null, TransactionsTable.DAY+ " ASC, " + TransactionsTable._ID + " ASC", null);
                else
                    c = mDb.query(TransactionsTable.TABLE_NAME, null, TransactionsTable.YEAR + " = " + y + " AND " + TransactionsTable.MONTH + " = " + m + " AND " + TransactionsTable.ACCOUNT + " = " + id + " AND " + TransactionsTable.AMOUNT + " >= "+ 0, null, null, null, TransactionsTable.DAY + " ASC, " + TransactionsTable._ID + " ASC", null);
                break;
            case Utils.AMOUNT_NEGATIVE:
                if(accountID==0)
                    c = mDb.query(TransactionsTable.TABLE_NAME, null, TransactionsTable.YEAR + " = " + y + " AND " + TransactionsTable.MONTH + " = " + m + " AND " + TransactionsTable.AMOUNT + " < " + 0, null, null, null, TransactionsTable.DAY+ " ASC, " + TransactionsTable._ID + " ASC", null);
                else
                    c = mDb.query(TransactionsTable.TABLE_NAME, null, TransactionsTable.YEAR + " = " + y + " AND " + TransactionsTable.MONTH + " = " + m + " AND " + TransactionsTable.ACCOUNT + " = " + id + " AND " + TransactionsTable.AMOUNT + " < "+ 0, null, null, null, TransactionsTable.DAY + " ASC, " + TransactionsTable._ID + " ASC", null);
                break;
        }

        ArrayList<Transaction> res = new ArrayList<Transaction>();
        while (c.moveToNext()) {
            res.add(new Transaction(c.getLong(0), c.getString(1), c.getDouble(2), c.getLong(3), c.getLong(4), c.getInt(5), c.getInt(6), c.getInt(7), c.getInt(8)));
        }
        return res;
    }

    public ArrayList<Transaction> getTransactionsMYFiltered(long accountID, int month, int y) {
        Cursor c;

        Calendar cal = new GregorianCalendar();
        int year = cal.get(Calendar.YEAR);
        int anno = year + 1 - y;
        if (month == 0 && y == 0) {
            c = mDb.query(TransactionsTable.TABLE_NAME, null, TransactionsTable.ACCOUNT + " = " + accountID, null, null, null, null, null);
        } else if (month == 0) {
            c = mDb.query(TransactionsTable.TABLE_NAME, null, TransactionsTable.ACCOUNT + " = " + accountID + " AND " + TransactionsTable.YEAR + " = " + anno, null, null, null, TransactionsTable.YEAR + " ASC, " + TransactionsTable.MONTH + " ASC, "
                    + TransactionsTable.DAY + " ASC, " + TransactionsTable._ID + " ASC", null);
        } else if (y == 0) {
            c = mDb.query(TransactionsTable.TABLE_NAME, null, TransactionsTable.ACCOUNT + " = " + accountID + " AND " + TransactionsTable.MONTH + " = " + month, null, null, null, TransactionsTable.YEAR + " ASC, " + TransactionsTable.MONTH + " ASC, "
                    + TransactionsTable.DAY + " ASC, " + TransactionsTable._ID + " ASC", null);
        } else {
            c = mDb.query(TransactionsTable.TABLE_NAME, null, TransactionsTable.ACCOUNT + " = " + accountID + " AND " + TransactionsTable.MONTH + " = " + month + " AND " + TransactionsTable.YEAR + " = " + anno, null, null, null, TransactionsTable.YEAR
                    + " ASC, " + TransactionsTable.MONTH + " ASC, " + TransactionsTable.DAY + " ASC, " + TransactionsTable._ID + " ASC", null);
        }
        ArrayList<Transaction> res = new ArrayList<Transaction>();

        while (c.moveToNext()) {
            res.add(new Transaction(c.getLong(0), c.getString(1), c.getDouble(2), c.getLong(3), c.getLong(4), c.getInt(5), c.getInt(6), c.getInt(7), c.getInt(8)));
        }

        return res;
    }

    public double getAmountTransactionsMYFiltered(long accountID, int m, int y) {
        double totale = 0;
        Cursor c;
        Calendar cal = new GregorianCalendar();
        int year = cal.get(Calendar.YEAR);
        int anno = year + 1 - y;

        if (m == 0 && y == 0) {
            c = mDb.query(TransactionsTable.TABLE_NAME, null, TransactionsTable.ACCOUNT + " = " + accountID, null, null, null, null, null);
        } else if (m == 0) {
            c = mDb.query(TransactionsTable.TABLE_NAME, null, TransactionsTable.ACCOUNT + " = " + accountID + " AND " + TransactionsTable.YEAR + " = " + anno, null, null, null, TransactionsTable.YEAR + " ASC, " + TransactionsTable.MONTH + " ASC, "
                    + TransactionsTable.DAY + " ASC, " + TransactionsTable._ID + " ASC", null);
        } else if (y == 0) {
            c = mDb.query(TransactionsTable.TABLE_NAME, null, TransactionsTable.ACCOUNT + " = " + accountID + " AND " + TransactionsTable.MONTH + " = " + m, null, null, null, TransactionsTable.YEAR + " ASC, " + TransactionsTable.MONTH + " ASC, "
                    + TransactionsTable.DAY + " ASC, " + TransactionsTable._ID + " ASC", null);
        } else {
            c = mDb.query(TransactionsTable.TABLE_NAME, null, TransactionsTable.ACCOUNT + " = " + accountID + " AND " + TransactionsTable.MONTH + " = " + m + " AND " + TransactionsTable.YEAR + " = " + anno, null, null, null, TransactionsTable.YEAR
                    + " ASC, " + TransactionsTable.MONTH + " ASC, " + TransactionsTable.DAY + " ASC, " + TransactionsTable._ID + " ASC", null);
        }
        while (c.moveToNext())
            totale += c.getDouble(2);
        return totale;
    }

    private void delTransactionFromDBByAccount(long accountID) {
        mDb.delete(TransactionsTable.TABLE_NAME, TransactionsTable.ACCOUNT + " = \'" + accountID + "\'", null);
    }

    private void delTransactionFromDB(long id) {
        mDb.delete(TransactionsTable.TABLE_NAME, TransactionsTable._ID + " = \'" + id + "\'", null);
    }

    public void deleteTransaction(long id) {
        Transaction t = getTransaction(id);
        delTransactionFromDB(id);
        double negativo = t.amount * (-1);
        updateTotalAccountsAmount(t.accountID, negativo);
    }

    public long addCategory(String nome, String colore) {
        ContentValues v = new ContentValues();
        v.put(CategoriesTable.NAME, nome);
        v.put(CategoriesTable.COLOR, colore);
        return mDb.insert(CategoriesTable.TABLE_NAME, null, v);
    }

    public void editCategory(long id, String nome, String colore) {
        ContentValues v = new ContentValues();
        v.put(CategoriesTable.NAME, nome);
        v.put(CategoriesTable.COLOR, colore);
        String[] wargs = {String.valueOf(id)};
        mDb.update(CategoriesTable.TABLE_NAME, v, CategoriesTable._ID + " = ?", wargs);
    }

    public ArrayList<Category> getCategories() {
        Cursor c = mDb.query(CategoriesTable.TABLE_NAME, null, null, null, null, null, null);
        ArrayList<Category> res = new ArrayList<Category>();
        while (c.moveToNext()) {
            res.add(new Category(c.getLong(0), c.getString(1), c.getString(2)));
        }
        return res;
    }

    // METODI PER MODIFICARE DATI

    public int getTransactionsNumberByCategory(long id) {
        final String QUERY_NUMTRANSCAT = "SELECT COUNT(" + TransactionsTable.CATEGORY + ") FROM " + TransactionsTable.TABLE_NAME + " WHERE " + TransactionsTable.CATEGORY + " = ? GROUP BY " + TransactionsTable.CATEGORY;
        String[] wargs = {String.valueOf(id)};
        Cursor c = mDb.rawQuery(QUERY_NUMTRANSCAT, wargs);
        if (c.moveToFirst())
            return c.getInt(0);
        else
            return 0;
    }

    public double getTotalAmountbyCategory(long id) {
        final String QUERY_TOTCAT = "SELECT SUM(" + TransactionsTable.AMOUNT + ") FROM " + TransactionsTable.TABLE_NAME + " WHERE " + TransactionsTable.CATEGORY + " = ? GROUP BY " + TransactionsTable.CATEGORY;
        String[] args = {String.valueOf(id)};
        Cursor c = mDb.rawQuery(QUERY_TOTCAT, args);
        if (c.moveToFirst())
            return c.getDouble(0);
        else
            return 0;
    }

    public void moveTransactionsByCategories(long id_from, long id_to) {
        ContentValues v = new ContentValues();
        v.put(TransactionsTable.CATEGORY, id_to);
        String[] wargs = {String.valueOf(id_from)};
        mDb.update(TransactionsTable.TABLE_NAME, v, TransactionsTable.CATEGORY + " = ?", wargs);
    }

    public void delCategory(long id) {
        mDb.delete(CategoriesTable.TABLE_NAME, CategoriesTable._ID + " = \'" + id + "\'", null);
    }

    private class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(TRANSAZIONI_TABLE_CREATE);
            _db.execSQL(CONTI_TABLE_CREATE);
            _db.execSQL(CATEGORIA_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            if (oldVersion < 2) {
                _db.execSQL(CATEGORIA_TABLE_UPDATE);
            }
        }
    }
}