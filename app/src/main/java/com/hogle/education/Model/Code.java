package com.hogle.education.Model;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class Code {
    public static final int NOTHING = 0;
    public static final int FORWARD = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    public static final int IF = 4;
    public static final int LOOP = 5;
    public static final int ROCK = 100;
    public static final int WALL = 101;

    private int code, ifCondition, loopCount;
    private Code trueCode, falseCode;
    private ArrayList<Code> loopList;
    private RecyclerView.Adapter adapter;
    private boolean isChecked;

    public Code(int code) {
        this.code = code;
        if (code == IF) {
            trueCode = new Code(NOTHING);
            falseCode = new Code(NOTHING);
        } else if (code == LOOP)
            loopList = new ArrayList<>();
    }

    public String getStringCode() {
        String result = "";
        switch (this.code) {
            case FORWARD:
                result += "전진";
                break;
            case LEFT:
                result += "좌회전";
                break;
            case RIGHT:
                result += "우회전";
                break;
            case IF:
                result += "만약";
                break;
            case LOOP:
                result += "반복";
                break;
        }
        return result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getIfCondition() {
        return ifCondition;
    }

    public void setIfCondition(int ifCondition) {
        this.ifCondition = ifCondition;
    }

    public int getLoopCount() {
        return loopCount;
    }

    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

    public Code getTrueCode() {
        return trueCode;
    }

    public void setTrueCode(Code trueCode) {
        this.trueCode = trueCode;
    }

    public Code getFalseCode() {
        return falseCode;
    }

    public void setFalseCode(Code falseCode) {
        this.falseCode = falseCode;
    }

    public ArrayList<Code> getLoopList() {
        return loopList;
    }

    public void setLoopList(ArrayList<Code> loopList) {
        this.loopList = loopList;
    }

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
