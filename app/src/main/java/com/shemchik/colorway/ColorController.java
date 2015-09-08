package com.shemchik.colorway;

import android.graphics.Color;

public class ColorController {
    private static int theme = 0;

    public static final int backgroundColor = 0;
    public static final int lightBackgroundColor = 1;
    public static final int goldColor = 2;
    public static final int silverColor = 3;
    public static final int bronzeColor = 4;
    public static final int redColor = 5;
    public static final int lightText = 6;
    public static final int darkText = 7;
    public static final int greenBackground = 8;
    public static final int transparentColor = 9;
    public static final int menuText = 10;
    public static final int unlockedLevel = 11;
    public static final int antiBackground = 12;
    public static final int buttonText = 13;

    private static String[][] colors = {
            {"#19B5FE", "#2c3e50"}, //backgroundColor
            {"#2574A9", "#34495e"}, //lightBackgroundColor
            {"#2ECC71", "#F5AB35"}, //goldColor
            {"#F7CA18", "#BDC3C7"}, //silverColor
            {"#EF4836", "#A57164"}, //bronzeColor
            {"#EF4836", "#e74c3c"}, //redColor
            {"#ffffff", "#7f8c8d"}, //lightText
            {"#ffffff", "#2c3e50"}, //darkText
            {"#2574A9", "#27ae60"}, //greenBackground
            {"#0000", "#0000"}, //transparentColor
            {"#ffffff", "#F5AB35"}, //menuText
            {"#2574A9", "#27ae60"}, //unlockedLevel
            {"#2c3e50", "#19B5FE"}, //antiBack
            {"#ffffff", "#2c3e50"}
    };

    public static void setTheme(int theme) {
        ColorController.theme = theme;
    }

    public static int getColor(int id) {
        return Color.parseColor(colors[id][theme]);
    }
}
