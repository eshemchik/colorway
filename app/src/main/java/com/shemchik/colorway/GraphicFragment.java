package com.shemchik.colorway;

public class GraphicFragment {
    public int left, right, top, bottom;

    public GraphicFragment() {}

    public boolean isIn(int x, int y) {
        return (x >= left && x <= right && y >= top && y <= bottom);
    }
}