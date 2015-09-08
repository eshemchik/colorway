package com.shemchik.colorway;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;


public class MainActivity extends Activity{

    ScrollView mainView;
    MenuView menuView;
    BlitzController blitzCtrl;
    public int currentScreen = 0;
    private int color_theme;
    public GameController.GameType gameType;

    static final int MAIN_SCREEN = 0;
    static final int MENU_SCREEN = 1;
    static final int BLITZ_SCREEN = 1;
    static final int GAME_SCREEN = 2;

    private final String PREFERENCES = "color_way_preferences";
    private final String COLOR_THEME = "color_theme";

    static final String FlurryID = "6NM5BDNQJHGGYYR5GCVM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getTheme().applyStyle(R.style.AppThemeBase, true);

        blitzCtrl = new BlitzController(this);

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
        refresh();

        SharedPreferences preferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        color_theme = Integer.valueOf(preferences.getString(COLOR_THEME, "0"));
        refreshColors();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.init(this, FlurryID);
        FlurryAgent.onStartSession(this, FlurryID);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    @Override
    public void onBackPressed() {
        if (currentScreen == MAIN_SCREEN)
            super.onBackPressed();
        else {
            currentScreen--;
            if (currentScreen == MAIN_SCREEN) {
                setContentView(R.layout.activity_main);
                refresh();
            } else
                showMenu();
        }
    }

    public void onPlayClicked(View view) {
        showMenu();
    }

    public void onThemeChange(View view) {
        color_theme ^= 1;

        SharedPreferences preferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(COLOR_THEME, Integer.toString(color_theme));
        editor.apply();

        refreshColors();
    }

    public void onBlitzClicked(View view) {
        currentScreen = BLITZ_SCREEN;
        gameType = GameController.GameType.TIME;
        blitzCtrl.startGame();
    }

    public void showMenu() {
        currentScreen = MENU_SCREEN;
        gameType = GameController.GameType.LEVELS;
        menuView.invalidate();
        setContentView(mainView);
    }

    public void refresh() {
        ((TextView)findViewById(R.id.score_text)).setText(String.format(getResources().getString(R.string.your_score), menuView.getScore()));
        ((TextView)findViewById(R.id.record_text)).setText(String.format(getResources().getString(R.string.your_record), blitzCtrl.getRecord()));
        refreshColors();
    }

    public void refreshColors() {
        ColorController.setTheme(color_theme);
        findViewById(R.id.main_layout).setBackgroundColor(ColorController.getColor(ColorController.backgroundColor));
        findViewById(R.id.themeChange).setBackgroundColor(ColorController.getColor(ColorController.antiBackground));
        ((Button)findViewById(R.id.playButton)).setBackgroundColor(ColorController.getColor(ColorController.greenBackground));
        ((Button)findViewById(R.id.playOnTimeButton)).setBackgroundColor(ColorController.getColor(ColorController.greenBackground));
        ((Button)findViewById(R.id.playButton)).setTextColor(ColorController.getColor(ColorController.buttonText));
        ((Button)findViewById(R.id.playOnTimeButton)).setTextColor(ColorController.getColor(ColorController.buttonText));
        ((TextView)findViewById(R.id.score_text)).setTextColor(ColorController.getColor(ColorController.lightText));
        ((TextView)findViewById(R.id.record_text)).setTextColor(ColorController.getColor(ColorController.lightText));
    }
}