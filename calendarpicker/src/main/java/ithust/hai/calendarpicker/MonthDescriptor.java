
package ithust.hai.calendarpicker;

class MonthDescriptor {
    private final int month;
    private final int year;
    private String label;

    MonthDescriptor(int month, int year, String label) {
        this.month = month;
        this.year = year;
        this.label = label;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "MonthDescriptor{"
                + "label='"
                + label
                + '\''
                + ", month="
                + month
                + ", year="
                + year
                + '}';
    }
}
