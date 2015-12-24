package com.giacsoft.yourwallet.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.giacsoft.yourwallet.R;
import com.giacsoft.yourwallet.activities.MainActivity;
import com.giacsoft.yourwallet.activities.TransactionDetailActivity;
import com.giacsoft.yourwallet.db.MyDatabase;

import java.text.DecimalFormat;

public class Widget_Total_4x1 extends AppWidgetProvider {

    MyDatabase db;
    DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        final int N = appWidgetIds.length;

        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);


            Intent intent2 = new Intent(context, TransactionDetailActivity.class);
            PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intent2, 0);
            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_total_4x1);

            views.setOnClickPendingIntent(R.id.rl_widget_main, pendingIntent);
            views.setOnClickPendingIntent(R.id.iv_widget_add, pendingIntent2);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String cur = preferences.getString("currency", "$");
            db = new MyDatabase(context);
            db.open();
            double tot = db.getTotalAccountsAmount();

            views.setTextViewText(R.id.tv_total_widget, df.format(tot) + " " + cur);

            if (tot >= 0)
                views.setTextColor(R.id.tv_total_widget, context.getResources().getColor(R.color.positive));
            else
                views.setTextColor(R.id.tv_total_widget, context.getResources().getColor(R.color.negative));
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
