package ithust.hai.calendarpicker;

/**
 * @author conghai on 1/10/19.
 */
public interface SelectionMode {
    /**
     * Only one date will be selectable.  If there is already a selected date and you select a new
     * one, the old date will be unselected.
     */
    byte SINGLE = 0;
    /**
     * Multiple dates will be selectable.  Selecting an already-selected date will un-select it.
     */
    byte MULTIPLE = 1;
    /**
     * Allows you to select a date range.  Previous selections are cleared when you either:
     * <ul>
     * <li>Have a range selected and select another date (even if it's in the current range).</li>
     * <li>Have one date selected and then select an earlier date.</li>
     * </ul>
     */
    byte RANGE = 2;
}
