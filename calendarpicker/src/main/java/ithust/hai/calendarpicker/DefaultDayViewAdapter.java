package ithust.hai.calendarpicker;

import android.view.ContextThemeWrapper;
import android.widget.FrameLayout;
import android.widget.TextView;

import static android.view.Gravity.CENTER;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class DefaultDayViewAdapter implements DayViewAdapter {
    @Override
    public void makeCellView(CalendarCellView parent) {
        TextView textView = new TextView(
                new ContextThemeWrapper(parent.getContext(), R.style.CalendarCell_CalendarDate));
        parent.addView(textView, new FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, CENTER));
        parent.setDayOfMonthTextView(textView);
    }
}
