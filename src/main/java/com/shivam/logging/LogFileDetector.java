package com.shivam.logging;

import com.shivam.config.DefaultConfig;
import com.shivam.enums.FileTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class LogFileDetector {

    private LogFileDetector(){}

    public static final Logger log = LoggerFactory.getLogger(LogFileDetector.class);

    private static final List<String> possiblePaths = new ArrayList<>();

    public static void detectLogFile(DefaultConfig config){
        List<String> SSH_LOG_PATHS =
                Arrays.asList(
                        "/var/log/auth.log",
                        "/var/log/secure",
                        "/var/log/messages",
                        "D:\\ssh-prot-fileTest/testlogfile2.txt",
                        "D:\\ssh-prot-fileTest/testlogfile.log"
                );

        log.info("Starting the logFileDetection task");
        if(config.getFileType() != null && config.getFileType().equals(FileTypeEnum.JOURNALD)){
            // if journald exists then set logfilepath to null;
            config.setLogFilePath(null);
            return;
        }
        if(config.getLogFilePath() != null){
            if(isValid(config.getLogFilePath())) return;
        }

        // check if journalD exists
        if(isJournalDAvailable()){
            log.info("JournalD is available in the system");
            config.setFileType(FileTypeEnum.JOURNALD);
            config.setLogFilePath(null);
            return;
        }else{
            log.info("journalD was not found in the system");
        }

        // traverse over each SSH_LOG_PATHS and check if file exists
        for(String logPath : SSH_LOG_PATHS) {
            log.info("Searching for path :{}", logPath);
            Path path = Path.of(logPath);
            // check if path exists and if it is readable
            if (Files.exists(path) && Files.isReadable(path)) {
                if (doesFileContainSSH(path)) {
                    // this is the required file then
                    config.setLogFilePath(String.valueOf(path));
                    log.info("Log file found with sshd");
                    return;
                } else {
                    possiblePaths.add(String.valueOf(path));
                }
            }
        }
        // if possible paths exists then check which one had the latest time stamp
        if(possiblePaths.isEmpty()){
            log.error("No valid log file was detected in the filesystem");
            throw new RuntimeException("Log file not detected, please add valid log file in the config file or program arguments and restart the application");
        }else if(possiblePaths.size() == 1){
            config.setLogFilePath(possiblePaths.get(0));
            return;
        }

        // now compare all the files in possible paths and set the latest one in the config
        checkListAndSetConfig(possiblePaths,config);

    }

    private static void checkListAndSetConfig(List<String> allPossiblePaths,DefaultConfig config){
        String latestPath = null;
        FileTime latestTime = null;

        for (String p : allPossiblePaths) {

            try {

                FileTime currentTime =
                        Files.getLastModifiedTime(Paths.get(p));

                if (latestTime == null
                        || currentTime.compareTo(latestTime) > 0) {

                    latestTime = currentTime;
                    latestPath = p;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config.setLogFilePath(latestPath);
    }

    private static boolean isJournalDAvailable(){
        Process p = null;
        try{
            p = new ProcessBuilder(
                    "journalctl",
                    "--version"
            ).start();
            int exitCode = p.waitFor();
            return exitCode == 0;
        }catch(IOException io){
            log.error("IOException while checking journald ");
            return false;
        }catch(InterruptedException ie){
            log.error("InterruptedException while checking journald ");
            return false;
        }finally {
            if(p != null) p.destroy();
        }
    }

    private static boolean doesFileContainSSH(Path path){
        try(Stream<String> lines = Files.lines(path)){
            return lines
                    .skip(Math.max(0,Files.lines(path).count()-100))
                    .anyMatch(
                            line -> line.toLowerCase().contains("sshd")
                    );
        } catch (IOException e) {
            log.error("Error while reading log file to check SSH exists or not");
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isValid(String path){
        Path filePath = Path.of(path);
        return Files.exists(filePath) && Files.isReadable(filePath) && Files.isRegularFile(filePath);
    }
}
