package me.datalight.outside.gson;

public class RequestWithLimit extends Request {

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    private int limit;
}
