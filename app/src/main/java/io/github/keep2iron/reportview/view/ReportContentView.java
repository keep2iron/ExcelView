package io.github.keep2iron.reportview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.github.keep2iron.reportview.controller.IBaseCellController;
import io.github.keep2iron.reportview.entry.Cell;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/08/01 15:25
 */
public class ReportContentView extends View {
    IBaseCellController mCellController;    //行列控制器

    Scroller mScroller;
    VelocityTracker mVelocityTracker;       //速度跟踪器

    ViewConfiguration mViewConfiguration;
    //    List<Rect> mRectList = new ArrayList<>();
    List<Cell> mCells = new ArrayList<>();

    private Paint mLinePaint;
    private Paint mTextPaint;

    private int mMaxScrollerX;
    private int mMaxScrollerY;

    private int paddingX;
    private int paddingY;

    DisplayMetrics mMetrics;

    int computeRowNum;

    int originWidth;
    int originHeight;

    public ReportContentView(Context context) {
        this(context, null);
    }

    public ReportContentView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReportContentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        mScroller = new Scroller(getContext());
        mViewConfiguration = ViewConfiguration.get(getContext());

        mLinePaint = new Paint();
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(2);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setTextSize(20);

        mMetrics = getResources().getDisplayMetrics();
        float density = mMetrics.density;
        paddingX = (int) (density * 0);
        paddingY = (int) (density * 0);

    }

    public void setCellController(IBaseCellController cellController) {
        mCellController = cellController;

        int currentColumn = 0;
        int currentRow = 0;

        List<Cell> cells = mCellController.getCells();
        mCells.addAll(cells);
        int column = mCellController.column();

        int cellWidth = mCellController.cellWidth();
        int cellHeight = mCellController.cellHeight();

//        int currentRowSpan = 0;
//        int currentColumnSpan = 0;

        //如果已经放置了Cell则将该Cell的position放入该集合中
        List<Integer> haveSetCellPosition = new ArrayList<>();
        for (int i = 0; i < cells.size(); i++) {
            Rect rect = new Rect();

            boolean isCanBePush = false;            //是否能够进行放置
            while (!isCanBePush) {
                Cell cell = cells.get(i);

                //这一行放不下了,就进行换行操作
                if (currentColumn + cell.getColumnSpan() > column) {
                    currentRow++;
                    currentColumn = 0;
                }

                //判断该位置是否可以进行放置Cell
                if (canSetCell(haveSetCellPosition, currentRow, currentColumn, cell)) {
                    for (int p = 0; p < cell.getRowSpan(); p++) {
                        for (int q = 0; q < cell.getColumnSpan(); q++) {
                            int position = (currentRow + p) * mCellController.column() + currentColumn + q;
                            haveSetCellPosition.add(position);
                        }
                    }
                    rect.left = currentColumn * cellWidth;
                    rect.top = currentRow * cellHeight;
                    rect.right = rect.left + cell.getColumnSpan() * cellWidth;
                    rect.bottom = rect.top + cell.getRowSpan() * cellHeight;

                    currentColumn += cell.getColumnSpan();
                    isCanBePush = true;
                } else {
                    currentColumn++;
                }
            }
            cells.get(i).setRect(rect);
        }

        Collections.sort(haveSetCellPosition, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });

        int maxRow = -1;
        if (!haveSetCellPosition.isEmpty()) {
            Integer maxPosition = haveSetCellPosition.get(haveSetCellPosition.size() - 1);
            maxRow = maxPosition / mCellController.column();
        }
        if (maxRow != -1) {
            computeRowNum = maxRow + 1;
        } else {
            computeRowNum = currentRow + 1;
        }

        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        int heightPixels = getResources().getDisplayMetrics().heightPixels;

        int computeWidth = column * mCellController.cellWidth();
        int computeHeight = column * mCellController.cellHeight();
        ViewGroup.LayoutParams params = getLayoutParams();
        setLayoutParams(params);
//        params.width = Math.max(computeWidth + 2 * paddingX, widthPixels);
//        params.height = Math.max(computeHeight + 2 * paddingX, heightPixels);
        requestLayout();

        if (computeWidth > widthPixels)
            mMaxScrollerX = computeWidth - mMetrics.widthPixels + 2 * paddingX;
        else
            mMaxScrollerX = 0;

        if (computeHeight > heightPixels)
            mMaxScrollerY = Math.abs(heightPixels - computeHeight) + 2 * paddingY;
        else
            mMaxScrollerY = 0;
    }

    /**
     * 判断当前位置是否能够放下这个Cell
     *
     * @param haveSetCellPosition 已经放置Cell的Position集合
     * @param row                 行号
     * @param col                 列号
     * @param cell                要被放置的Cell对象
     * @return true可以被放置，false不可以被放置
     */
    private boolean canSetCell(List<Integer> haveSetCellPosition, int row, int col, Cell cell) {
        for (int i = 0; i < cell.getRowSpan(); i++) {
            for (int j = 0; j < cell.getColumnSpan(); j++) {
                int position = (row + i) * mCellController.column() + col + j;
                if (haveSetCellPosition.contains(position))
                    return false;
            }
        }

        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = 0;
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                if (mCellController != null) {
                    widthSize = Math.max(mCellController.column() * mCellController.cellWidth() + 2 * paddingX, mMetrics.widthPixels);
                }
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                if (mCellController != null) {
                    widthSize = mCellController.cellWidth() * mCellController.column() + 2 * paddingX;
                } else {
                    widthSize = MeasureSpec.getSize(widthSize);
                }
                break;
        }
        widthSize = (int) ((widthSize + paddingX * 2) * scale);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = 0;
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                if (mCellController != null)
                    heightSize = computeRowNum * mCellController.cellHeight();
                else
                    heightSize = MeasureSpec.getSize(heightMeasureSpec);
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                if (mCellController != null) {
                    heightSize = computeRowNum * mCellController.cellHeight();
                } else {
                    heightSize = MeasureSpec.getSize(heightMeasureSpec);
                }
                break;
        }
        heightSize = (int) ((heightSize + paddingY * 2) * scale);

//        Log.e("tag", "width : " + widthSize + " : " + heightSize);
        originWidth = widthSize;
        originHeight = heightSize;
        setMeasuredDimension(widthSize, heightSize);
    }

    PointF mLastPoint = new PointF();

//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//
//        if (h < mMetrics.heightPixels - getTop()) {
//            mMaxScrollerY = 0;
//        } else {
//            mMaxScrollerY = (h - (mMetrics.heightPixels - getTop()));
//        }
//        if (w < mMetrics.widthPixels - getLeft()) {
//            mMaxScrollerX = 0;
//        } else {
//            mMaxScrollerX = (w - (mMetrics.widthPixels - getLeft()));
//        }
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if(!mCellController.canBeTouch()) return false;

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                down(event);
                break;
            case MotionEvent.ACTION_MOVE:
                move(event);
                break;
            case MotionEvent.ACTION_UP:
                up();
                break;
        }

        return mScaleGestureDetector.onTouchEvent(event) | mGestureDetector.onTouchEvent(event);
    }

    private void up() {
        mVelocityTracker.computeCurrentVelocity(600, mViewConfiguration.getScaledMaximumFlingVelocity());
        float xVelocity = mVelocityTracker.getXVelocity();
        float yVelocity = mVelocityTracker.getYVelocity();

        if (Math.abs(xVelocity) < mViewConfiguration.getScaledMinimumFlingVelocity()) {
            xVelocity = 0;
        }
        if (Math.abs(yVelocity) < mViewConfiguration.getScaledMinimumFlingVelocity()) {
            yVelocity = 0;
        }
//                Log.e("tag","xVelocity : " + xVelocity + "...." + mViewConfiguration.getScaledMinimumFlingVelocity());
        //这里需要做的是在Scroller start的时候，进行调用invalidate方法
        mScroller.startScroll(getScrollX(), getScrollY(), -(int) (xVelocity / 3), -(int) (yVelocity / 3), 600);
        invalidate();

        mVelocityTracker.recycle();
    }

    private void move(MotionEvent event) {
        int dx = (int) (mLastPoint.x - event.getX());
        int dy = (int) (mLastPoint.y - event.getY());
        int computeX = getScrollX() + dx;
        int computeY = getScrollY() + dy;

        if (computeX < 0) {
            dx = -getScrollX();
        } else if (computeX > mMaxScrollerX) {
            dx = -(mMaxScrollerX - getScrollX());
        }

        if (computeY < 0) {
            dy = -getScrollY();
        } else if (computeY > mMaxScrollerY) {
            dy = -(mMaxScrollerY - getScrollY());
        }

        Log.e("tag", "computeY : " + computeY + " mMaxScrollerY : " + mMaxScrollerY );

        if (mOnScrollListener != null)
            mOnScrollListener.onTouchMove((int) (dx * scale), (int) (dy * scale));
        scrollBy((int) (dx * scale), (int) (dy * scale));

        mLastPoint.x = event.getX();
        mLastPoint.y = event.getY();

        mVelocityTracker.addMovement(event);
    }

    private void down(MotionEvent event) {
        mLastPoint.x = event.getX();
        mLastPoint.y = event.getY();
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }

        if (mVelocityTracker != null) mVelocityTracker.clear();

        mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.addMovement(event);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();

            if (x < 0) {
                x = 0;
            } else if (x > mMaxScrollerX) {
                x = mMaxScrollerX;
            }

            if (y < 0) {
                y = 0;
            } else if (y > mMaxScrollerY) {
                y = mMaxScrollerY;
            }

            if (mOnScrollListener != null)
                mOnScrollListener.onFillingMove(x, y);
            scrollTo(x, y);
            postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawContent(canvas);
    }


    private void drawContent(Canvas canvas) {
        canvas.save();
        canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);
        canvas.scale(scale, scale);
        canvas.translate(paddingX, paddingY);
        for (int i = 0; i < mCells.size(); i++) {
            Rect rect = mCells.get(i).getRect();
            canvas.drawRect(rect, mLinePaint);
            drawText(canvas, rect, mCells.get(i));
        }
        canvas.restore();
    }

    Rect textBounds = new Rect();

    private void drawText(Canvas canvas, Rect rect, Cell cell) {
        String string = cell.getData();
        mTextPaint.getTextBounds(string, 0, string.length(), textBounds);
        Paint.FontMetricsInt fontMetricsInt = mTextPaint.getFontMetricsInt();

        int startX = rect.left + (rect.width() - textBounds.width()) / 2;
        int startY = (rect.top + rect.bottom - fontMetricsInt.top - fontMetricsInt.bottom) / 2;
        canvas.drawText(string, startX, startY, mTextPaint);
    }

    float scale = 1.0f;

    float lastScale = 1.0f;

    GestureDetector mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            int newX = (int) (getScrollX() + e.getX() - paddingX * scale);
            int newY = (int) (getScrollY() + e.getY() - paddingY * scale);
            Cell clickCell = null;

            for (int i = 0; i < mCells.size(); i++) {
                Cell cell = mCells.get(i);
                Rect rect = cell.getRect();
                if (rect.left < rect.right && rect.top < rect.bottom && // check for empty first
                        newX >= rect.left * scale &&
                        newX < rect.right * scale &&
                        newY >= rect.top * scale &&
                        newY < rect.bottom * scale) {
                    clickCell = cell;
                    break;
                }
            }

            if (clickCell != null) {
                Toast.makeText(getContext(), "clickCell is " + clickCell.getData(), Toast.LENGTH_SHORT).show();
            }

            return true;
        }
    });

    ScaleGestureDetector mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (detector.getScaleFactor() == 1.0f) {
                //如果手势进行了重置，则重新进行判定
                lastScale = detector.getScaleFactor();
            }

            float dScale = detector.getScaleFactor() - lastScale;

//            Log.e("tag", "scale : " + scale + " dScale： " + dScale + " detector.getScaleFactor(: " + detector.getScaleFactor() + " lastScale: " + lastScale );
            if (dScale >= 0 && scale >= 3) {
                scale = 3.0f;
                mOnScrollListener.onScale(3.0f);
                return false;
            }
            if (scale <= 1 && dScale < 0) {
                scale = 1.0f;
                mOnScrollListener.onScale(1.0f);
                return false;
            }

            scale += (dScale);
            mOnScrollListener.onScale(scale);
            lastScale = detector.getScaleFactor();

            mMaxScrollerX = (int) (((getMeasuredWidth() - 2 * paddingX) * scale - getLeft() * scale) - mMetrics.widthPixels);
            if(originHeight * scale >  mMetrics.heightPixels - getTop()) {
                mMaxScrollerY = (int) (originHeight * scale - mMetrics.heightPixels + getTop());
            }else{
                mMaxScrollerY = 0;
            }
            postInvalidate();

            Log.e("tag","mMaxScrollerX : " + mMaxScrollerX + " mMaxScrollerY : " + mMaxScrollerY);

            return false;
        }
    });

    public void setScale(float scale) {
        this.scale = scale;
        ViewGroup.LayoutParams params = getLayoutParams();
        setLayoutParams(params);
        postInvalidate();
    }

    OnScrollListener mOnScrollListener;

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    public interface OnScrollListener {
        void onTouchMove(int dx, int dy);

        void onFillingMove(int scrollX, int scrollY);

        void onScale(float scale);
    }
}