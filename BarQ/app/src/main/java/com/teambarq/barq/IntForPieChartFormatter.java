package com.teambarq.barq;

import com.github.mikephil.charting.utils.ValueFormatter;

/**
 * Created by Sanditi on 5/22/16.
 */
public class IntForPieChartFormatter implements ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        return "" + ((int) value);
    }
}
