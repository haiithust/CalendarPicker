package calendarpicker.ithust.hai.calendarpickerdemo;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import ithust.hai.calendarpicker.CalendarCellView;
import ithust.hai.calendarpicker.DayViewAdapter;

public class SampleDayViewAdapter implements DayViewAdapter {
  @Override public void makeCellView(CalendarCellView parent) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View layout = inflater.inflate(R.layout.day_view_custom, parent, false);
    parent.addView(layout);
    parent.setDayOfMonthTextView((TextView) layout.findViewById(R.id.day_view));
  }
}
