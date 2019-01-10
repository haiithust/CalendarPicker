package ithust.hai.calendarpicker;

/**
 * The range state of a cell for {@link MonthCellDescriptor} and {@link CalendarCellView}
 */
public interface RangeState {
    byte NONE = 0;
    byte FIRST = 1;
    byte MIDDLE = 2;
    byte LAST = 3;
    byte START_WEEK = 4;
    byte END_WEEK = 5;
}
