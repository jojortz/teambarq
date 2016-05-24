package com.teambarq.barq;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

/**
 * Created by Sanditi on 5/23/16.
 */
public class IntForBarChartValueFormatter implements YAxisValueFormatter {
    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        return "" + ((int) value);
    }
}
