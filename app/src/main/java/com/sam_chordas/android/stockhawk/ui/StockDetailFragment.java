package com.sam_chordas.android.stockhawk.ui;


import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;

import static com.google.android.gms.common.stats.zzb.zza.FL;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class StockDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private LineChartView mLineChartView;

    public StockDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_stock_detail, container, false);
        mLineChartView = (LineChartView) rootView.findViewById(R.id.linechart);

        String[] labels = {"9am", "10am", "11am", "12pm", "1pm", "2pm", "3pm", "4pm"};

        LineSet dataset = new LineSet(labels, new float[] {1f,2f,3f,4f,4f,6f,7f,8f});
        mLineChartView.addData(dataset);
        mLineChartView.show();

        return  rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
