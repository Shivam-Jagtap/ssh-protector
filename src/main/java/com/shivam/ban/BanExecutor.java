package com.shivam.ban;

import com.shivam.event.FailedLoginEvent;

import java.io.IOException;

public interface BanExecutor {

    void ban(String ip) throws IOException, InterruptedException;
    void unBan(String ip) throws IOException, InterruptedException;
}
