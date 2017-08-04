package io.github.keep2iron.reportview;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import io.github.keep2iron.reportview.controller.CellController;
import io.github.keep2iron.reportview.entry.Cell;
import io.github.keep2iron.reportview.view.ReportContentView;
import io.github.keep2iron.reportview.view.ReportLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ReportLayout reportLayout = (ReportLayout) findViewById(R.id.reportLayout);
        reportLayout.setLeftTopController(new CellController(getApplicationContext(), setLeftTopData(), 4, false));
        reportLayout.setFixedHorizontalController(new CellController(getApplicationContext(), setHeader(), 36, false));
        reportLayout.setFixedVerticalController(new CellController(getApplicationContext(), setLeftData(10, 4), 4, false));
        reportLayout.setContentViewController(new CellController(getApplicationContext(), setData(10, 36), 36, true));
//        ReportContentView reportView = (ReportContentView) findViewById(R.id.reportView);
//
//        List<Cell> data = setData();
//        cells.addAll(data);
//
//        CellController controller = new CellController(this, cells, 40);
//        reportView.setCellController(controller);
    }

    private List<Cell> setLeftData(int row, int column) {
        List<Cell> cells = new ArrayList<>();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                cells.add(new Cell("测试", 1, 1));
            }
        }
        return cells;
    }

    private List<Cell> setData(int row, int column) {
        List<Cell> cells = new ArrayList<>();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                cells.add(new Cell("测试 : " + i + " : " + j, 1, 1));
            }
        }
        return cells;
    }

    public List<Cell> setLeftTopData() {
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell("区总", 3, 1));
        cells.add(new Cell("区经", 3, 1));
        cells.add(new Cell("店名", 3, 1));
        cells.add(new Cell("店长", 3, 1));
        return cells;
    }

    @NonNull
    private List<Cell> setHeader() {
        //从左到右从上到下进行排列
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell("出售", 1, 25));
        cells.add(new Cell("出租", 1, 11));

        cells.add(new Cell("房源数量", 2, 1));
        cells.add(new Cell("有简易委托", 1, 2));
        cells.add(new Cell("有普通委托", 1, 2));
        cells.add(new Cell("有独家委托", 1, 2));
        cells.add(new Cell("有房勘", 1, 2));
        cells.add(new Cell("合规房勘", 1, 2));
        cells.add(new Cell("有钥匙", 1, 2));
        cells.add(new Cell("三项合规", 1, 2));
        cells.add(new Cell("房源检查过且有委托", 1, 2));
        cells.add(new Cell("房源检查过且有合规房勘", 1, 2));
        cells.add(new Cell("有委托且有合规房勘", 1, 2));
        cells.add(new Cell("有委托无合规房勘", 1, 2));
        cells.add(new Cell("有合规房勘无委托", 1, 2));

        cells.add(new Cell("房源数量", 2, 1));
        cells.add(new Cell("有简易委托", 1, 2));
        cells.add(new Cell("有普通委托", 1, 2));
        cells.add(new Cell("有房勘", 1, 2));
        cells.add(new Cell("合规房勘", 1, 2));
        cells.add(new Cell("有钥匙", 1, 2));

        for (int i = 0; i < 17; i++) {
            cells.add(new Cell("数量", 1, 1));
            cells.add(new Cell("比例", 1, 1));
        }

        return cells;
    }
}