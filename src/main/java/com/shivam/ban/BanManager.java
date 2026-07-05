package com.shivam.ban;

import com.shivam.event.FailedLoginEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BanManager {

    private final BanExecutor banExecutor;

    private final DetectionStrategy detectionStrategy;

    private static final Logger log= LoggerFactory.getLogger(BanManager.class);

    public BanManager(BanExecutor banExecutor, DetectionStrategy detectionStrategy){
        this.banExecutor = banExecutor;
        this.detectionStrategy = detectionStrategy;
    }

    public void process(FailedLoginEvent event){
        boolean isImposter = detectionStrategy.recordFailureAndCheckThreshold(event);
        if(isImposter){
            try{
                log.info("BANNING THE IP : {} ",event.getIp());
                banExecutor.ban(event.getIp());
            }catch (Exception e){
                log.error("Could not ban ip {} because of some error ",event.getIp());
            }
        }
    }
}
