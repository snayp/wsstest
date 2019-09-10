package me.datalight.outside.gson;

import java.util.List;

public class Response {
    public boolean status() {
        return status;
    }

    public List getData() {
        return data;
    }

    private boolean status;
    private List data;
}
