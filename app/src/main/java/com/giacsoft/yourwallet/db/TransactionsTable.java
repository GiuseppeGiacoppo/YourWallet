package com.giacsoft.yourwallet.db;

import android.provider.BaseColumns;

public interface TransactionsTable extends BaseColumns {
    String TABLE_NAME = "transazioni";
    String NOTE = "nota";
    String AMOUNT = "importo";
    String ACCOUNT = "conto";
    String CATEGORY = "categoria";
    String DAYOFWEEK = "giorno";
    String DAY = "day";
    String MONTH = "month";
    String YEAR = "year";
}