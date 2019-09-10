package me.datalight.outside.gson;

import java.util.List;

public class Request {
    public boolean isGet_other() {
        return get_other;
    }

    public void setGet_other(boolean get_other) {
        this.get_other = get_other;
    }

    public List getOrder_by() {
        return order_by;
    }

    public void setOrder_by(List order_by) {
        this.order_by = order_by;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    private boolean get_other;
    private List order_by;
    private int offset;

    public List<RequestFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<RequestFilter> filters) {
        this.filters = filters;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    private List<String> fields;
    private List<RequestFilter> filters;
}
