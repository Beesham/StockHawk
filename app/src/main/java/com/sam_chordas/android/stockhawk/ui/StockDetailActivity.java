package com.sam_chordas.android.stockhawk.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.sam_chordas.android.stockhawk.R;

public class StockDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = StockDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        String quote = getIntent().getStringExtra("quote");

        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            arguments.putString("quote", quote);

            StockDetailFragment detailFragment = new StockDetailFragment();
            if(detailFragment != null) {
                Log.v(LOG_TAG, "fragment exists");
                detailFragment.setArguments(arguments);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.stock_detail_fragment_container, detailFragment)
                        .commit();
            }
        }
    }
}
