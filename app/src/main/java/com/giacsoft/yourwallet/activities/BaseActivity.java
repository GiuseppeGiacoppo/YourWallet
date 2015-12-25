package com.giacsoft.yourwallet.activities;

import android.app.Activity;
import android.widget.Toolbar;

import com.giacsoft.yourwallet.R;

/**
 * Created by Peppe on 25/12/2015.
 */
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
}
