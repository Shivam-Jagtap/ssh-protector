package com.shivam.config;

import com.shivam.enums.FileTypeEnum;

// This is a singelton POJO for configuration
public class DefaultConfig {
    private int maxFailedAttempts;
    private long timeWindowSeconds;
    private int banDurationMinuites;
    private String logFilePath;
//    private Boolean isJournalD;
    private FileTypeEnum fileType  = FileTypeEnum.FILE;

    static DefaultConfig defaultConfig;

    private DefaultConfig() {}

    public static DefaultConfig getDefaultConfig() {
        if(defaultConfig != null) return defaultConfig;
        else{
            synchronized (DefaultConfig.class){
                if(defaultConfig == null){
                    defaultConfig = new DefaultConfig();
                }
                return defaultConfig;
            }
        }
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

    public long getTimeWindowSeconds() {
        return timeWindowSeconds;
    }

    public void setTimeWindowSeconds(long timeWindowSeconds) {
        this.timeWindowSeconds = timeWindowSeconds;
    }

    public int getBanDurationMinuites() {
        return banDurationMinuites;
    }

    public void setBanDurationMinuites(int banDurationMinuites) {
        this.banDurationMinuites = banDurationMinuites;
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public FileTypeEnum getFileType() {
        return fileType;
    }

    public void setFileType(FileTypeEnum fileTypeEnum) {
        this.fileType = fileTypeEnum;
    }

    @Override
    public String toString() {
        return "DefaultConfig{" +
                "maxFailedAttempts=" + maxFailedAttempts +
                ", timeWindowSeconds=" + timeWindowSeconds +
                ", banDurationMinuites=" + banDurationMinuites +
                ", logFilePath='" + logFilePath + '\'' +
                '}';
    }
}
