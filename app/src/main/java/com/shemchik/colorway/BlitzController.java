package com.shemchik.colorway;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.InputType;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Random;

public class BlitzController implements GameController {
    MainActivity parent;
    private Level[] levels;
    public int score;
    private int record;
    private final String PREFERENCES = "color_way_preferences";
    private final String SCORES = "scores_blitz";
    private Random rand = new Random();
    private boolean levelSuccess;
    public int timer;

    public BlitzController(MainActivity parent) {
        this.parent = parent;

        int levelsCount = parent.getResources().getInteger(R.integer.levelsCount);
        levels = new Level[levelsCount];
        String[] jsons = parent.getResources().getStringArray(R.array.levels);
        for (int i = 0; i < levelsCount; i++)
            levels[i] = Level.fromJson(jsons[i]);

        SharedPreferences preferences = parent.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        String string_scores = preferences.getString(SCORES, "0");
        record = Integer.valueOf(string_scores);
    }

    public int getRecord() {
        return record;
    }

    private Level getLevel() {
        int ind = rand.nextInt(levels.length);
        return levels[ind];
    }

    public void startGame() {
        levelSuccess = true;
        score = 0;
        timer = parent.getResources().getInteger(R.integer.blitzTimelimit);
        onNextGame();
    }

    @Override
    public void onRestart() {
        startGame();
    }

    @Override
    public void onGameEnded(int score) {
        levelSuccess = (score > 0);
        if (levelSuccess)
            this.score++;
        if (this.score > record) {
            Toast.makeText(parent, R.string.new_record, Toast.LENGTH_SHORT).show();
            record = this.score;
            SharedPreferences preferences = parent.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(SCORES, Integer.toString(record));
            editor.apply();
        }
    }

    @Override
    public void onNextGame() {
        if (levelSuccess)
            parent.setContentView(new GameView(parent, getLevel(), this));
        else
            onBack();
    }

    @Override
    public void onBack() {
        parent.setContentView(R.layout.activity_main);
        parent.refresh();
    }
}
