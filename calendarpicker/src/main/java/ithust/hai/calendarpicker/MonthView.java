
package ithust.hai.calendarpicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MonthView extends LinearLayout {
    private TextView title;
    private CalendarGridView grid;
    private Listener listener;
    private Locale locale;
    private boolean alwaysDigitNumbers;
    private StyleResourcesProvider styleResourcesProvider;

    public static MonthView create(ViewGroup parent, LayoutInflater inflater,
                                   DateFormat weekdayNameFormat, Listener listener, Calendar today,
                                   int dayBackgroundResId, int dayTextColorResId, int titleTextStyle,
                                   boolean displayDayNamesHeaderRowView, boolean showAlwaysDigitNumbers,
                                   StyleResourcesProvider styleResourcesProvider,
                                   Locale locale, DayViewAdapter adapter) {
        final MonthView view = (MonthView) inflater.inflate(R.layout.calendar_view_month, parent, false);

        // Set style resources provider
        view.styleResourcesProvider = styleResourcesProvider;

        // Set the views
        view.title = new TextView(new ContextThemeWrapper(view.getContext(), titleTextStyle));
        view.grid = view.findViewById(R.id.calendar_grid);
        ViewGroup dayNamesHeaderRowView = view.findViewById(R.id.day_names_header_row);

        // Add the month title as the first child of MonthView
        view.addView(view.title, 0);

        view.setDayViewAdapter(adapter);
        view.setDayTextColor(dayTextColorResId);

        view.locale = locale;
        view.alwaysDigitNumbers = showAlwaysDigitNumbers;
        int firstDayOfWeek = today.getFirstDayOfWeek();
        // Day Name Section
        if (displayDayNamesHeaderRowView) {
            final int originalDayOfWeek = today.get(Calendar.DAY_OF_WEEK);
            for (int offset = 0; offset < 7; offset++) {
                today.set(Calendar.DAY_OF_WEEK, firstDayOfWeek + offset);
                final TextView textView = (TextView) dayNamesHeaderRowView.getChildAt(offset);
                textView.setText(weekdayNameFormat.format(today.getTime()));
            }
            today.set(Calendar.DAY_OF_WEEK, originalDayOfWeek);
        } else {
            dayNamesHeaderRowView.setVisibility(View.GONE);
        }

        view.listener = listener;
        return view;
    }

    public MonthView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public void init(MonthDescriptor month, List<List<MonthCellDescriptor>> cells, boolean displayOnly) {
        Logr.d("Initializing MonthView (%d) for %s", System.identityHashCode(this), month);
        long start = System.currentTimeMillis();
        title.setText(month.getLabel());
        NumberFormat numberFormatter;
        if (alwaysDigitNumbers) {
            numberFormatter = NumberFormat.getInstance(Locale.US);
        } else {
            numberFormatter = NumberFormat.getInstance(locale);
        }

        final int numRows = cells.size();
        grid.setNumRows(numRows);
        for (int i = 0; i < 6; i++) {
            CalendarRowView weekRow = (CalendarRowView) grid.getChildAt(i);
            weekRow.setListener(listener);
            if (i < numRows) {
                weekRow.setVisibility(VISIBLE);
                List<MonthCellDescriptor> week = cells.get(i);
                for (int c = 0; c < week.size(); c++) {
                    MonthCellDescriptor cell = week.get(c);
                    CalendarCellView cellView = (CalendarCellView) weekRow.getChildAt(c);
                    if (cell.isCurrentMonth()) {
                        String cellDate = numberFormatter.format(cell.getValue());
                        if (!cellView.getDayOfMonthTextView().getText().equals(cellDate)) {
                            cellView.getDayOfMonthTextView().setText(cellDate);
                        }
                        cellView.setEnabled(cell.isSelectable());
                        cellView.getDayOfMonthTextView().setEnabled(cell.isSelectable());
                        cellView.setClickable(!displayOnly);
                        cellView.setSelected(cell.isSelected());

                        cellView.setRangeState(cell.getRangeState(), styleResourcesProvider);
                        cellView.setToday(cell.isToday());
                        cellView.setTag(cell);
                    } else {
                        cellView.setEnabled(false);
                        cellView.setClickable(false);
                        cellView.setSelected(false);

                        cellView.setRangeState(cell.getRangeState(), styleResourcesProvider);
                        cellView.setToday(false);
                        cellView.getDayOfMonthTextView().setText("");
                    }
                }
            } else {
                weekRow.setVisibility(GONE);
            }
        }

        Logr.d("MonthView.init took %d ms", System.currentTimeMillis() - start);
    }

    public void setDayBackground(int resId) {
        grid.setDayBackground(resId);
    }

    public void setDayTextColor(int resId) {
        grid.setDayTextColor(resId);
    }

    public void setDayViewAdapter(DayViewAdapter adapter) {
        grid.setDayViewAdapter(adapter);
    }

    public interface Listener {
        void handleClick(MonthCellDescriptor cell);
    }
}
