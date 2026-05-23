package com.shivam.logging;

import com.shivam.config.DefaultConfig;
import com.shivam.enums.FileTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/*
Responsiblities of this class -
1. Open file
2. Tail the file/journalD
3. Whenever new log is written, send to the subscribers
4. Maintain a list of listeners and also add or remove the listeners
 */
public class LogWatcher {
    private DefaultConfig config;

    public LogWatcher(DefaultConfig config) {
        this.config = config;
    }

    private static final Logger log = LoggerFactory.getLogger(LogWatcher.class);

    List<LogListener> logListeners = new CopyOnWriteArrayList<>();

    public void addListener(LogListener l){
        logListeners.add(l);
        log.debug("new listener registered {}",l.getClass().getSimpleName());
    }

    public void removeListener(LogListener r){
        logListeners.remove(r);
    }

    //-----------------core work-----------------
    public void watch(){
        if(config.getFileType().equals(FileTypeEnum.FILE)){
            tailFile(config.getLogFilePath());
        }else if(config.getFileType().equals(FileTypeEnum.JOURNALD)){
            tailJournalD();
        }else{
            throw new RuntimeException("The provided file type in the context is not currently supported ");
        }
    }

    private void tailFile(String path){
        FileState fileState = null;

        while(fileState == null){
            try {
                fileState = openFile(path);
            } catch (Exception e) {

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }

        log.info("Starting the tailing of file for path : {} ",path);
        log.info("FileState object : {}",fileState);
        while(true){
           RandomAccessFile file = fileState.file;
           Object currentFileKey = fileState.currentFileKey;
           Long filePointer = fileState.filePointer;
            try{
                // check if fileRotated
                if(isFileRotated(currentFileKey,path)){
                    // set this file to the new file
                    // close file and reopen file

                    try{
                        file.close();

                        // open file and assign things
                        fileState = openFile(path);
                    }catch (Exception e){
                        log.error("Couldn't close and reopen the new file");
                    }
                }

                // reinitialize in case if file was reopened and our object was reinitialized
                file = fileState.file;
                currentFileKey = fileState.currentFileKey;
                filePointer = fileState.filePointer;

                Long fileLength = file.length(); // current length of file
                // new log appened
                if(fileLength > filePointer){
                    file.seek(filePointer);
                    String line = null;

                    while((line = file.readLine()) != null){
                        notifyListeners(line);
                        log.info("New log appended is : {}",line);
                    }

                    fileState.filePointer = file.getFilePointer();
                }else if(fileLength < filePointer){
                    // file truncated
                    fileState.filePointer = 0L;
                }

                Thread.sleep(1000);
            }catch(Exception e){
                log.error("Error while tailing the file");
                e.printStackTrace();
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private static class FileState {

        private RandomAccessFile file;

        private Object currentFileKey;

        private long filePointer;

        public FileState(RandomAccessFile file,
                         Object currentFileKey,
                         long filePointer) {

            this.file = file;
            this.currentFileKey = currentFileKey;
            this.filePointer = filePointer;
        }

        public String toString(){

            return "file : "+file.toString()+" currentFileKey : "+currentFileKey.toString()+" filePointer : "+filePointer;
        }
    }

    // opens the given file and assigns the given reference to the opened file and returns files length
    private FileState openFile(String path) {
        try{
            RandomAccessFile file = new RandomAccessFile(path,"r");
            Object fileKey = Files.readAttributes(Path.of(path),BasicFileAttributes.class).fileKey();
            Long filePointer = file.length();

            return new FileState(file,fileKey,filePointer);
        }catch(Exception e){
            log.error("Error while opening the file with path {}",path);
            throw new RuntimeException("Error while opening logfile");
        }
    }

    private boolean isFileRotated(Object currentFileKey, String path) throws IOException {
        Object latestFileKey = Files.readAttributes(Path.of(path), BasicFileAttributes.class).fileKey();
        return !currentFileKey.equals(latestFileKey) ;
    }

    //-------------------for journalD type logs---------------
    private void tailJournalD(){
        while(true){
            Process process = null;

            try{
                ProcessBuilder processBuilder = new ProcessBuilder("journalctl","-u","ssh","-f","-n","0","--output=short");
                processBuilder.redirectErrorStream(true);
                process = processBuilder.start();

                log.info("started the journalctl process");

                // reading the logs
                try(BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream())
                )){
                    String line ;
                    while((line = reader.readLine()) != null){
                        log.info("NEW JOURNALD LOG : {}",line);
                        notifyListeners(line);
                    }
                }

                int exitCode = process.waitFor();
                log.error("journalctl exited with code : {}",exitCode);

            }catch(Exception e){
                log.error("Exception when trying to read journald ",e);
            }finally{
                if(process != null){
                    process.destroy();
                }
            }

            // retry after failure
            try{
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // set the interrupt flag as true as java sets it to false after interruptException if someone interrupts and wakes this thread before the timer ends
                e.printStackTrace();
                return;
            }
        }
    }

    //------------notify to the listeners-------
    private void notifyListeners(String line){
        for(LogListener l : logListeners){
            try{
                l.processLog(line);
            }catch (Exception e){
                log.error("Error while notifying the listener {} , the message {} ",l.getClass().getSimpleName(),line);
            }
        }
    }

}
