package com.shemchik.colorway;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;


public class MainActivity extends Activity{

    ScrollView mainView;
    MenuView menuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getTheme().applyStyle(R.style.AppThemeBase, true);

        mainView = new ScrollView(this);

        menuView = new MenuView(this);
        int levelsCount = getResources().getInteger(R.integer.levelsCount);
        int padding = getResources().getDimensionPixelSize(R.dimen.padding);
        int levelHight = getResources().getDimensionPixelSize(R.dimen.levelHight);
        int levelWidth = getResources().getDimensionPixelSize(R.dimen.levelWidth);
        int menuHight = getResources().getDimensionPixelSize(R.dimen.menuHight);
        int lineSize = (getWindowManager().getDefaultDisplay().getWidth() - padding) / (padding + levelWidth);
        int lines_count = levelsCount / lineSize + (levelsCount % lineSize != 0 ? 1 : 0);
        menuView.setMinimumHeight(Math.max(2 * padding + (padding + levelHight) * lines_count + menuHight, getWindowManager().getDefaultDisplay().getHeight()));
        mainView.addView(menuView);
        mainView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        setContentView(R.layout.activity_main);
    }

    public void onPlayClicked(View view) {
        showMenu();
    }

    public void showMenu() {
        menuView.invalidate();
        setContentView(mainView);
    }
}
