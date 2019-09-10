package me.datalight.outside.gson;

public class ResponseCoin extends Response {
    public String getCoin() {
        return Coin;
    }

    private String Coin;

    public int getGlobal_curr_market_cap_usd() {
        int market_cap_usd =0;
        return (market_cap_usd)/1_000_000;
    }

    private long Global_curr_market_cap_usd;
}
