package com.miniredis.protocol;

public class Encoder {
    public String encode(String response) {
        return response + "\n";
    }
}
