package com.giacsoft.yourwallet.db;

import android.provider.BaseColumns;

public interface AccountsTable extends BaseColumns {
    String TABLE_NAME = "conti";

    String NAME = "nome";
    String TOTAL = "totale";
}