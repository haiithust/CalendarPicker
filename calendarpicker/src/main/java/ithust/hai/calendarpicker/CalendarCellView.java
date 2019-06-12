// Copyright 2013 Square, Inc.

package ithust.hai.calendarpicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

public class CalendarCellView extends FrameLayout {
    private boolean isToday = false;
    private TextView dayOfMonthTextView;

    public CalendarCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setToday(boolean isToday) {
        if (this.isToday != isToday) {
            this.isToday = isToday;
            if (isToday) {
                if (dayOfMonthTextView != null) {
                    dayOfMonthTextView.setTypeface(null, Typeface.BOLD);
                }
            } else {
                if (dayOfMonthTextView != null) {
                    dayOfMonthTextView.setTypeface(null, Typeface.NORMAL);
                }
            }
        }
    }

    public void setRangeState(byte rangeState, StyleResourcesProvider resourcesProvider) {
        switch (rangeState) {
            case RangeState.NONE:
                if (!isSelected()) {
                    setBackgroundColor(Color.WHITE);
                } else {
                    setBackgroundResource(resourcesProvider.getDefaultDateSelected());
                }
                break;
            case RangeState.START_WEEK:
                setBackgroundResource(resourcesProvider.getDateStartWeekSelected());
                break;
            case RangeState.END_WEEK:
                setBackgroundResource(resourcesProvider.getDateEndWeekSelected());
                break;
            case RangeState.MIDDLE:
                setBackgroundResource(resourcesProvider.getDateMiddleWeekSelected());
                break;
            case RangeState.FIRST:
                setBackgroundResource(resourcesProvider.getStartingDateSelected());
                break;
            case RangeState.LAST:
                setBackgroundResource(resourcesProvider.getEndingDateSelected());
                break;
        }
    }

    public void setDayOfMonthTextView(TextView textView) {
        dayOfMonthTextView = textView;
    }

    public TextView getDayOfMonthTextView() {
        return dayOfMonthTextView;
    }
}
