package com.shivam.ban;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class NfTableBanExecutor implements BanExecutor{

    private static final int COMMAND_TIMEOUT_SECONDS = 5;
    private static final Logger log = LoggerFactory.getLogger(IpTableBanExecutor.class);

    @Override
    public void ban(String ip) throws IOException, InterruptedException {
        runCommand( "nft",
                "add",
                "rule",
                "inet",
                "filter",
                "input",
                "ip",
                "saddr",
                ip,
                "drop");
    }

    @Override
    public void unBan(String ip) throws IOException, InterruptedException {

    }

    private void runCommand(String... command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);

            try {
                Process p = pb.start();

                boolean finished = p.waitFor(COMMAND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                if (!finished) {
                    p.destroyForcibly();
                    throw new IOException("Command timed out");
                }

                int exitCode = p.exitValue();

                if (exitCode == 0) {
                    log.debug("Command succeeded: {}", String.join(" ", command));
                } else {
                    log.warn("Command failed (exit {}): {}",
                            exitCode, String.join(" ", command));
                }

            } catch (IOException e) {
                log.error("Failed to execute command: {}", String.join(" ", command), e);
                throw e;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Command execution interrupted: {}", String.join(" ", command), e);
                throw e;
            }
        }
}

