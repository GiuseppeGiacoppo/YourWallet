package com.giacsoft.yourwallet.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.widget.Toolbar;

import com.giacsoft.yourwallet.R;
import com.giacsoft.yourwallet.Utils;
import com.giacsoft.yourwallet.dialogs.AboutDialogFragment;
import com.giacsoft.yourwallet.dialogs.AccountDialogFragment;
import com.giacsoft.yourwallet.dialogs.CurrencyDialogFragment;
import com.giacsoft.yourwallet.dialogs.ImportDBDialogFragment;
import com.giacsoft.yourwallet.dialogs.ImportLiteDialogFragment;
import com.giacsoft.yourwallet.dialogs.PinDialogFragment;
import com.giacsoft.yourwallet.dialogs.TransactionDialogFragment;

public class BaseActivity extends Activity {

    private Toolbar mActionBarToolbar;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
    }

    protected Toolbar getActionBarToolbar() {if (mActionBarToolbar == null) {
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        if (mActionBarToolbar != null) {
            setActionBar(mActionBarToolbar);
        }
    }
        return mActionBarToolbar;
    }

    protected void showDialog(int dialogID, FragmentManager mFragmentManager, long... id) {
        switch (dialogID) {
            case Utils.ABOUT_DIALOG:
                DialogFragment aboutDialog = AboutDialogFragment.newInstance();
                aboutDialog.show(mFragmentManager, String.valueOf(Utils.ABOUT_DIALOG));
                break;
            case Utils.ACCOUNT_DIALOG:
                AccountDialogFragment accountDialog = AccountDialogFragment.newIstance();
                accountDialog.show(mFragmentManager, String.valueOf(Utils.ACCOUNT_DIALOG));
                break;
            case Utils.CURRENCY_DIALOG:
                CurrencyDialogFragment currencyDialog = CurrencyDialogFragment.newInstance();
                currencyDialog.show(mFragmentManager, String.valueOf(Utils.CURRENCY_DIALOG));
                break;
            case Utils.IMPORT_DIALOG:
                DialogFragment importDialog = ImportDBDialogFragment.newInstance();
                importDialog.show(mFragmentManager, String.valueOf(Utils.IMPORT_DIALOG));
                break;
            case Utils.IMPORTLITE_DIALOG:
                DialogFragment importLiteDialog = ImportLiteDialogFragment.newInstance();
                importLiteDialog.show(mFragmentManager, String.valueOf(Utils.IMPORTLITE_DIALOG));
            case Utils.PIN_DIALOG:
                PinDialogFragment pinDialog = PinDialogFragment.newInstance();
                pinDialog.show(mFragmentManager, String.valueOf(Utils.PIN_DIALOG));
                break;
            case Utils.TRANSACTION_DIALOG:
                DialogFragment transactionDialog = TransactionDialogFragment.newInstance(id[0], id[1]);
                transactionDialog.show(mFragmentManager, String.valueOf(Utils.TRANSACTION_DIALOG));
                break;
        }
    }
}
