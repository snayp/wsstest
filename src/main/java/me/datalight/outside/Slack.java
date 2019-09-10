package me.datalight.outside;

import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

import java.io.IOException;

public class Slack {
    public static SlackSession connect() throws IOException {
        SlackSession session = SlackSessionFactory.createWebSocketSlackSession("");
        session.connect();
        return session;
    }
}
