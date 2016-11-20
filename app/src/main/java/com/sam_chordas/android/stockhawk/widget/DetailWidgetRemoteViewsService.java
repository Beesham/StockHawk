package com.sam_chordas.android.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * Created by Beesham on 11/13/2016.
 */

public class DetailWidgetRemoteViewsService extends RemoteViewsService {

    private static final String[] QUOTE_COLUMNS = {
            QuoteColumns._ID,
            QuoteColumns.BIDPRICE,
            QuoteColumns.ISUP,
            QuoteColumns.CHANGE,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.SYMBOL
    };

    static final int INDEX_QUOTE_ID = 0;
    static final int INDEX_QUOTE_BIDPRICE = 1;
    static final int INDEX_QUOTE_ISUP = 2;
    static final int INDEX_QUOTE_CHANGE = 3;
    static final int INDEX_QUOTE_PERCENT_CHANGE = 4;
    static final int INDEX_QUOTE_SYMBOL = 5;


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {

            private Cursor data = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if(data != null) {
                    data.close();
                }

                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        QUOTE_COLUMNS,
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if(data != null){
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
                if(position == AdapterView.INVALID_POSITION ||
                        data == null ||
                        !data.moveToPosition(position)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_detail_list_item);

                int quoteId = data.getInt(INDEX_QUOTE_ID);
                String quoteSymbol = data.getString(INDEX_QUOTE_SYMBOL);
                String quoteBid = data.getString(INDEX_QUOTE_BIDPRICE);
                int quoteIsUp = data.getInt(INDEX_QUOTE_ISUP);
                String quoteChange = data.getString(INDEX_QUOTE_CHANGE);
                String quotePercentChange = data.getString(INDEX_QUOTE_PERCENT_CHANGE);


                views.setTextViewText(R.id.widget_stock_symbol, quoteSymbol);
                views.setTextViewText(R.id.widget_bid_price, quoteBid);
                views.setTextViewText(R.id.widget_change, quoteChange);

                int sdk = Build.VERSION.SDK_INT;
                if (quoteIsUp == 1){
                    if (sdk < Build.VERSION_CODES.JELLY_BEAN){
                         views.setInt(R.id.widget_change, "setBackgroundResource",
                                (R.drawable.percent_change_pill_green));
                    }else {
                        views.setInt(R.id.widget_change, "setBackgroundResource",
                                (R.drawable.percent_change_pill_green));
                    }
                } else{
                    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                        views.setInt(R.id.widget_change, "setBackgroundResource",
                                (R.drawable.percent_change_pill_red));
                    } else{
                        views.setInt(R.id.widget_change, "setBackgroundResource",
                                (R.drawable.percent_change_pill_red));
                    }
                }

                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra ("quote", quoteSymbol);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(),
                        R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if(data.moveToPosition(position)){
                    return data.getLong(INDEX_QUOTE_ID);
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
