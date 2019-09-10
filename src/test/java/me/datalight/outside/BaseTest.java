package me.datalight.outside;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import me.datalight.outside.gson.Request;
import me.datalight.outside.gson.RequestFilter;
import org.apache.log4j.PropertyConfigurator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static me.datalight.outside.Utils.connect;

public class BaseTest {

    protected Logger log = LoggerFactory.getLogger(getClass());

    protected static WebSocket ws;

    protected static Gson gson = new Gson();

    protected JsonObject response;
    protected Request request = new Request();
    protected List<RequestFilter> filters = new ArrayList<>();
    protected List<String> fields = new ArrayList<>();
    protected static SlackSession session;
    protected static SlackChannel channel;


    @BeforeAll
    public static void setupAll() throws Exception {
        PropertyConfigurator.configure("src/main/resources/log4j.properties");

//;
    }

    @BeforeEach
    void setupListner() throws Exception {
        ws = connect();
        ws.addListener(new WebSocketAdapter() {
            // A text message arrived from the server.
            @Override
            public void onTextMessage(WebSocket websocket, String message) {
            JsonParser parser = new JsonParser();
            JsonElement jsonTree = parser.parse(message);

            if(jsonTree.isJsonObject()){
                JsonObject jsonObject = jsonTree.getAsJsonObject();
                response = jsonObject;
            }
            }
        });
    }
}
