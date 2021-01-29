
package ithust.hai.calendarpicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.YEAR;

/**
 * Android component to allow picking a date from a calendar view (a list of months).  Must be
 * initialized after inflation with {@link #init(Date, Date)} and can be customized with any of the
 * {@link FluentInitializer} methods returned.  The currently selected date can be retrieved with
 */
public class CalendarPickerView extends RecyclerView {
    private final CalendarPickerView.MonthAdapter adapter;
    private final MonthView.Listener listener = new CellClickedListener();
    private final List<Calendar> selectedCals = new ArrayList<>();
    // with mode MULTIPLE if restricted calendar not empty it means only able to select inside list
    private final HashSet<Long> restrictedCals = new HashSet<>();
    private Locale locale;
    private DateFormat monthNameFormat;
    private DateFormat weekdayNameFormat;
    private Calendar minCal;
    private Calendar maxCal;
    private Calendar monthCounter;
    private Calendar today;
    private boolean displayOnly;
    private byte selectionMode;
    private int dayBackgroundResId;
    private int dayTextColorResId;
    private int titleTextStyle;
    private boolean displayDayNamesHeaderRow;
    private boolean displayAlwaysDigitNumbers;
    private StyleResourcesProvider styleResourcesProvider;

    private OnDateSelectedListener dateListener;
    private DateSelectableFilter dateConfiguredListener;
    private OnInvalidDateSelectedListener invalidDateListener;
    private CellClickInterceptor cellClickInterceptor;
    private DayViewAdapter dayViewAdapter = new DefaultDayViewAdapter();

    private int totalMonth;

    public CalendarPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Resources res = context.getResources();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CalendarPickerView);
        final int bg = a.getColor(R.styleable.CalendarPickerView_android_background,
                res.getColor(R.color.calendar_bg));
        dayBackgroundResId = a.getResourceId(R.styleable.CalendarPickerView_tsquare_dayBackground,
                R.drawable.calendar_bg_selector);
        dayTextColorResId = a.getResourceId(R.styleable.CalendarPickerView_tsquare_dayTextColor,
                R.color.calendar_text_selector);
        titleTextStyle = a.getResourceId(R.styleable.CalendarPickerView_tsquare_titleTextStyle,
                R.style.CalendarTitle);
        displayDayNamesHeaderRow =
                a.getBoolean(R.styleable.CalendarPickerView_tsquare_displayDayNamesHeaderRow, true);
        displayAlwaysDigitNumbers =
                a.getBoolean(R.styleable.CalendarPickerView_tsquare_displayAlwaysDigitNumbers, false);
        styleResourcesProvider = StyleResourcesProvider.createFromAttributes(a);
        a.recycle();

        setLayoutManager(new LinearLayoutManager(getContext()));
        setHasFixedSize(true);
        adapter = new MonthAdapter();
        setBackgroundColor(bg);
        locale = Locale.getDefault();
        today = Calendar.getInstance(locale);
        minCal = Calendar.getInstance(locale);
        maxCal = Calendar.getInstance(locale);
        monthCounter = Calendar.getInstance(locale);
        weekdayNameFormat = new SimpleDateFormat(context.getString(R.string.day_name_format), locale);
        monthNameFormat = new SimpleDateFormat(context.getString(R.string.month_name_format), locale);

        if (isInEditMode()) {
            Calendar nextYear = Calendar.getInstance(locale);
            nextYear.add(Calendar.YEAR, 1);

            init(new Date(), nextYear.getTime()) //
                    .withSelectedDate(new Date());
        }
    }

    public FluentInitializer init(@NonNull Date minDate, @NonNull Date maxDate, @NonNull Locale locale) {
        // Make sure that all calendar instances use the same time zone and locale.
        this.locale = locale;
        today = Calendar.getInstance(locale);
        minCal = Calendar.getInstance(locale);
        maxCal = Calendar.getInstance(locale);
        monthCounter = Calendar.getInstance(locale);
        weekdayNameFormat = new SimpleDateFormat(getContext().getString(R.string.day_name_format), locale);
        monthNameFormat = new SimpleDateFormat(getContext().getString(R.string.month_name_format), locale);

        this.selectionMode = SelectionMode.SINGLE;
        // Clear out any previously-selected dates/cells.
        selectedCals.clear();

        // Clear previous state.
        minCal.setTime(minDate);
        maxCal.setTime(maxDate);
        setMidnight(minCal);
        setMidnight(maxCal);
        displayOnly = false;

        // maxDate is exclusive: bump back to the previous day so if maxDate is the first of a month,
        // we don't accidentally include that month in the view.
        maxCal.add(MINUTE, -1);

        // Now iterate between minCal and maxCal and build up our list of months to show.
        monthCounter.setTime(minCal.getTime());
        final int maxMonth = maxCal.get(MONTH);
        final int maxYear = maxCal.get(YEAR);
        totalMonth = 0;
        while ((monthCounter.get(MONTH) <= maxMonth // Up to, including the month.
                || monthCounter.get(YEAR) < maxYear) // Up to the year.
                && monthCounter.get(YEAR) < maxYear + 1) { // But not > next yr.
            totalMonth++;
            monthCounter.add(MONTH, 1);
        }

        return new FluentInitializer();
    }

    public FluentInitializer init(Date minDate, Date maxDate) {
        return init(minDate, maxDate, Locale.getDefault());
    }

    public class FluentInitializer {
        /**
         * Override the {@link SelectionMode} from the default ({@link SelectionMode#SINGLE}).
         */
        public FluentInitializer inMode(byte mode) {
            selectionMode = mode;
            return this;
        }

        /**
         * Set an initially-selected date.  The calendar will scroll to that date if it's not already
         * visible.
         */
        public FluentInitializer withSelectedDate(Date selectedDates) {
            return withSelectedDates(Collections.singletonList(selectedDates));
        }

        /**
         * Set multiple selected dates.  This will throw an {@link IllegalArgumentException} if you
         * pass in multiple dates and haven't already called {@link #(SelectionMode)}.
         */
        public FluentInitializer withSelectedDates(Collection<Date> selectedDates) {
            if (selectionMode == SelectionMode.SINGLE && selectedDates.size() > 1) {
                throw new IllegalArgumentException("SINGLE mode can't be used with multiple selectedDates");
            }
            if (selectionMode == SelectionMode.RANGE && selectedDates.size() > 2) {
                throw new IllegalArgumentException(
                        "RANGE mode only allows two selectedDates.  You tried to pass " + selectedDates.size());
            }
            if (selectedDates != null) {
                for (Date date : selectedDates) {
                    selectDate(date);
                }
            }
            scrollToSelectedDates();
            return this;
        }

        /**
         * Should call after {@link @withSelectedDates}
         */
        public FluentInitializer withRestrictedDate(@NonNull List<Date> restrictedDates) {
            for (Date date : restrictedDates) {
                Calendar cal = Calendar.getInstance(locale);
                cal.setTime(date);
                setMidnight(cal);
                restrictedCals.add(cal.getTimeInMillis());
            }
            return this;
        }

        @SuppressLint("SimpleDateFormat")
        public FluentInitializer setShortWeekdays(String[] newShortWeekdays) {
            DateFormatSymbols symbols = new DateFormatSymbols(locale);
            symbols.setShortWeekdays(newShortWeekdays);
            weekdayNameFormat =
                    new SimpleDateFormat(getContext().getString(R.string.day_name_format), symbols);
            return this;
        }

        public FluentInitializer displayOnly() {
            displayOnly = true;
            return this;
        }

        public void show() {
            validateAndUpdate();
        }
    }

    public void newSelectedDate(Collection<Date> selectedDates) {
        clearOldSelections();
        if (selectedDates != null) {
            for (Date date : selectedDates) {
                selectDate(date);
            }
        }
        validateAndUpdate();
        scrollToSelectedDates();
    }

    private void validateAndUpdate() {
        if (getAdapter() == null) {
            setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();
    }

    private void scrollToSelectedMonth(int selectedIndex) {
        scrollToPosition(selectedIndex);
    }

    private void scrollToSelectedDates() {
        int selectedIndex = NO_POSITION;
        Calendar cal = minDate(selectedCals);
        if (cal == null) {
            cal = Calendar.getInstance(locale);
        }

        for (int c = 0; c < totalMonth; c++) {
            monthCounter.setTime(minCal.getTime());
            monthCounter.add(MONTH, c);

            if (sameMonth(cal, monthCounter)) {
                selectedIndex = c;
                break;
            }
        }

        if (selectedIndex != NO_POSITION) {
            scrollToSelectedMonth(selectedIndex);
        }
    }

    public void fixDialogDimens() {
        Logr.d("Fixing dimensions to h = %d / w = %d", getMeasuredHeight(), getMeasuredWidth());
        // Fix the layout height/width after the dialog has been shown.
        getLayoutParams().height = getMeasuredHeight();
        getLayoutParams().width = getMeasuredWidth();
        // Post this runnable so it runs _after_ the dimen changes have been applied/re-measured.
        post(new Runnable() {
            @Override
            public void run() {
                Logr.d("Dimens are fixed: now scroll to the selected date");
                scrollToSelectedDates();
            }
        });
    }

    /**
     * This method should only be called if the calendar is contained in a dialog, and it should only
     * be called when the screen has been rotated and the dialog should be re-measured.
     */
    public void unfixDialogDimens() {
        Logr.d("Reset the fixed dimensions to allow for re-measurement");
        // Fix the layout height/width after the dialog has been shown.
        getLayoutParams().height = LayoutParams.MATCH_PARENT;
        getLayoutParams().width = LayoutParams.MATCH_PARENT;
        requestLayout();
    }

    public List<Date> getSelectedDates() {
        List<Date> selectedDates = new ArrayList<>();
        for (Calendar cal : selectedCals) {
            selectedDates.add(new Date(cal.getTimeInMillis()));
        }

        Collections.sort(selectedDates);
        return selectedDates;
    }

    static void setMidnight(Calendar cal) {
        cal.set(HOUR_OF_DAY, 0);
        cal.set(MINUTE, 0);
        cal.set(SECOND, 0);
        cal.set(MILLISECOND, 0);
    }

    private class CellClickedListener implements MonthView.Listener {
        @Override
        public void handleClick(MonthCellDescriptor cell) {
            Date clickedDate = new Date(cell.getTime());

            if (cellClickInterceptor != null && cellClickInterceptor.onCellClicked(clickedDate)) {
                return;
            }
            if (!betweenDates(clickedDate, minCal, maxCal) || !isDateSelectable(clickedDate)) {
                if (invalidDateListener != null) {
                    invalidDateListener.onInvalidDateSelected(clickedDate);
                }
            } else {
                boolean wasSelected = doSelectDate(clickedDate, true);

                if (dateListener != null) {
                    if (wasSelected) {
                        dateListener.onDateSelected(clickedDate);
                    } else {
                        dateListener.onDateUnselected(clickedDate);
                    }
                }
            }
        }
    }

    private boolean selectDate(Date date) {
        if (betweenDates(date, minCal, maxCal)) {
            return doSelectDate(date, false);
        }
        return false;
    }

    private boolean doSelectDate(Date date, boolean isValidate) {
        Calendar newlySelectedCal = Calendar.getInstance(locale);
        newlySelectedCal.setTime(date);
        // Sanitize input: clear out the hours/minutes/seconds/millis.
        setMidnight(newlySelectedCal);

        switch (selectionMode) {
            case SelectionMode.RANGE:
                if (selectedCals.size() > 1) {
                    // We've already got a range selected: clear the old one.
                    clearOldSelections();
                } else if (selectedCals.size() == 1 && newlySelectedCal.before(selectedCals.get(0))) {
                    // We're moving the start of the range back in time: clear the old start date.
                    clearOldSelections();
                } else if (selectedCals.size() == 1 && newlySelectedCal.equals(selectedCals.get(0))) {
                    // no need perform action when user select current
                    return false;
                }
                break;

            case SelectionMode.MULTIPLE:
                date = applyMultiSelect(newlySelectedCal);
                break;

            case SelectionMode.SINGLE:
                clearOldSelections();
                break;
        }

        if (date != null) {
            selectedCals.add(newlySelectedCal);
        }

        // Update the adapter.
        if (isValidate) {
            validateAndUpdate();
        }
        return date != null;
    }

    private void clearOldSelections() {
        selectedCals.clear();
    }

    private Date applyMultiSelect(Calendar selectedCal) {
        if (!restrictedCals.contains(selectedCal.getTimeInMillis())) {
            return null;
        }

        for (Calendar cal : selectedCals) {
            if (sameDate(cal, selectedCal)) {
                selectedCals.remove(cal);
                return null;
            }
        }

        return selectedCal.getTime();
    }

    public void clearSelectedDates() {
        clearOldSelections();
        validateAndUpdate();
    }

    private class MonthAdapter extends RecyclerView.Adapter<MonthViewHolder> {

        @Override
        public int getItemCount() {
            return totalMonth;
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @NonNull
        @Override
        public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MonthView monthView = MonthView.create(parent, LayoutInflater.from(parent.getContext()), weekdayNameFormat, listener, today,
                    dayBackgroundResId, dayTextColorResId, titleTextStyle,
                    displayDayNamesHeaderRow, displayAlwaysDigitNumbers,
                    styleResourcesProvider, locale, dayViewAdapter);
            return new MonthViewHolder(monthView);
        }

        @Override
        public void onBindViewHolder(@NonNull MonthViewHolder holder, int position) {
            MonthView monthView = (MonthView) holder.itemView;

            // calculate current month
            monthCounter.setTime(minCal.getTime());
            monthCounter.add(MONTH, position);
            Date date = monthCounter.getTime();

            // cap word month name
            String monthLabel = monthNameFormat.format(date);
            if (!TextUtils.isEmpty(monthLabel) && Character.isLowerCase(monthLabel.charAt(0))) {
                monthLabel = Character.toUpperCase(monthLabel.charAt(0)) + monthLabel.substring(1);
            }
            MonthDescriptor month = new MonthDescriptor(monthCounter.get(MONTH), monthCounter.get(YEAR), monthLabel);
            monthView.init(month, getMonthCells(month, monthCounter), displayOnly);
        }
    }

    private static class MonthViewHolder extends ViewHolder {
        private MonthViewHolder(View itemView) {
            super(itemView);
        }
    }

    private List<List<MonthCellDescriptor>> getMonthCells(MonthDescriptor month, Calendar startCal) {
        Calendar cal = Calendar.getInstance(locale);
        cal.setTime(startCal.getTime());
        List<List<MonthCellDescriptor>> cells = new ArrayList<>();
        cal.set(DAY_OF_MONTH, 1);
        int firstDayOfWeek = cal.get(DAY_OF_WEEK);
        int offset = cal.getFirstDayOfWeek() - firstDayOfWeek;
        if (offset > 0) {
            offset -= 7;
        }
        cal.add(Calendar.DATE, offset);

        Calendar minSelectedCal = minDate(selectedCals);
        Calendar maxSelectedCal = maxDate(selectedCals);

        while ((cal.get(MONTH) < month.getMonth() + 1 || cal.get(YEAR) < month.getYear()) //
                && cal.get(YEAR) <= month.getYear()) {
            Logr.d("Building week row starting at %s", cal.getTime());
            List<MonthCellDescriptor> weekCells = new ArrayList<>();
            cells.add(weekCells);
            for (int c = 0; c < 7; c++) {
                boolean isCurrentMonth = cal.get(MONTH) == month.getMonth();

                boolean isSelected = isCurrentMonth && containsDate(selectedCals, cal);
                boolean isSelectable = (selectionMode == SelectionMode.MULTIPLE && !restrictedCals.isEmpty())
                        ? restrictedCals.contains(cal.getTimeInMillis())
                        : (isCurrentMonth && betweenDates(cal, minCal, maxCal) && isDateSelectable(cal.getTime()));

                boolean isToday = sameDate(cal, today);

                byte rangeState = RangeState.NONE;
                if (selectedCals.size() > 1 && isCurrentMonth && selectionMode != SelectionMode.MULTIPLE) {
                    if (sameDate(minSelectedCal, cal)) {
                        if (c != 6) {
                            // if min calendar at the end of week should no mark first
                            rangeState = RangeState.FIRST;
                        }
                    } else if (sameDate(maxDate(selectedCals), cal)) {
                        if (c != 0) {
                            // if max calendar at the start of week should no mark first
                            rangeState = RangeState.LAST;
                        }
                    } else if (betweenDates(cal, minSelectedCal, maxSelectedCal)) {
                        // check start and end of week to draw background
                        if (c == 0) {
                            rangeState = RangeState.START_WEEK;
                        } else if (c == 6) {
                            rangeState = RangeState.END_WEEK;
                        } else {
                            rangeState = RangeState.MIDDLE;
                        }
                    }
                }

                weekCells.add(new MonthCellDescriptor(cal.getTimeInMillis(), isCurrentMonth, isSelectable, isSelected, isToday, isCurrentMonth ? cal.get(DAY_OF_MONTH) : 0, rangeState));
                if (isCurrentMonth && selectionMode != SelectionMode.MULTIPLE) {
                    if (cal.get(DAY_OF_MONTH) == cal.getActualMaximum(DAY_OF_MONTH) && (rangeState == RangeState.FIRST
                            || rangeState == RangeState.MIDDLE || rangeState == RangeState.START_WEEK)) {
                        // if first day of month is last selected day, should mark all next item
                        for (c = c + 1; c < 7; c++) {
                            weekCells.add(new MonthCellDescriptor(cal.getTimeInMillis(), false, false, false, isToday, 0, c == 6 ? RangeState.END_WEEK : RangeState.MIDDLE));
                            cal.add(DATE, 1);
                        }
                        break;
                    } else if (cal.get(DAY_OF_MONTH) == cal.getActualMinimum(DAY_OF_MONTH) && (rangeState == RangeState.LAST
                            || rangeState == RangeState.MIDDLE || rangeState == RangeState.END_WEEK)) {
                        // if last day selected is start of month should mark range state for previous item
                        for (int i = 0; i < c; i++) {
                            weekCells.get(i).setRangeState(i == 0 ? RangeState.START_WEEK : RangeState.MIDDLE);
                        }
                    }
                }
                cal.add(DATE, 1);
            }
        }
        return cells;
    }

    private boolean containsDate(List<Calendar> selectedCals, Calendar cal) {
        for (Calendar selectedCal : selectedCals) {
            if (sameDate(cal, selectedCal)) {
                return true;
            }
        }
        return false;
    }

    private Calendar minDate(List<Calendar> selectedCals) {
        if (selectedCals == null || selectedCals.size() == 0) {
            return null;
        }
        return Collections.min(selectedCals);
    }

    private Calendar maxDate(List<Calendar> selectedCals) {
        if (selectedCals == null || selectedCals.size() == 0) {
            return null;
        }
        return Collections.max(selectedCals);
    }

    private boolean sameDate(Calendar cal, Calendar selectedDate) {
        return cal.get(MONTH) == selectedDate.get(MONTH)
                && cal.get(YEAR) == selectedDate.get(YEAR)
                && cal.get(DAY_OF_MONTH) == selectedDate.get(DAY_OF_MONTH);
    }

    private boolean betweenDates(Calendar cal, Calendar minCal, Calendar maxCal) {
        final Date date = cal.getTime();
        return betweenDates(date, minCal, maxCal);
    }

    private boolean betweenDates(Date date, Calendar minCal, Calendar maxCal) {
        final Date min = minCal.getTime();
        return (date.equals(min) || date.after(min)) // >= minCal
                && date.before(maxCal.getTime()); // && < maxCal
    }

    private boolean sameMonth(Calendar first, Calendar second) {
        return (first.get(MONTH) == second.get(MONTH) && first.get(YEAR) == second.get(YEAR));
    }

    private boolean isDateSelectable(Date date) {
        return dateConfiguredListener == null || dateConfiguredListener.isDateSelectable(date);
    }

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        dateListener = listener;
    }

    /**
     * Set a listener to react to user selection of a disabled date.
     *
     * @param listener the listener to set, or null for no reaction
     */
    public void setOnInvalidDateSelectedListener(OnInvalidDateSelectedListener listener) {
        invalidDateListener = listener;
    }

    /**
     * Set a listener used to discriminate between selectable and unselectable dates. Set this to
     * disable arbitrary dates as they are rendered.
     * <p>
     * Important: set this before you call {@link #init(Date, Date)} methods.  If called afterwards,
     * it will not be consistently applied.
     */
    public void setDateSelectableFilter(DateSelectableFilter listener) {
        dateConfiguredListener = listener;
    }

    /**
     * Set an adapter used to initialize {@link CalendarCellView} with custom layout.
     * <p>
     * Important: set this before you call {@link #init(Date, Date)} methods.  If called afterwards,
     * it will not be consistently applied.
     */
    public void setCustomDayView(DayViewAdapter dayViewAdapter) {
        this.dayViewAdapter = dayViewAdapter;
        if (null != adapter) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Set a listener to intercept clicks on calendar cells.
     */
    public void setCellClickInterceptor(CellClickInterceptor listener) {
        cellClickInterceptor = listener;
    }

    /**
     * Interface to be notified when a new date is selected or unselected. This will only be called
     * when the user initiates the date selection.  If you call {@link #selectDate(Date)} this
     * listener will not be notified.
     *
     * @see #setOnDateSelectedListener(OnDateSelectedListener)
     */
    public interface OnDateSelectedListener {
        void onDateSelected(Date date);

        void onDateUnselected(Date date);
    }

    /**
     * Interface to be notified when an invalid date is selected by the user. This will only be
     * called when the user initiates the date selection. If you call {@link #selectDate(Date)} this
     * listener will not be notified.
     *
     * @see #setOnInvalidDateSelectedListener(OnInvalidDateSelectedListener)
     */
    public interface OnInvalidDateSelectedListener {
        void onInvalidDateSelected(Date date);
    }

    /**
     * Interface used for determining the selectability of a date cell when it is configured for
     * display on the calendar.
     *
     * @see #setDateSelectableFilter(DateSelectableFilter)
     */
    public interface DateSelectableFilter {
        boolean isDateSelectable(Date date);
    }

    /**
     * Interface to be notified when a cell is clicked and possibly intercept the click.  Return true
     * to intercept the click and prevent any selections from changing.
     *
     * @see #setCellClickInterceptor(CellClickInterceptor)
     */
    public interface CellClickInterceptor {
        boolean onCellClicked(Date date);
    }
}
