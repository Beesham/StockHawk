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
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;

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

    private static final int CURSOR_LOADER_ID = 1;

    private LineChart mLineChartView;

    public StockDetailFragment() {
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
            List<Entry> values = new ArrayList<>();
            ArrayList<String> xAxisLables = new ArrayList<>();
            int y=0;
            try {
                JSONArray historicalDataJSONArray = (new JSONObject(data.getString(data.getColumnIndex(QuoteColumns.HISTORICALDATA)))).getJSONArray("series");
                for(int i = 0; i < historicalDataJSONArray.length(); i++){
                    JSONObject tickData = historicalDataJSONArray.getJSONObject(i);
                    values.add(new Entry(y++,Float.parseFloat(tickData.getString("close"))));
                    xAxisLables.add(Utils.formatDate(tickData.getLong("Timestamp")));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            LineDataSet dataset = new LineDataSet(values, "Change");
            dataset.setColor(getResources().getColor(R.color.white));
            dataset.setDrawValues(false);
            dataset.setHighlightEnabled(false);
            dataset.setDrawCircles(false);

            LineData lineData = new LineData(dataset);
            mLineChartView.setData(lineData);
            mLineChartView.getLegend().setEnabled(false);
            mLineChartView.setDescription(null);

            String[] xAxisLablesStrArr = new String[xAxisLables.size()];
            XAxis xAxis = mLineChartView.getXAxis();
            xAxis.setValueFormatter(new MyXAxisValueFormatter(xAxisLables.toArray(xAxisLablesStrArr)));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            mLineChartView.getAxisRight().setDrawLabels(false);
            mLineChartView.invalidate();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {

        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return mValues[(int) value];
        }

        /** this is only needed if numbers are returned, else return 0 */
        @Override
        public int getDecimalDigits() { return 0; }
    }
}
