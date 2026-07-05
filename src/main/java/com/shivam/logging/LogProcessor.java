package com.shivam.logging;

import com.shivam.ban.BanManager;
import com.shivam.event.FailedLoginEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// this class is the main implementation to get the IP and ban it
public class LogProcessor implements LogListener{
    private final BanManager banManager;

    public LogProcessor(BanManager banManager){
        this.banManager = banManager;
    }

    private static final Pattern FAILED_PASSWORD = Pattern.compile(
            "^(\\w+\\s+\\d+\\s+\\d+:\\d+:\\d+).*?Failed password for (?:invalid user )?(\\S+) from ([\\d.]+)"
    );

    private static final Pattern INVALID_USER = Pattern.compile(
            "^(\\w+\\s+\\d+\\s+\\d+:\\d+:\\d+).*?Invalid user (\\S+) from ([\\d.]+)"
    );


    Logger log = LoggerFactory.getLogger(LogProcessor.class);

    @Override
    public void processLog(String line) {
        if(line == null){
            log.error("Log is invalid");
        }

        if(isSshLog(line)){
            log.info("ssh log detected");
            process(line);
        }
    }

    private void process(String line){
        Matcher m1 = FAILED_PASSWORD.matcher(line);
        FailedLoginEvent f = null;

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("MMM d HH:mm:ss")
                .parseDefaulting(ChronoField.YEAR, Year.now().getValue())
                .toFormatter(Locale .ENGLISH);

        if(m1.find()){ // if found villain IP
            f = new FailedLoginEvent(m1.group(3),m1.group(2), LocalDateTime.parse(m1.group(1),formatter));
            banManager.process(f);
            return;
        }

        Matcher m2 = INVALID_USER.matcher(line);
        if(m2.find()){
            f = new FailedLoginEvent(m2.group(3),m2.group(2),LocalDateTime.parse(m2.group(1),formatter));
            banManager.process(f);
            return;
        }

    }

    // check if the given line is related to ssh or not
    private boolean isSshLog(String log){
        return log.contains("sshd");
    }
}