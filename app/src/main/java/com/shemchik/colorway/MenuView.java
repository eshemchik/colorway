package com.shemchik.colorway;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;


public class MenuView extends View implements GameController{
    private Paint mPaint = new Paint();
    private Level[] levels;
    private int[] scores;
    private GraphicFragment[] buttons;
    private final String PREFERENCES = "color_way_preferences";
    private final String SCORES = "scores";
    private int CurrentLevel;
    private Gson gson = new Gson();
    private int click_x, click_y;
    GraphicFragment backButton;

    public MenuView(Context context) {
        super(context);
        init();
    }

    public MenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public int getScore() {
        int res = 0;
        for (int i = 0; i < scores.length; i++)
            res += scores[i];
        return res;
    }

    protected void init() {
        // Load levels
        int levelsCount = getResources().getInteger(R.integer.levelsCount);
        levels = new Level[levelsCount];
        String[] jsons = getResources().getStringArray(R.array.levels);
        for (int i = 0; i < levelsCount; i++)
            levels[i] = Level.fromJson(jsons[i]);

        buttons = new GraphicFragment[levelsCount];

        scores = new int[levelsCount];
        for (int i = 0; i < levelsCount; i++)
            scores[i] = 0;

        SharedPreferences preferences = getContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        String string_scores = preferences.getString(SCORES, "[]");
        Integer[] tmp = gson.fromJson(string_scores, Integer[].class);
        for (int i = 0; i < Math.min(tmp.length, scores.length); i++)
            scores[i] = tmp[i];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(getResources().getColor(R.color.backgroundColor));

        int w = getWidth();

        int padding = getResources().getDimensionPixelSize(R.dimen.padding);
        int stars_padding = getResources().getDimensionPixelSize(R.dimen.stars_padding);
        int levelHight = getResources().getDimensionPixelSize(R.dimen.levelHight);
        int levelWidth = getResources().getDimensionPixelSize(R.dimen.levelWidth);
        int menuHight = getResources().getDimensionPixelSize(R.dimen.menuHight);
        int clientWidth = w - 2 * padding;

        int count = levels.length;



        mPaint.setColor(getResources().getColor(R.color.lightBackgroundColor));
        mPaint.setStyle(Paint.Style.FILL);

        Bitmap locked_icon = BitmapFactory.decodeResource(getResources(), R.mipmap.locked);

        int lineSize = (w - padding) / (padding + levelWidth);

        for (int i = 0; i < count; i++) {
            int line = i / lineSize;
            int pos = i % lineSize;

            int line_elems =  (line == (count - 1) / lineSize && count % lineSize != 0) ? (count % lineSize) : lineSize;
            int line_dx = (w - padding - (padding + levelWidth)  * line_elems) / 2;

            int levelTop = 2 * padding + menuHight + line * (padding + levelHight);
            int levelLeft = line_dx + padding + pos * (padding + levelWidth);

            canvas.translate(levelLeft, levelTop);

            // Is locked
            if (getScore() < levels[i].min_score) {
                mPaint.setColor(getResources().getColor(R.color.lightBackgroundColor));
                canvas.drawRect(0, 0, levelWidth, levelHight, mPaint);
                canvas.drawBitmap(locked_icon, (levelWidth - locked_icon.getWidth()) / 2, (levelHight - locked_icon.getHeight()) / 2, mPaint);
            } else {
                int color, text_color;
                if (scores[i] == 0)
                    color = R.color.greenBackground;
                else if (scores[i] == 1)
                    color = R.color.bronzeColor;
                else if (scores[i] == 2)
                    color = R.color.silverColor;
                else
                    color = R.color.goldColor;
                color = getResources().getColor(color);
                text_color = getResources().getColor(R.color.darkText);
                mPaint.setColor(color);
                canvas.drawRect(0, 0, levelWidth, levelHight, mPaint);
                mPaint.setColor(text_color);
                mPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
                mPaint.setStyle(Paint.Style.STROKE);
                int text_h = locked_icon.getHeight();
                mPaint.setTextSize(text_h);
                int text_left = (levelWidth - Math.round(mPaint.measureText(Integer.toString(i + 1)))) / 2;
                canvas.drawText(Integer.toString(i + 1), text_left, (levelHight + text_h) / 2 - stars_padding, mPaint);
                mPaint.setStyle(Paint.Style.FILL);
            }

            buttons[i] = new GraphicFragment();
            buttons[i].left = levelLeft;
            buttons[i].right = levelLeft + levelWidth;
            buttons[i].top = levelTop;
            buttons[i].bottom = levelTop + levelHight;

            canvas.translate(-levelLeft, -levelTop);

            // Drawing menu

            canvas.translate(padding, padding);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.back);
            canvas.drawBitmap(bitmap, stars_padding, (menuHight - bitmap.getHeight()) / 2, mPaint);

            backButton = new GraphicFragment();
            backButton.left = padding;
            backButton.right = padding + bitmap.getWidth();
            backButton.top = padding;
            backButton.bottom = padding + bitmap.getHeight();

            String string_score = Integer.toString(getScore());

            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.star);
            canvas.drawBitmap(bitmap, clientWidth - stars_padding - bitmap.getWidth(), (menuHight - bitmap.getHeight()) / 2, mPaint);

            mPaint.setTextSize(bitmap.getHeight());
            mPaint.setColor(getResources().getColor(R.color.goldColor));
            mPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
            mPaint.setStyle(Paint.Style.STROKE);
            int textLeft = clientWidth - stars_padding * 2 - bitmap.getWidth() - Math.round(mPaint.measureText(string_score));
            int textTop = (menuHight + bitmap.getHeight()) / 2 - stars_padding;
            canvas.drawText(string_score, textLeft, textTop, mPaint);
            mPaint.setStyle(Paint.Style.FILL);

            canvas.translate(-padding, -padding);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            click_x = Math.round(event.getX());
            click_y = Math.round(event.getY());
            return true;
        }

        int x = Math.round(event.getX());
        int y = Math.round(event.getY());

        if (event.getAction() != MotionEvent.ACTION_UP || Math.abs(x - click_x) > 50 || Math.abs(y - click_y) > 50)
            return false;

        if (backButton.isIn(x, y)) {
            ((Activity)getContext()).setContentView(R.layout.activity_main);
        }

        int ind = -1;
        for (int i = 0; i < buttons.length && ind == -1; i++)
            if (buttons[i].isIn(x, y))
                ind = i;
        if (ind == -1)
            return false;

        if (getScore() < levels[ind].min_score) {
            String message = getResources().getString(R.string.need_score);
            message = String.format(message, levels[ind].min_score);
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        } else {
            gameStart(ind);
        }

        return true;
    }

    public void gameStart(int levelId) {
        CurrentLevel = levelId;
        ((Activity)getContext()).setContentView(new GameView(getContext(), levels[levelId], this));
        String message = String.format(getResources().getString(R.string.level_number), levelId + 1);
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRestart() {
        gameStart(CurrentLevel);
    }

    @Override
    public void onGameEnded(int score) {
        scores[CurrentLevel] = Math.max(scores[CurrentLevel], score);
        String string_scores = gson.toJson(scores, Integer[].class);
        SharedPreferences preferences = getContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SCORES, string_scores);
        editor.apply();

        if (CurrentLevel == levels.length - 1 || getScore() < levels[CurrentLevel + 1].min_score)
            onBack();
        else
            gameStart(CurrentLevel + 1);
    }

    @Override
    public void onBack() {
        ((MainActivity)getContext()).showMenu();
    }
}
