
package ithust.hai.calendarpicker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * TableRow that draws a divider between each cell. To be used with {@link CalendarGridView}.
 */
public class CalendarRowView extends ViewGroup implements View.OnClickListener {
    private MonthView.Listener listener;

    public CalendarRowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        child.setOnClickListener(this);
        super.addView(child, index, params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int cellSize = totalWidth / 7;
        for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
            final View child = getChildAt(c);
            // Calculate width cells, making sure to cover totalWidth.
            child.measure(MeasureSpec.makeMeasureSpec(cellSize, MeasureSpec.EXACTLY)
                    , MeasureSpec.makeMeasureSpec(cellSize, MeasureSpec.AT_MOST));
        }

        setMeasuredDimension(cellSize, cellSize);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int cellHeight = bottom - top;
        for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
            final View child = getChildAt(c);
            child.layout(c * cellHeight, 0, c * cellHeight + cellHeight, cellHeight);
        }
    }

    @Override
    public void onClick(View v) {
        // Header rows don't have a click listener
        if (listener != null) {
            listener.handleClick((MonthCellDescriptor) v.getTag());
        }
    }

    public void setListener(MonthView.Listener listener) {
        this.listener = listener;
    }

    public void setDayViewAdapter(DayViewAdapter adapter) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof CalendarCellView) {
                CalendarCellView cell = ((CalendarCellView) getChildAt(i));
                cell.removeAllViews();
                adapter.makeCellView(cell);
            }
        }
    }

    public void setCellBackground(int resId) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setBackgroundResource(resId);
        }
    }

    public void setCellTextColor(int resId) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof CalendarCellView) {
                ((CalendarCellView) getChildAt(i)).getDayOfMonthTextView().setTextColor(resId);
            }
        }
    }

    public void setCellTextColor(ColorStateList colors) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof CalendarCellView) {
                ((CalendarCellView) getChildAt(i)).getDayOfMonthTextView().setTextColor(colors);
            }
        }
    }

    public void setTypeface(Typeface typeface) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof CalendarCellView) {
                ((CalendarCellView) getChildAt(i)).getDayOfMonthTextView().setTypeface(typeface);
            }
        }
    }
}
