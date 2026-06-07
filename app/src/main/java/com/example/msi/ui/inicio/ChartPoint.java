package com.example.msi.ui.inicio;

public final class ChartPoint {
    private final String label;
    private final int value;

    public ChartPoint(String label, int value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public int getValue() {
        return value;
    }
}

