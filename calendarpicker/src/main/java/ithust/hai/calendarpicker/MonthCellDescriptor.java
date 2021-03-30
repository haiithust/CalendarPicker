

package ithust.hai.calendarpicker;

/**
 * Describes the state of a particular time cell in a {@link MonthView}.
 */
class MonthCellDescriptor {

    private final long time;
    private final int value;
    private final boolean isCurrentMonth;
    private final boolean isSelected;
    private final boolean isToday;
    private final boolean isSelectable;
    private final boolean isActive;
    private byte rangeState;

    MonthCellDescriptor(long time, boolean currentMonth, boolean selectable, boolean selected,
                        boolean today, int value, boolean isActive, byte rangeState) {
        this.time = time;
        isCurrentMonth = currentMonth;
        isSelectable = selectable;
        isSelected = selected;
        isToday = today;
        this.value = value;
        this.rangeState = rangeState;
        this.isActive = isActive;
    }

    public long getTime() {
        return time;
    }

    public boolean isCurrentMonth() {
        return isCurrentMonth;
    }

    public boolean isSelectable() {
        return isSelectable;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isToday() {
        return isToday;
    }

    public byte getRangeState() {
        return rangeState;
    }

    public void setRangeState(byte rangeState) {
        this.rangeState = rangeState;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "MonthCellDescriptor{"
                + "time="
                + time
                + ", value="
                + value
                + ", isCurrentMonth="
                + isCurrentMonth
                + ", isSelected="
                + isSelected
                + ", isToday="
                + isToday
                + ", isSelectable="
                + isSelectable
                + ", rangeState="
                + rangeState
                + '}';
    }
}
