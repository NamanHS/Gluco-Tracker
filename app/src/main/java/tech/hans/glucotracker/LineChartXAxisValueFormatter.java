package tech.hans.glucotracker;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class LineChartXAxisValueFormatter implements IAxisValueFormatter {
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // Convert float value to date string
        // Convert from seconds back to milliseconds to format time  to show to the user
        long emissionsMilliSince1970Time = ((long) value) * 1000;

        // Show time in local version
        Date timeMilliseconds = new Date(emissionsMilliSince1970Time);
        DateFormat dateTimeFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());

        return dateTimeFormat.format(timeMilliseconds);
    }
}
