package com.hogle.education.Model;

public class Map {
    int startX;
    int startY;
    int cellLength;

    public Map(int startX, int startY, int size) {
        this.startX = startX;
        this.startY = startY;
        this.cellLength = size / 5;
    }

    public int getX(int x) {
        return startX + x * cellLength;
    }

    public int getY(int y) {
        return startY + y * cellLength;
    }

    public int getCellLength() {
        return cellLength;
    }
}
