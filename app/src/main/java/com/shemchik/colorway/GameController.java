package com.shemchik.colorway;

public interface GameController{
    enum GameType {LEVELS, TIME};

    void onRestart();
    void onGameEnded(int score);
    void onNextGame();
    void onBack();
}
