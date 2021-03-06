
package ithust.hai.calendarpicker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * ViewGroup that draws a grid of calendar cells.  All children must be {@link CalendarRowView}s.
 * The first row is assumed to be a header and no divider is drawn above it.
 */
public class CalendarGridView extends ViewGroup {
    private int oldWidthMeasureSize;
    private int oldNumRows;

    public CalendarGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDayViewAdapter(DayViewAdapter adapter) {
        for (int i = 0; i < getChildCount(); i++) {
            ((CalendarRowView) getChildAt(i)).setDayViewAdapter(adapter);
        }
    }

    public void setDayBackground(int resId) {
        for (int i = 0; i < getChildCount(); i++) {
            ((CalendarRowView) getChildAt(i)).setCellBackground(resId);
        }
    }

    public void setDayTextColor(int resId) {
        for (int i = 0; i < getChildCount(); i++) {
            ColorStateList colors = getResources().getColorStateList(resId);
            ((CalendarRowView) getChildAt(i)).setCellTextColor(colors);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Logr.d("Grid.onMeasure w=%s h=%s", MeasureSpec.toString(widthMeasureSpec),
                MeasureSpec.toString(heightMeasureSpec));
        int widthMeasureSize = MeasureSpec.getSize(widthMeasureSpec);
        if (oldWidthMeasureSize == widthMeasureSize) {
            Logr.d("SKIP Grid.onMeasure");
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
            return;
        }
        long start = System.currentTimeMillis();
        oldWidthMeasureSize = widthMeasureSize;
        int totalHeight = 0;
        final int rowWidthSpec = makeMeasureSpec(widthMeasureSize, EXACTLY);
        // Most cells are gonna be cellSize tall, but we want to allow custom cells to be taller.
        final int rowHeightSpec = makeMeasureSpec(widthMeasureSize, AT_MOST);
        for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
            View child = getChildAt(c);
            if (child.getVisibility() == View.VISIBLE) {
                measureChild(child, rowWidthSpec, rowHeightSpec);
                totalHeight += child.getMeasuredHeight();
            }
        }
        setMeasuredDimension(widthMeasureSize, totalHeight);
        Logr.d("Grid.onMeasure %d ms", System.currentTimeMillis() - start);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        long start = System.currentTimeMillis();
        top = 0;
        int rowHeight = 0;
        for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
            if (rowHeight == 0) {
                rowHeight = getChildAt(c).getMeasuredHeight();
            }
            getChildAt(c).layout(left, top, right, top + rowHeight);
            top += rowHeight;
        }
        Logr.d("Grid.onLayout %d ms", System.currentTimeMillis() - start);
    }

    public void setNumRows(int numRows) {
        if (oldNumRows != numRows) {
            // If the number of rows changes, make sure we do a re-measure next time around.
            oldWidthMeasureSize = 0;
        }
        oldNumRows = numRows;
    }
}
