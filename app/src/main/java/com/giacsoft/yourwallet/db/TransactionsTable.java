package com.giacsoft.yourwallet.db;

import android.provider.BaseColumns;

public interface TransactionsTable extends BaseColumns {
    String TABLE_NAME = "transazioni";
    String NOTA = "nota";
    String IMPORTO = "importo";
    String CONTO = "conto";
    String CATEGORIA = "categoria";
    String GIORNO = "giorno";
    String DAY = "day";
    String MONTH = "month";
    String YEAR = "year";
}