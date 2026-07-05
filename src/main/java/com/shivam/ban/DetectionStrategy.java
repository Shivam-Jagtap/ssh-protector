package com.shivam.ban;

import com.shivam.event.FailedLoginEvent;

// this includes algorithm to decide if we currently want to ban the given IP or not
public interface DetectionStrategy {

    boolean recordFailureAndCheckThreshold(FailedLoginEvent event);
}
