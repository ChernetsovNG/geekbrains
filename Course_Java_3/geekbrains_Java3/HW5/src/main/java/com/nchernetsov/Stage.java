package com.nchernetsov;

public abstract class Stage {
    protected int length;
    protected String description;

    public abstract void go(Car car);

    public String getDescription() {
        return description;
    }
}
