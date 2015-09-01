package com.shemchik.colorway;

import com.google.gson.Gson;

public class Level {
    public int min_score;
    public int size;
    public int limits[];
    public int counters[];
    public int bases[];

    public String toJSON() {
        return new Gson().toJson(this);
    }

    public static Level fromJson(String json) {
        return new Gson().fromJson(json, Level.class);
    }
}
