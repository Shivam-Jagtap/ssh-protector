package com.shivam.config;

public class DefaultConfig {
    private int maxFailedAttempts;
    private int timeWindowSeconds;
    private int banDurationMinuites;

    public DefaultConfig() {
    }

    public DefaultConfig(int maxFailedAttempts, int timeWindowSeconds, int banDurationMinuites) {
        this.maxFailedAttempts = maxFailedAttempts;
        this.timeWindowSeconds = timeWindowSeconds;
        this.banDurationMinuites = banDurationMinuites;
    }

    // this function will check if mandatory fields are set or not
    public boolean checkMandatoryFieldsSet(){
        return maxFailedAttempts != 0 && timeWindowSeconds != 0 && banDurationMinuites != 0;
    }

    public int getMaxFailedAttempts() {
        return maxFailedAttempts;
    }

    public void setMaxFailedAttempts(int maxFailedAttempts) {
        this.maxFailedAttempts = maxFailedAttempts;
    }

    public int getTimeWindowSeconds() {
        return timeWindowSeconds;
    }

    public void setTimeWindowSeconds(int timeWindowSeconds) {
        this.timeWindowSeconds = timeWindowSeconds;
    }

    public int getBanDurationMinuites() {
        return banDurationMinuites;
    }

    public void setBanDurationMinuites(int banDurationMinuites) {
        this.banDurationMinuites = banDurationMinuites;
    }

    @Override
    public String toString() {
        return "DefaultConfig{" +
                "maxFailedAttempts=" + maxFailedAttempts +
                ", timeWindowSeconds=" + timeWindowSeconds +
                ", banDurationMinuites=" + banDurationMinuites +
                '}';
    }
}
