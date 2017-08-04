package io.github.keep2iron.reportview.entry;

import android.graphics.Rect;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/08/01 15:31
 */
public class Cell {
    String data;
    int columnSpan;         //占几列
    int rowSpan;            //占几行
    Rect mRect;

    public Cell(String data) {
        this.data = data;
        this.columnSpan = 1;
        this.rowSpan = 1;
        this.mRect = new Rect();
    }

    public Cell(String data, int rowSpan, int columnSpan) {
        this.data = data;
        this.columnSpan = columnSpan;
        this.rowSpan = rowSpan;
        this.mRect = new Rect();
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setColumnSpan(int columnSpan) {
        this.columnSpan = columnSpan;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    public int getColumnSpan() {
        return columnSpan;
    }

    public Rect getRect() {
        return mRect;
    }

    public void setRect(Rect rect) {
        mRect = rect;
    }
}