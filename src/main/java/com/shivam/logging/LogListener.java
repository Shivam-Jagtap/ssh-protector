package com.shivam.logging;

@FunctionalInterface
public interface LogListener {

    public void processLog(String line);
}
