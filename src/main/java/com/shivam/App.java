package com.shivam;


import com.shivam.config.DefaultConfig;
import com.shivam.helper.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the main starting point of the application
 *
 * We are asuming that at this point, our application has all the necessary permissions to run into the system and it can start working
 * Flow and working ->
 * 0. Load the config file if present or args
 * 1. Search for the ubuntu systems log file - as different versions have different path
 * 2. After finding the logs, watch those logs -> on every unsuccessful login attemp count it and note the IP
 *
 */
public class App 
{
    private static final Logger log = LoggerFactory.getLogger(App.class.getName());
    public static void main( String[] args )
    {
        log.info("=========================================");
        log.info("Starting the SSH-PROTECTOR app");
        log.info("=========================================");

        DefaultConfig config = new DefaultConfig();
        // set the config , highest priority to arguments then config file in system and then default config
        ConfigLoader.getConfigLoader().validateAndApplyConfigurations(args,config);
        log.info("The set config by the system is {} ",config);
    }
}
