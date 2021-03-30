// Copyright 2013 Square, Inc.

package ithust.hai.calendarpicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

public class CalendarCellView extends FrameLayout {
    private boolean isHighlight = false;
    private TextView dayOfMonthTextView;

    public CalendarCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setHighlightDate(boolean isHighlight) {
        if (this.isHighlight != isHighlight) {
            this.isHighlight = isHighlight;
            if (isHighlight) {
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

    @Override
    public void setActivated(boolean activated) {
        super.setActivated(activated);
        dayOfMonthTextView.setActivated(activated);
    }

    public void setRangeState(byte rangeState) {
        switch (rangeState) {
            case RangeState.NONE:
                if (!isSelected()) {
                    setBackgroundColor(Color.WHITE);
                } else {
                    if (isActivated()) {
                        setBackgroundResource(R.drawable.calendar_date_picker_selected);
                    } else {
                        setBackgroundResource(R.drawable.calendar_date_picker_inactive);
                    }
                }
                break;
            case RangeState.START_WEEK:
                setBackgroundResource(R.drawable.calendar_date_picker_selected_start_week);
                break;
            case RangeState.END_WEEK:
                setBackgroundResource(R.drawable.calendar_date_picker_selected_end_week);
                break;
            case RangeState.MIDDLE:
                setBackgroundResource(R.drawable.calendar_date_picker_selected_middle);
                break;
            case RangeState.FIRST:
                setBackgroundResource(R.drawable.calendar_date_picker_selected_left);
                break;
            case RangeState.LAST:
                setBackgroundResource(R.drawable.calendar_date_picker_selected_right);
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
