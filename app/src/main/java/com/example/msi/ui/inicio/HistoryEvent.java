package com.example.msi.ui.inicio;

public final class HistoryEvent {
    private final String title;
    private final String time;
    private final int leftDotResId;
    private final int rightDotResId;

    public HistoryEvent(String title, String time, int leftDotResId, int rightDotResId) {
        this.title = title;
        this.time = time;
        this.leftDotResId = leftDotResId;
        this.rightDotResId = rightDotResId;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public int getLeftDotResId() {
        return leftDotResId;
    }

    public int getRightDotResId() {
        return rightDotResId;
    }
}

