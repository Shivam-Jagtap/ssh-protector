package com.shivam.ban;

import com.shivam.event.FailedLoginEvent;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// executing IP table ban require root previlages or atleast for the user running the application
public class IpTableBanExecutor implements BanExecutor{

    private static final int COMMAND_TIMEOUT_SECONDS = 5;
    private static final Logger log = LoggerFactory.getLogger(IpTableBanExecutor.class);

    @Override
    public void ban(String ip) throws IOException, InterruptedException {
        runCommand("iptables","-I","INPUT","-s",ip,"-j","DROP");
    }

    @Override
    public void unBan(String ip) throws IOException, InterruptedException {
        runCommand("iptables", "-D", "INPUT", "-s", ip, "-j", "DROP");
    }

    private void runCommand(String... command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        try {
            Process p = pb.start();

            // check for how much time this process will take ,if it exceeds a lot then close forcibly
            boolean finished =  p.waitFor(COMMAND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if(!finished){
                p.destroyForcibly();
            }
            int exitCode = p.waitFor();

            if(exitCode == 0){
                log.debug("Command succeeded: {} ",String.join(" ",command));
            }else{
                log.warn("Command failed (exit {}): {} — output: {}",
                        exitCode, String.join(" ", command));

            }
        } catch (IOException e) {
            log.error("Failed to execute command: {} — {}", String.join(" ", command), e.getMessage());
            e.printStackTrace();
            throw e;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Command execution interrupted: {}", String.join(" ", command));
            e.printStackTrace();
            throw  e;
        }
    }
}
