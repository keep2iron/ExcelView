package io.github.keep2iron.reportview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import io.github.keep2iron.reportview.R;
import io.github.keep2iron.reportview.controller.IBaseCellController;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/08/03 15:46
 */
public class ReportLayout extends RelativeLayout {
    ReportContentView mFixedHorizontalView;
    ReportContentView mFixedVerticalView;
    ReportContentView mFixedTopLeftView;
    ReportContentView mContentView;

    public ReportLayout(Context context) {
        this(context, null);
    }

    public ReportLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReportLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_report_layout, this, true);

        mFixedHorizontalView = (ReportContentView) findViewById(R.id.fixedHorizontalView);
        mContentView = (ReportContentView) findViewById(R.id.contentView);
        mFixedVerticalView = (ReportContentView) findViewById(R.id.fixedVerticalView);
        mFixedTopLeftView = (ReportContentView) findViewById(R.id.fixedLeftTpoView);

        mContentView.setOnScrollListener(new ReportContentView.OnScrollListener() {
            @Override
            public void onTouchMove(int dx, int dy) {
//                Log.e("TAG","scrollX" + scrollX + " scrollY : " + scrollY);
                mFixedHorizontalView.scrollBy(dx, 0);
                mFixedVerticalView.scrollBy(0, dy);

                mFixedVerticalView.postInvalidate();
                mFixedHorizontalView.postInvalidate();
            }

            @Override
            public void onFillingMove(int scrollX, int scrollY) {
                mFixedHorizontalView.scrollTo(scrollX, 0);
                mFixedVerticalView.scrollTo(0, scrollY);

                mFixedVerticalView.postInvalidate();
                mFixedHorizontalView.postInvalidate();
            }

            @Override
            public void onScale(float scale) {
                mFixedHorizontalView.setScale(scale);
                mFixedTopLeftView.setScale(scale);
                mFixedVerticalView.setScale(scale);
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public void setFixedHorizontalController(IBaseCellController fixedHorizontalController) {
        mFixedHorizontalView.setCellController(fixedHorizontalController);
    }

    public void setContentViewController(IBaseCellController contentViewController) {
        mContentView.setCellController(contentViewController);
    }

    public void setFixedVerticalController(IBaseCellController fixedVerticalController) {
        mFixedVerticalView.setCellController(fixedVerticalController);
    }

    public void setLeftTopController(IBaseCellController leftTopController) {
        mFixedTopLeftView.setCellController(leftTopController);
    }
}