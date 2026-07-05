package com.shivam.ban;

import com.shivam.config.DefaultConfig;
import com.shivam.event.FailedLoginEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// this algorithm will keep track of unsuccessful logins for a fixed time and ban the IPs
public class SlidingWindowDetectionStrategy implements DetectionStrategy{

    private static final Map<String, Deque<LocalDateTime>> ipTrack = new ConcurrentHashMap<>();

    public SlidingWindowDetectionStrategy() {}

    private final Logger log = LoggerFactory.getLogger(SlidingWindowDetectionStrategy.class);

    @Override
    public boolean recordFailureAndCheckThreshold(FailedLoginEvent event) {
        long timeWindowSeconds = DefaultConfig.getDefaultConfig().getTimeWindowSeconds();
        int maxAttempts = DefaultConfig.getDefaultConfig().getMaxFailedAttempts();

        boolean isImposterId = false;

        Deque<LocalDateTime> queue = null;
        String ip = event.getIp();
        String userName = event.getUsername();
        LocalDateTime eventTime = event.getTimestamp();

        if(ipTrack.containsKey(ip)){ // if ip exists
            queue = ipTrack.get(ip);
            if(queue.size() == maxAttempts){
                LocalDateTime firstEventTime = queue.peekLast();
                // check the time difference between first invalid attempt and current one
                long seconds = ChronoUnit.SECONDS.between(firstEventTime,eventTime);
                if(seconds >= timeWindowSeconds){
                    // ban this IP
                    isImposterId = true;
                }
                // remove last entry and
                queue.pollFirst();
            }
        }else{
            // create a new deque and initialize
            queue = new ArrayDeque<>();
            ipTrack.put(ip,queue);
        }
        // add the current event Time
        queue.addLast(eventTime);

        log.info("================The hashmap : {}",ipTrack);
        return isImposterId;
    }
}
