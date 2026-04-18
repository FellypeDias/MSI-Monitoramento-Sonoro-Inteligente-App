package com.example.msi.ui.inicio;

import androidx.annotation.DrawableRes;

public final class HistoryEvent {
    private final String title;
    private final String time;
    @DrawableRes
    private final int leftDotDrawable;
    @DrawableRes
    private final int rightDotDrawable;

    public HistoryEvent(String title, String time, @DrawableRes int leftDotDrawable, @DrawableRes int rightDotDrawable) {
        this.title = title;
        this.time = time;
        this.leftDotDrawable = leftDotDrawable;
        this.rightDotDrawable = rightDotDrawable;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public int getLeftDotDrawable() {
        return leftDotDrawable;
    }

    public int getRightDotDrawable() {
        return rightDotDrawable;
    }
}
