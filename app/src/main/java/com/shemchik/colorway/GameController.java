package com.shemchik.colorway;

public interface GameController{
    void onRestart();
    void onGameEnded(int score);
    void onNextGame();
    void onBack();
}
