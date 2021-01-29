package ithust.hai.calendarpicker;

import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

public class StyleResourcesProvider {

    public StyleResourcesProvider(@DrawableRes int defaultDateSelected,
                                  @DrawableRes int startingDateSelected,
                                  @DrawableRes int endingDateSelected,
                                  @DrawableRes int dateStartWeekSelected,
                                  @DrawableRes int dateEndWeekSelected,
                                  @DrawableRes int dateMiddleWeekSelected) {
        this.defaultDateSelected = defaultDateSelected;
        this.startingDateSelected = startingDateSelected;
        this.endingDateSelected = endingDateSelected;
        this.dateStartWeekSelected = dateStartWeekSelected;
        this.dateEndWeekSelected = dateEndWeekSelected;
        this.dateMiddleWeekSelected = dateMiddleWeekSelected;
    }

    @DrawableRes
    private int defaultDateSelected;

    @DrawableRes
    private int startingDateSelected;

    @DrawableRes
    private int endingDateSelected;

    @DrawableRes
    private int dateStartWeekSelected;

    @DrawableRes
    private int dateEndWeekSelected;

    @DrawableRes
    private int dateMiddleWeekSelected;

    int getDefaultDateSelected() {
        return defaultDateSelected;
    }

    int getStartingDateSelected() {
        return startingDateSelected;
    }

    int getEndingDateSelected() {
        return endingDateSelected;
    }

    int getDateStartWeekSelected() {
        return dateStartWeekSelected;
    }

    int getDateEndWeekSelected() {
        return dateEndWeekSelected;
    }

    int getDateMiddleWeekSelected() {
        return dateMiddleWeekSelected;
    }

    @NonNull
    public static StyleResourcesProvider createFromAttributes(@NonNull TypedArray typedArray) {
        int defaultDateSelected = typedArray.getResourceId(R.styleable.CalendarPickerView_cpv_defaultDateSelected,
                R.drawable.calendar_date_picker_selected);
        int startingDateSelected = typedArray.getResourceId(R.styleable.CalendarPickerView_cpv_startingDateSelected,
                R.drawable.calendar_date_picker_selected_left);
        int endingDateSelected = typedArray.getResourceId(R.styleable.CalendarPickerView_cpv_endingDateSelected,
                R.drawable.calendar_date_picker_selected_right);
        int dateStartWeekSelected = typedArray.getResourceId(R.styleable.CalendarPickerView_cpv_dateStartWeekSelected,
                R.drawable.calendar_date_picker_selected_start_week);
        int dateEndWeekSelected = typedArray.getResourceId(R.styleable.CalendarPickerView_cpv_dateEndWeekSelected,
                R.drawable.calendar_date_picker_selected_end_week);
        int dateMiddleWeekSelected = typedArray.getResourceId(R.styleable.CalendarPickerView_cpv_dateMiddleWeekSelected,
                R.drawable.calendar_date_picker_selected_middle);
        return new StyleResourcesProvider(defaultDateSelected,
                startingDateSelected, endingDateSelected,
                dateStartWeekSelected, dateEndWeekSelected, dateMiddleWeekSelected);
    }
}
