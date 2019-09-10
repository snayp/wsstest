package me.datalight.outside.main;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.datalight.outside.BaseTest;
import me.datalight.outside.gson.RequestFilter;
import me.datalight.outside.gson.Response;
import me.datalight.outside.gson.ResponseCoin;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static java.lang.Thread.sleep;
import static me.datalight.outside.Utils.getCoinMarketCap;
import static me.datalight.outside.Utils.getCoinMarketCapBTCDominance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

public class MainTest extends BaseTest {

    @Test
    void testMainHypeTop10() throws Exception {
        RequestFilter filterDate = new RequestFilter();
        RequestFilter filterHype = new RequestFilter();
        String fieldCoin = "Coin";
        String telegramHype = "telegram.coin_hype_mood_24h_curr.hype";
        fields.add(fieldCoin);
        fields.add(telegramHype);
        request.setFields(fields);
        filterDate.setField("Date");
        filterDate.setOperator("gt");
        filterDate.setValues(Arrays.asList(System.currentTimeMillis()/1000));
        filterHype.setField("telegram.coin_hype_mood_24h_curr.hype");
        filterHype.setOperator("top");
        filterHype.setValues(Arrays.asList(10));
        filters.add(filterDate);
        filters.add(filterHype);
        request.setFilters(filters);

        String jsonRequest = gson.toJson(request);
        log.info("Request = " +jsonRequest);
        ws.sendText(jsonRequest);
        sleep((long) 300);
        log.info("Response = " + response.toString());
        Response jsonResponse = gson.fromJson(response.toString(), Response.class);

        assertThat("status is false", jsonResponse.status(), is(true));
        log.info("status = " + String.valueOf(jsonResponse.status()));
        try {
            assertThat("data has invalid array size", jsonResponse.getData().size(), is(equalTo(10)));
        } catch (AssertionError e) {
            session.sendMessage(channel, "data array size are not equals to top filter! 10 != " + String.valueOf(jsonResponse.getData().size()));
        }

        log.info("data array size = " + String.valueOf(jsonResponse.getData().size()));
    }

    @Test
    void testMainLineChartForBTC() throws Exception {
        RequestFilter filterDate = new RequestFilter();
        RequestFilter filterCoin = new RequestFilter();
        String fieldCoin = "Coin";
        String marketCapUsd = "coinmarketcap.coin.market_cap_usd";
        String telegramHype = "telegram.coin_hype_mood_daily.hype";
        fields.add(fieldCoin);
        fields.add(marketCapUsd);
        fields.add(telegramHype);
        request.setFields(fields);
        filterDate.setField("Date");
        filterDate.setOperator("gt");
        filterDate.setValues(Arrays.asList(System.currentTimeMillis()/1000));
        filterCoin.setField("Coin");
        filterCoin.setOperator("in");
        filterCoin.setValues(Arrays.asList("BTC"));
        filters.add(filterDate);
        filters.add(filterCoin);
        request.setFilters(filters);

        String jsonRequest = gson.toJson(request);
        log.info("Request = " +jsonRequest);
        ws.sendText(jsonRequest);
        sleep((long) 300);
        log.info("Response = " + response.toString());
        Response jsonResponse = gson.fromJson(response.toString(), Response.class);

        assertThat("status is false", jsonResponse.status(), is(true));
        log.info("status = " + String.valueOf(jsonResponse.status()));

        String firstData = String.valueOf(jsonResponse.getData().get(0));
        ResponseCoin coin = gson.fromJson(firstData, ResponseCoin.class);
        try {
            assertThat("Coin is not BTC", coin.getCoin(), equalTo("BTC"));
        } catch (AssertionError e) {
            session.sendMessage(channel, "Coin for on Main Page is not BTC! Coin = " + coin.getCoin());
        }
        log.info("Coin = " + coin.getCoin());
    }

    @Test
    void testMainMarketAgg() throws Exception {
        RequestFilter filterDate = new RequestFilter();
        RequestFilter filterCoin = new RequestFilter();
        String fieldCoin = "Coin";
        String globalMarketCapUsd = "coinmarketcap.marketcap_global_curr.market_cap_usd";
        String globalMarketCapChange = "coinmarketcap.marketcap_global_curr.total_market_cap_usd_change";
        fields.add(fieldCoin);
        fields.add(globalMarketCapUsd);
        fields.add(globalMarketCapChange);
        request.setFields(fields);
        filterDate.setField("Date");
        filterDate.setOperator("gt");
        filterDate.setValues(Arrays.asList(System.currentTimeMillis()/1000));
        filterCoin.setField("Coin");
        filterCoin.setOperator("in");
        filterCoin.setValues(Arrays.asList("MARKET_AGG"));
        filters.add(filterDate);
        filters.add(filterCoin);
        request.setFilters(filters);

        String jsonRequest = gson.toJson(request);
        log.info("Request = " + jsonRequest);;
        ws.sendText(jsonRequest);
        sleep((long) 500);
        log.info("Response = " + response.toString());
        Response jsonResponse = gson.fromJson(response.toString(), Response.class);

        assertThat("status is false", jsonResponse.status(), is(true));
        log.info("status = " + String.valueOf(jsonResponse.status()));

        String firstData = String.valueOf(jsonResponse.getData().get(0));
        ResponseCoin coin = gson.fromJson(firstData, ResponseCoin.class);
        try {
            assertThat("Coin is not MARKET_AGG", coin.getCoin(), equalTo("MARKET_AGG"));
        } catch (AssertionError e) {
            session.sendMessage(channel, "No MARKET_AGG coin! Coin = " + coin.getCoin());
        }
        log.info("Coin = " + coin.getCoin());

        JsonParser parser = new JsonParser();
        JsonElement jsonTree = parser.parse(firstData);

        if(jsonTree.isJsonObject()){
            JsonObject jsonObject = jsonTree.getAsJsonObject();

            JsonElement globalMarketCap = jsonObject.get("coinmarketcap.marketcap_global_curr.market_cap_usd");
            double ourCap = globalMarketCap.getAsFloat();
            double capCap = getCoinMarketCap();
            double errorcapCap = capCap/100;

            try {
                assertThat("our market cap has more than 1% difference with coinmarketcap", ourCap, is(closeTo(capCap, errorcapCap)));
            } catch (AssertionError e) {
                session.sendMessage(channel, "our market cap = " + ourCap + " has more than 1% difference with coinmarketcap = " + capCap);
            }

            JsonElement totalCapChange = jsonObject.get("coinmarketcap.marketcap_global_curr.total_market_cap_usd_change");
            boolean totalChange = totalCapChange.getAsBoolean();
            log.info("Total market cap change = " + totalChange );
            assertThat("Total market cap change not null", totalChange,  is(notNullValue()));
        }
    }

    @Test
    void testMainBTCDominance() throws Exception {
        RequestFilter filterDate = new RequestFilter();
        RequestFilter filterCoin = new RequestFilter();
        String fieldCoin = "Coin";
        String globalBTCDominance = "coinmarketcap.dominance_global_curr.btc_dominance";
        fields.add(fieldCoin);
        fields.add(globalBTCDominance);
        request.setFields(fields);
        filterDate.setField("Date");
        filterDate.setOperator("gt");
        filterDate.setValues(Arrays.asList(System.currentTimeMillis()/1000));
        filterCoin.setField("Coin");
        filterCoin.setOperator("in");
        filterCoin.setValues(Arrays.asList("MARKET_AGG"));
        filters.add(filterDate);
        filters.add(filterCoin);
        request.setFilters(filters);

        String jsonRequest = gson.toJson(request);
        log.info("Request = " + jsonRequest);
        ws.sendText(jsonRequest);
        sleep((long) 300);
        log.info("Response = " + response.toString());
        Response jsonResponse = gson.fromJson(response.toString(), Response.class);

        assertThat("status is false", jsonResponse.status(), is(true));
        log.info("status = " + String.valueOf(jsonResponse.status()));

        String firstData = String.valueOf(jsonResponse.getData().get(0));
        ResponseCoin coin = gson.fromJson(firstData, ResponseCoin.class);
        try {
            assertThat("Coin is not MARKET_AGG", coin.getCoin(), equalTo("MARKET_AGG"));
        } catch (AssertionError e) {
            session.sendMessage(channel, "Global coin MARKET_AGG not found = " + coin.getCoin());
        }
        log.info("Coin = " + coin.getCoin());

        JsonParser parser = new JsonParser();
        JsonElement jsonTree = parser.parse(firstData);

        if(jsonTree.isJsonObject()){
            JsonObject jsonObject = jsonTree.getAsJsonObject();

            JsonElement globalDoninamce = jsonObject.get("coinmarketcap.dominance_global_curr.btc_dominance");
            double ourBTCDoninamce = globalDoninamce.getAsFloat();
            double capBTCDoninamce = getCoinMarketCapBTCDominance();
            double errorCapBTCDoninamce = getCoinMarketCapBTCDominance()/100;
            try {
                assertThat("our BTC dominance has more than 1% difference with coinmarketcap", ourBTCDoninamce, is(closeTo(capBTCDoninamce,errorCapBTCDoninamce)));
            } catch (AssertionError e) {
                session.sendMessage(channel, "our BTC dominance  = " + ourBTCDoninamce + " has more than 1% difference with coinmarketcap = " + capBTCDoninamce);
            }
        }
    }
}
