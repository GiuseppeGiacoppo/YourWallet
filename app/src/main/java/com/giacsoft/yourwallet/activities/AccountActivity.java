package com.giacsoft.yourwallet.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.giacsoft.yourwallet.R;
import com.giacsoft.yourwallet.Utils;
import com.giacsoft.yourwallet.db.MyDatabase;
import com.giacsoft.yourwallet.fragments.AccountFragment;
import com.giacsoft.yourwallet.fragments.GraphFragment;
import com.giacsoft.yourwallet.dialogs.TransactionDialogFragment;
import com.giacsoft.yourwallet.types.Account;
import com.giacsoft.yourwallet.types.Transaction;


public class AccountActivity extends Activity implements TransactionDialogFragment.OnTransazioneDialogListener, View.OnClickListener {
    static final int DLG3 = 3;
    Toolbar toolbar;
    TabLayout tabLayout;
    FloatingActionButton fab;

    long accountID;
    MyDatabase db;
    int selected_item_graph_card_header;
    SharedPreferences preferences;
    AccountFragment accountFragment;
    GraphFragment graphFragment;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.layout_account);
        accountID = getIntent().getExtras().getLong(Utils.ACCOUNT_ID);

        toolbar = (Toolbar) findViewById(R.id.account_toolbar);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        fab = (FloatingActionButton) findViewById(R.id.fab_account);
        fab.setOnClickListener(this);
        accountFragment = new AccountFragment();
        graphFragment = new GraphFragment();

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Prova1"));
        tabLayout.addTab(tabLayout.newTab().setText("Prova2"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPager);
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager(), tabLayout.getTabCount());

        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        db = new MyDatabase(getApplicationContext());

        if (accountID != 0) {
            db.open();
            Account c1 = db.getAccount(accountID);
            db.close();
        } else
            preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @Override
    public void onClick(View v) {
        if(v==fab) {
                if (accountID != 0)
                    showDialog(DLG3, accountID, 0);
                else
                    showDialog(DLG3, 0, 0);
        }
    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public ViewPagerAdapter(FragmentManager manager, int NumOfTabs) {
            super(manager);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    AccountFragment AF = new AccountFragment();
                    return AF;
                case 1:
                    GraphFragment GF = new GraphFragment();
                    return GF;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
         //updateTransactions(idarrayin[0]);
        //selected_item_graph_card_header = preferences.getInt("selected_item_graph_card_header", 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_account_total, menu);
        MenuItem item = menu.findItem(R.id.menu_cerca);
        if (accountID != 0)
            item.setVisible(true);
        else
            item.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // switch che stabilisce le azioni a seconda dell'oggetto nel menu
        switch (item.getItemId()) {
            case R.id.menu_cerca:
                if (accountID != 0) {
                    //long idarrayoutgraph[] = { idconto , 0};
                    Intent i = new Intent(getApplicationContext(), AccountFiltersActivity.class);
                    i.putExtra(Utils.ACCOUNT_ID, accountID);
                    i.putExtra(Utils.FILTRO_PREV_ACTIVITY, 0);
                    startActivity(i);
                } else
                    Toast.makeText(this, R.string.toast_error_select_account, Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_categorie:
                Intent i = new Intent(getApplicationContext(), CategoriesActivity.class);
                i.putExtra(Utils.ACCOUNT_ID, accountID);
                i.putExtra(Utils.CAT_PREV_ACTIVITY, 0);
                startActivity(i);
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void showDialog(int id, long idc, long idt) {
        switch (id) {
            case DLG3:
                DialogFragment tdf = TransactionDialogFragment.newInstance(idc, idt);
                tdf.show(getFragmentManager(), "transazionedialog");
                break;
        }
    }

    @Override
    public void doTransaction(int mode, Transaction t,double diff, int p) {
        switch (mode) {
            case Utils.ADD:
                //TODO
                accountFragment.doTransaction(Utils.ADD, t,0);
                selected_item_graph_card_header = preferences.getInt("selected_item_graph_card_header", 0);
                graphFragment.updateGraph(accountID, selected_item_graph_card_header);
                break;
            case Utils.EDIT:
                accountFragment.doTransaction(Utils.EDIT, t, p);
                selected_item_graph_card_header = preferences.getInt("selected_item_graph_card_header", 0);
                graphFragment.updateGraph(accountID, selected_item_graph_card_header);
                break;
            case Utils.DELETE:
                accountFragment.doTransaction(Utils.DELETE,t, p);
                selected_item_graph_card_header = preferences.getInt("selected_item_graph_card_header", 0);
                graphFragment.updateGraph(accountID, selected_item_graph_card_header);
                break;
        }
    }
}
