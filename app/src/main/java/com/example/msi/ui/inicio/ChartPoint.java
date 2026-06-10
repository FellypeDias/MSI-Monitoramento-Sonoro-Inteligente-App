package com.example.msi.ui.inicio;

public final class ChartPoint {
    private final String label;
    private final int value;
    private final int count;

    public ChartPoint(String label, int value, int count) {
        this.label = label;
        this.value = value;
        this.count = count;
    }

    public String getLabel() {
        return label;
    }

    public int getValue() {
        return value;
    }

    public int getCount() {
        return count;
    }
}
