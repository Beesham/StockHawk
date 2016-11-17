package com.sam_chordas.android.stockhawk.ui;


import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;
import static android.R.attr.y;
import static com.google.android.gms.common.stats.zzb.zza.FL;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class StockDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String LOG_TAG = StockDetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";

    private static final int CURSOR_LOADER_ID = 1;

    private LineChart mLineChartView;

    public StockDetailFragment() {
        // Required empty public constructor
        hasOptionsMenu();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(CURSOR_LOADER_ID, getArguments(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_stock_detail, container, false);
        mLineChartView = (LineChart) rootView.findViewById(R.id.linechart);

        Log.v(LOG_TAG, "Quote: " + getArguments().get("quote"));


        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
                ab.setDisplayHomeAsUpEnabled(true);
                ab.setTitle(getArguments().get("quote").toString());

        return  rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),
                QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns._ID,
                        QuoteColumns.SYMBOL,
                        QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE,
                        QuoteColumns.CHANGE,
                        QuoteColumns.ISUP,
                        QuoteColumns.HISTORICALDATA},
                QuoteColumns.SYMBOL + " = ?",
                new String[]{args.get("quote").toString()},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()){
            Log.v(LOG_TAG, "data: " + data.getString(data.getColumnIndex(QuoteColumns.HISTORICALDATA)));
            List<Entry> values = new ArrayList<>();
            int y=0;
            try {
                JSONArray historicalDataJSONArray = new JSONArray(data.getString(data.getColumnIndex(QuoteColumns.HISTORICALDATA)));
                for(int i = 0; i < historicalDataJSONArray.length(); i++){
                    values.add(new Entry(y++,Float.parseFloat(((JSONObject) historicalDataJSONArray.get(i)).getString("Close"))));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            LineDataSet dataset = new LineDataSet(values, "Bid change");
            dataset.setColor(getResources().getColor(R.color.white));

            LineData lineData = new LineData(dataset);
            mLineChartView.setData(lineData);
            mLineChartView.invalidate();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
