package com.espian.showcaseview.drawing;

import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;

import com.espian.showcaseview.ShowcaseView;
import com.manuelpeinado.refreshactionitem.R;

/**
 * Created by liupeng on 12/19/14.
 */
public class ClingRectDrawerImpl implements ClingDrawer {
    private Paint mEraser;
    private Drawable mShowcaseDrawable;
    private Rect mShowcaseRect;
    public int rect_x, rect_y, rect_w, rect_h;
    private Resources mRes;
    private ShowcaseView.ConfigOptions options;

    public ClingRectDrawerImpl(Resources resources, int showcaseColor) {
        mRes = resources;
        PorterDuffXfermode mBlender = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        mEraser = new Paint();
        mEraser.setColor(Color.RED);
        mEraser.setAlpha(0);
        mEraser.setXfermode(mBlender);
        mEraser.setAntiAlias(true);

        mShowcaseDrawable = resources.getDrawable(R.drawable.case_rect_bg);
        mShowcaseDrawable.setColorFilter(showcaseColor, PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public float drawShowcase(Canvas canvas, float x, float y, float scaleMultiplier, float radius) {
        if (options != null && options.hasNoTargetView) {
            //draw tips image
            int startY = 0;
            int tempTotalHeight = 0;
            //cl total height
            for (Drawable dr : options.imageDrawables) {
                tempTotalHeight += dr.getIntrinsicHeight();
            }
            //draw
            if (tempTotalHeight != 0) {
                startY = (canvas.getHeight() - tempTotalHeight) / 2;
                for (Drawable dr : options.imageDrawables) {
                    Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
                    canvas.drawBitmap(bitmap, (canvas.getWidth() - dr.getIntrinsicWidth()) / 2, startY + 5, null);
                    startY += dr.getIntrinsicHeight() + 10;
                }
            }
            mShowcaseDrawable.draw(canvas);
            return startY;
        }else{
            int halfW = getShowcaseWidth() / 2;
            int halfH = getShowcaseHeight() / 2;
            int left = (int) (x - halfW);
            int top = (int) (y - halfH);

            canvas.drawRect(new Rect(left, top,
                    left + getShowcaseWidth(),
                    top + getShowcaseHeight()), mEraser);

            mShowcaseDrawable.setBounds(left, top,
                    left + getShowcaseWidth(),
                    top + getShowcaseHeight());
            int startY = -1000;
            int offsetY = 75;
            if (options != null && options.imageDrawables != null && options.imageDrawables.size() > 0) {
                //draw image
                for (Drawable dr : options.imageDrawables) {
                    if (startY == -1000) {
                        startY = top + (rect_h - dr.getIntrinsicHeight()) / 2 + offsetY;
                    }
                    Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
                    //find best position
                    int dr_x = (canvas.getWidth() - dr.getIntrinsicWidth()) / 2;
                    if (rect_w + 20 >= canvas.getWidth()) {
                        //center
                        dr_x = (canvas.getWidth() - dr.getIntrinsicWidth()) / 2;
                    } else {
                        int center_x = rect_x;
                        boolean onRight = center_x > canvas.getWidth() / 2;
                        if (onRight) {
                            dr_x = (center_x - (dr.getIntrinsicWidth() / 2 - (canvas.getWidth() - center_x))) - dr.getIntrinsicWidth() / 2;
                        } else {
                            if (center_x < dr.getIntrinsicWidth() / 2) {
                                //left limit
                                dr_x = dr.getIntrinsicWidth() / 2;
                            }
                        }
                    }
                    canvas.drawBitmap(bitmap, dr_x, startY + 5, null);
                    startY += dr.getIntrinsicHeight() + 10;
                }
            }
            mShowcaseDrawable.draw(canvas);
            return startY;
        }

    }

    @Override
    public int getShowcaseWidth() {
        return mShowcaseDrawable.getIntrinsicWidth();
    }

    @Override
    public int getShowcaseHeight() {
        return mShowcaseDrawable.getIntrinsicHeight();
    }

    /**
     * Creates a {@link android.graphics.Rect} which represents the area the showcase covers. Used
     * to calculate where best to place the text
     *
     * @return true if voidedArea has changed, false otherwise.
     */
    public boolean calculateShowcaseRect(float x, float y) {

        if (mShowcaseRect == null) {
            mShowcaseRect = new Rect();
        }

        int cx = (int) x, cy = (int) y;
        int dw = getShowcaseWidth();
        int dh = getShowcaseHeight();

        if (mShowcaseRect.left == cx - dw / 2 && mShowcaseRect.top == cy - dh / 2) {
            return false;
        }

        Log.d("ShowcaseView", "Recalculated");

        mShowcaseRect.left = cx - dw / 2;
        mShowcaseRect.top = cy - dh / 2;
        mShowcaseRect.right = cx + dw / 2;
        mShowcaseRect.bottom = cy + dh / 2;

        return true;

    }

    @Override
    public Rect getShowcaseRect() {
        return mShowcaseRect;
    }

    @Override
    public void drawRect(int x, int y, int w, int h) {
        rect_x = x;
        rect_y = y;
        rect_w = w - 20;
        rect_h = h - 10;
        Bitmap bitmap = ((BitmapDrawable) mShowcaseDrawable).getBitmap();
        mShowcaseDrawable = new BitmapDrawable(mRes, Bitmap.createScaledBitmap(bitmap, rect_w, rect_h, true));
    }

    @Override
    public boolean canTouch(MotionEvent motionEvent) {
        int offset = 5;
        return (motionEvent.getRawX() < rect_x + offset || motionEvent.getRawX() + offset > rect_x + rect_w)
                || (motionEvent.getRawY() < rect_y + offset || motionEvent.getRawY() > rect_y + rect_h + offset);
    }

    @Override
    public void setConfigOpt(ShowcaseView.ConfigOptions configOpt) {
        options = configOpt;
    }
}
