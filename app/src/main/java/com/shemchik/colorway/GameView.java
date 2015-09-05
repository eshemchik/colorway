package com.shemchik.colorway;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Stack;

public class GameView extends View{
    private Paint mPaint = new Paint();
    private GameController parent;
    private Cell cells[];
    private Level level;
    private int size;
    private int cellSize;
    private int[] colors = {
            Color.rgb(189, 195, 199),
            Color.rgb(231, 76, 60),
            Color.rgb(46, 204, 113),
            Color.rgb(52, 152, 219),
            Color.rgb(241, 196, 15),
            Color.rgb(155, 89, 182),
            Color.rgb(26, 188, 156)
    };
    private GraphicFragment backButton = new GraphicFragment();
    private GraphicFragment restartButton = new GraphicFragment();
    private GraphicFragment helpButton = new GraphicFragment();
    private int lastId = 0;
    private boolean gameEnded = false;

    public GameView(Context context, Level level, GameController parent) {
        super(context);

        this.parent = parent;
        this.level = level;
        size = level.size;
        cells = new Cell[level.size * level.size];
        for (int i = 0; i < level.size; i++)
            for (int j = 0; j < level.size; j++) {
                cells[i * size + j] = new Cell();
                cells[i * size + j].type = 0;
            }

        int ind = 0;
        for (int i = 0; i < level.counters.length; i++)
            for (int j = 0; j < level.counters[i]; j++) {
                cells[level.bases[ind]].type = i + 1;
                cells[level.bases[ind]].isBase = true;
                ind++;
            }


    }

    private int textSizeByWidth(String text, int width) {
        float w = width;
        float baseSize = 64;
        mPaint.setTextSize(baseSize);
        float height = baseSize * (w / mPaint.measureText(text));
        mPaint.setTextSize(height);
        return Math.round(height);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        int padding = getResources().getDimensionPixelSize(R.dimen.padding);
        int stars_padding = getResources().getDimensionPixelSize(R.dimen.stars_padding);
        int menuHight = getResources().getDimensionPixelSize(R.dimen.menuHight);
        int scoreHight = getResources().getDimensionPixelSize(R.dimen.scoreHight);

        int h = this.getHeight();
        int w = this.getWidth();
        cellSize = Math.min(h - menuHight - scoreHight - 2 * padding, w - 2 * padding) / size;
        int clientWidth = cellSize * size;
        int menuTop = h - padding - menuHight;
        int cellTop = (padding + scoreHight + menuTop - clientWidth) / 2;
        int cellLeft = (w - clientWidth) / 2;

        // Drawing background
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getResources().getColor(R.color.backgroundColor));
        canvas.drawPaint(mPaint);

        // Drawing score
        mPaint.setColor(getResources().getColor(R.color.lightBackgroundColor));
        canvas.drawRect(padding, padding, padding + clientWidth, padding + scoreHight, mPaint);
        Bitmap star = BitmapFactory.decodeResource(getResources(), R.mipmap.star);
        Bitmap empty_star = BitmapFactory.decodeResource(getResources(), R.mipmap.empty_star);
        int starsTop = padding + scoreHight / 2 - star.getHeight() / 2;
        int starsLeft[] = {
                clientWidth - 5 * star.getWidth() / 2 - 3 * stars_padding,
                clientWidth - 3 * star.getWidth() / 2 - 2 * stars_padding,
                clientWidth - star.getWidth() / 2 - stars_padding
        };

        int score = getScore();
        for (int i = 0; i < 3; i++)
            canvas.drawBitmap((score <= level.limits[i]) ? star : empty_star, starsLeft[i], starsTop, mPaint);

        int score_color;
        if (score <= level.limits[2])
            score_color = R.color.goldColor;
        else if (score <= level.limits[1])
            score_color = R.color.silverColor;
        else if (score <= level.limits[0])
            score_color = R.color.bronzeColor;
        else
            score_color = R.color.redColor;
        score_color = getResources().getColor(score_color);
        mPaint.setTextSize(star.getHeight());
        mPaint.setColor(score_color);
        mPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawText(Integer.toString(score), padding + stars_padding, starsTop + star.getHeight() - stars_padding, mPaint);
        mPaint.setStyle(Paint.Style.FILL);


        // Drawing cells
        if (!gameEnded) {
            for (int i = 0; i < level.size; i++)
                for (int j = 0; j < level.size; j++) {
                    cells[i * size + j].x = j * cellSize + cellLeft;
                    cells[i * size + j].y = i * cellSize + cellTop;
                }

            for (Cell cell : cells) {
                mPaint.setColor(colors[cell.type]);
                canvas.drawRect(cell.x, cell.y, cell.x + cellSize, cell.y + cellSize, mPaint);

                if (cell.isBase) {
                    mPaint.setColor(colors[0]);
                    canvas.drawRect(cell.x + cellSize / 3, cell.y + cellSize / 3, cell.x + 2 * cellSize / 3, cell.y + 2 * cellSize / 3, mPaint);
                }
            }
        } else {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(getResources().getColor(R.color.lightText));

            String text;
            text = getResources().getString(R.string.level_completed);
            int textTop = cellTop + clientWidth / 2;
            int textHeight = textSizeByWidth(text, clientWidth);
            canvas.drawText(text, cellLeft, textTop, mPaint);
            textTop += textHeight + stars_padding;
            text = getResources().getString(R.string.click_to_continue);
            textHeight = textSizeByWidth(text, clientWidth);
            canvas.drawText(text, cellLeft, textTop, mPaint);
            mPaint.setStyle(Paint.Style.FILL);
        }

        // Drawing menu
        canvas.translate(padding, menuTop);
        mPaint.setColor(getResources().getColor(R.color.lightBackgroundColor));
        canvas.drawRect(0, 0, clientWidth, menuHight, mPaint);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.restart);
        canvas.drawBitmap(bitmap, (clientWidth - bitmap.getWidth()) / 2, (menuHight - bitmap.getHeight()) / 2, mPaint);

        restartButton.left = padding + (clientWidth - bitmap.getWidth()) / 2;
        restartButton.top = menuTop + (menuHight - bitmap.getHeight()) / 2;
        restartButton.right = padding + (clientWidth + bitmap.getWidth()) / 2;
        restartButton.bottom = menuTop + (menuHight + bitmap.getHeight()) / 2;

        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.back);
        canvas.drawBitmap(bitmap, stars_padding, (menuHight - bitmap.getHeight()) / 2, mPaint);

        backButton.left = padding + stars_padding;
        backButton.top = menuTop + (menuHight - bitmap.getHeight()) / 2;
        backButton.right = padding + stars_padding + bitmap.getWidth();
        backButton.bottom = menuTop + (menuHight + bitmap.getHeight()) / 2;

        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.help);
        canvas.drawBitmap(bitmap, clientWidth - stars_padding - bitmap.getWidth(), (menuHight - bitmap.getHeight()) / 2, mPaint);

        helpButton.left = padding + clientWidth - stars_padding - bitmap.getWidth();
        helpButton.right = padding + clientWidth - stars_padding;
        helpButton.top = menuTop + (menuHight - bitmap.getHeight()) / 2;
        helpButton.bottom = menuTop + (menuHight + bitmap.getHeight()) / 2;

        canvas.translate(-padding, -menuTop);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = Math.round(event.getX());
        int y = Math.round(event.getY());

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            if (backButton.isIn(x, y)) {
                parent.onBack();
                this.destroyDrawingCache();
                return true;
            }

            if (restartButton.isIn(x, y)) {
                parent.onRestart();
                this.destroyDrawingCache();
                return true;
            }

            if (helpButton.isIn(x, y)) {
                getContext().setTheme(R.style.AppThemeBase);
                Toast.makeText(getContext(), R.string.game_description, Toast.LENGTH_LONG).show();
                return true;
            }

            int ind = -1;
            for (int i = 0; i < cells.length && ind == -1; i++)
                if (x >= cells[i].x && x <= cells[i].x + cellSize)
                    if (y >= cells[i].y && y <= cells[i].y + cellSize)
                        ind = i;

            if (ind == -1)
                return false;

            if (!gameEnded)
                onCellDown(ind);
            else
                parent.onNextGame();

        } else {
            int ind = -1;
            for (int i = 0; i < cells.length && ind == -1; i++)
                if (x >= cells[i].x && x <= cells[i].x + cellSize)
                    if (y >= cells[i].y && y <= cells[i].y + cellSize)
                        ind = i;

            if (ind == -1)
                return false;

            if (event.getAction() == MotionEvent.ACTION_UP)
                onCellUp(ind);
            else
                onCellMove(ind);
        }
        return true;
    }


    private void onCellDown(int id) {
        lastId = id;
    }

    private void onCellMove(int id) {
        if (!cells[id].isBase) {
            cells[id].type = cells[lastId].type;
            this.invalidate();
            check();
        }
    }

    private void onCellUp(int id) {
        if (id == lastId && !cells[id].isBase) {
            cells[id].type = (cells[id].type + 1) % (level.counters.length + 1);
            this.invalidate();
        }

        check();
    }

    public int getScore() {
        int score = 0;
        for (Cell cell : cells)
            if (cell.type != 0 && !cell.isBase)
                score++;
        return score;
    }

    public int getStarsScore() {
        int res = 0;
        for (int i = 0; i < 3; i++)
            if (getScore() <= level.limits[i])
                res++;
        return res;
    }

    public void check() {

        //Graph init
        ArrayList< ArrayList<Integer> > graph = new ArrayList<>();
        int[] color = new int[cells.length];
        for (int i = 0; i < cells.length; i++) {
            graph.add(new ArrayList<Integer>());

            //Left
            if (i % size > 0)
                if (cells[i - 1].type == cells[i].type)
                    graph.get(i).add(i - 1);
            //Right
            if (i % size < size - 1)
                if (cells[i + 1].type == cells[i].type)
                    graph.get(i).add(i + 1);
            //Top
            if (i / size > 0)
                if (cells[i - size].type == cells[i].type)
                    graph.get(i).add(i - size);
            //Bottom
            if (i / size < size - 1)
                if (cells[i + size].type == cells[i].type)
                    graph.get(i).add(i + size);

            color[i] = 0;
        }

        //pseudo-BFS
        int ind = 0;
        for (int i = 0; i < level.counters.length; i++) {
            int start = level.bases[ind];
            Stack<Integer> stack = new Stack<>();
            stack.push(start);
            color[start] = i + 1;
            while (!stack.empty()) {
                int cur = stack.pop();
                for (int j = 0; j < graph.get(cur).size(); j++) {
                    int tmp = graph.get(cur).get(j);
                    if (color[tmp] == 0) {
                        color[tmp] = i + 1;
                        stack.push(tmp);
                    }
                }
            }
            ind += level.counters[i];
        }

        for (int i = 0; i < cells.length; i++)
            if (color[i] != cells[i].type)
                return;

        //GAME ENDED
        if (getScore() <= level.limits[0]) {
            parent.onGameEnded(getStarsScore());
            gameEnded = true;
            this.invalidate();
        }
    }

    private class Cell {
        public int type = 0;
        public int x = 0, y = 0;
        public boolean isBase = false;
    }

}