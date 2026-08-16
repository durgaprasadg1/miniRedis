package com.miniredis.storage;

public class Entry {

    private final DataType type;
    private Object value;
    private long expiresAt;

    public Entry(DataType type, Object value) {
        this.type = type;
        this.value = value;
        this.expiresAt = -1;
    }

    public DataType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;

    }

    public long getExpiresAt() {

        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

}
