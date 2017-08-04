package io.github.keep2iron.reportview.controller;

import android.content.Context;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.List;

import io.github.keep2iron.reportview.entry.Cell;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/08/01 15:37
 */
public class CellController implements IBaseCellController {

    private final boolean canBeTouch;
    List<Cell> mFixedHorizontalCells = new ArrayList<>();
    int column;
    private Context ctx;

    public CellController(Context context, List<Cell> fixedHorizontalCells, int column,boolean canBeTouch) {
        mFixedHorizontalCells = fixedHorizontalCells;
        this.column = column;
        ctx = context.getApplicationContext();
        this.canBeTouch = canBeTouch;
    }

    @Override
    public int column() {
        return column;
    }

    @Override
    public List<Cell> getCells() {
        return mFixedHorizontalCells;
    }

    @Override
    public int cellWidth() {
        return 3 * cellHeight();
    }

    @Override
    public int cellHeight() {
        return (int) dp2px(20);
    }

    @Override
    public boolean canBeTouch() {
        return canBeTouch;
    }

    private float dp2px(int dp) {
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        return dp * dm.density;
    }
}