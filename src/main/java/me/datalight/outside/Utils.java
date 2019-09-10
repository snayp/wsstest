package me.datalight.outside;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import io.restassured.response.Response;

import javax.net.ssl.SSLContext;

import static io.restassured.RestAssured.get;

public class Utils {
    private static float coinMarketCap;
    private static float coinMarketCapBTCDominance;

    private static final String SERVER = "wss://*******/ws";
//    private static final String SERVER = "wss://*********/ws";
    //wss://outside.********/ws
    //wss://**********прод/ws

    /**
     * The timeout value in milliseconds for socket connection.
     */
    private static final int TIMEOUT = 5000;

    /**
     * Connect to the server.
     */
    static WebSocket connect() throws Exception
    {
        SSLContext context = NaiveSSLContext.getInstance("TLS");
        return new WebSocketFactory()
                .setSSLContext(context)
                .setConnectionTimeout(TIMEOUT)
                .createSocket(SERVER)
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                .connect();
    }

    public static float getCoinMarketCap() {
        Response response = get("https://api.coinmarketcap.com/v2/global/");
        coinMarketCap = response.jsonPath().get("data.quotes.USD.total_market_cap");
        return coinMarketCap;
    }

    public static float getCoinMarketCapBTCDominance() {
        Response response = get("https://api.coinmarketcap.com/v2/global/");
        coinMarketCapBTCDominance = response.jsonPath().get("data.bitcoin_percentage_of_market_cap");
        return coinMarketCapBTCDominance;
    }
}
