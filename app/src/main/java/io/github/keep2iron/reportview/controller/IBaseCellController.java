package io.github.keep2iron.reportview.controller;

import java.util.List;

import io.github.keep2iron.reportview.entry.Cell;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/08/01 15:30
 */
public interface IBaseCellController {

    int column();

    List<Cell> getCells();

    int cellWidth();

    int cellHeight();

    boolean canBeTouch();
}