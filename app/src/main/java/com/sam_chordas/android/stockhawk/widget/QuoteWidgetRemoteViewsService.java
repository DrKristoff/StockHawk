package com.sam_chordas.android.stockhawk.widget;

/**
 * Created by ryand on 4/10/2016.
 */
import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.support.v4.app.TaskStackBuilder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteDatabase;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;
import com.sam_chordas.android.stockhawk.ui.StockActivity;

/**
 * IntentService which handles updating all Today widgets with the latest data
 */
public class QuoteWidgetRemoteViewsService extends RemoteViewsService {

  private static final String[] STOCK_COLUMNS = {
          QuoteDatabase.QUOTES + "." + QuoteColumns._ID,
          QuoteColumns.SYMBOL,
          QuoteColumns.BIDPRICE,
          QuoteColumns.PERCENT_CHANGE,
          QuoteColumns.CHANGE
  };

  static final int INDEX_STOCK_ID = 0;
  static final int INDEX_STOCK_SYMBOL = 1;
  static final int INDEX_STOCK_BIDPRICE = 2;
  static final int INDEX_STOCK_PERCENT_CHANGE = 3;
  static final int INDEX_STOCK_CHANGE = 4;

  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    return new RemoteViewsFactory() {
      private Cursor data = null;

      @Override
      public void onCreate() {

      }

      @Override
      public void onDataSetChanged() {
        if (data != null) {
          data.close();
        }
        final long identityToken = Binder.clearCallingIdentity();
        data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                STOCK_COLUMNS,
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
        Binder.restoreCallingIdentity(identityToken);
      }

      @Override
      public void onDestroy() {
        if (data != null) {
          data.close();
          data = null;
        }
      }

      @Override
      public int getCount() {
        return data == null ? 0 : data.getCount();
      }

      @Override
      public RemoteViews getViewAt(int position) {

        if (position == AdapterView.INVALID_POSITION ||
                data == null || !data.moveToPosition(position)) {
          return null;
        }
        RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.widget_list_item);
        String stockSymbol = data.getString(INDEX_STOCK_SYMBOL);
        double bid = data.getDouble(INDEX_STOCK_BIDPRICE);
        double change = data.getDouble(INDEX_STOCK_CHANGE);

        remoteView.setTextViewText(R.id.changeTextView, String.valueOf(change));
        remoteView.setTextViewText(R.id.stockSymbolTextView, stockSymbol);
        
        return remoteView;
      }

      @Override
      public RemoteViews getLoadingView() {
        return new RemoteViews(getPackageName(), R.layout.widget_list_item);
      }

      @Override
      public int getViewTypeCount() {
        return 1;
      }

      @Override
      public long getItemId(int position) {
        if (data.moveToPosition(position))
          return data.getLong(INDEX_STOCK_ID);
        return position;
      }

      @Override
      public boolean hasStableIds() {
        return true;
      }
    };
  }
}
