package com.sam_chordas.android.stockhawk.ui;

import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.sam_chordas.android.stockhawk.R;
import com.github.mikephil.charting.data.Entry;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;


public class StockActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int LOADER_ID = 0;
    private final String LOG_TAG = StockActivity.class.getSimpleName();

    private LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        getLoaderManager().initLoader(LOADER_ID, null, this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_stock);

        mChart = (LineChart) findViewById(R.id.chart1);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //TODO Limit query to last 15

        Uri uri = getIntent().getData();
        Log.v(LOG_TAG, uri.toString());
        return new CursorLoader(this, uri,
                new String[]{QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE, QuoteColumns.PERCENT_CHANGE, QuoteColumns.CREATED},
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String symbolString = "";
        Float bidPrice;
        Float maxPrice =0f, minPrice =0f ;
        String percentChangeString;
        String createdString;

        ArrayList<Entry> yVals = new ArrayList<Entry>();
        ArrayList<String> labels = new ArrayList<String>();
        int yValCounter = 0;

        //LineDataSet dataset = new LineDataSet();

        if (data.getCount() > 0) {

            while (data.moveToNext()) {
                String bidPriceString = data.getString(1);

                if (bidPriceString!=null){
                    Float bidPriceFloat = Float.parseFloat(bidPriceString);
                    yVals.add(new Entry(bidPriceFloat, yValCounter));
                    labels.add(String.valueOf(yValCounter));
                    yValCounter++;
                    if(data.isFirst()){
                        maxPrice = minPrice = bidPriceFloat;
                    }
                    if(bidPriceFloat > maxPrice){
                        maxPrice = bidPriceFloat;
                    }
                    if(bidPriceFloat < minPrice){
                        minPrice = bidPriceFloat;
                    }
                }

                if(data.isFirst()){
                    symbolString = data.getString(0);
                }


            }

            // create a dataset and give it a type
            LineDataSet set1 = new LineDataSet(yVals, "Bid Prices");

            LineData lineData = new LineData(labels, set1);
            mChart.setData(lineData); // set the data and list of lables into chart

            mChart.setDrawGridBackground(false);

            // no description text
            mChart.setDescription(symbolString);
            mChart.setDescriptionTextSize(16f);
            mChart.setNoDataTextDescription("Ni data available.");
            mChart.setBackgroundColor(Color.WHITE);

            set1.setDrawCubic(true);
            set1.setDrawFilled(true);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
