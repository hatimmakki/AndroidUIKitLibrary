package com.espian.showcaseview.drawing;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import android.view.MotionEvent;

import com.espian.showcaseview.ShowcaseView;
import com.manuelpeinado.refreshactionitem.R;

/**
 * Created by curraa01 on 13/10/2013.
 */
public class ClingDrawerImpl implements ClingDrawer {

    private Paint mEraser;
    private Drawable mShowcaseDrawable;
    private Rect mShowcaseRect;
    private ShowcaseView.ConfigOptions options;

    public ClingDrawerImpl(Resources resources, int showcaseColor) {
        PorterDuffXfermode mBlender = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        mEraser = new Paint();
        mEraser.setColor(0xFFFFFF);
        mEraser.setAlpha(0);
        mEraser.setXfermode(mBlender);
        mEraser.setAntiAlias(true);

        mShowcaseDrawable = resources.getDrawable(R.drawable.cling_bleached);
        mShowcaseDrawable.setColorFilter(showcaseColor, PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public float drawShowcase(Canvas canvas, float x, float y, float scaleMultiplier, float radius) {
        Matrix mm = new Matrix();
        mm.postScale(scaleMultiplier, scaleMultiplier, x, y);
        canvas.setMatrix(mm);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            canvas.drawCircle(x, y - 25, radius - 25, mEraser);
        }

        if (options != null && options.imageDrawables != null && options.imageDrawables.size() > 0) {
            //draw image
            float startY = y - 25;
            for (Drawable dr : options.imageDrawables) {
                Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
                //find best position
                float dr_x= (canvas.getWidth() - dr.getIntrinsicWidth()) / 2 ;//
                if (x + radius - 25 >= canvas.getWidth()) {
                    //center
                    dr_x = (canvas.getWidth() - dr.getIntrinsicWidth()) / 2;
                } else {
                    boolean onRight = x > canvas.getWidth() / 2;
                    if (onRight) {
                        dr_x = (x - (dr.getIntrinsicWidth() / 2 - (canvas.getWidth() - x))) - dr.getIntrinsicWidth() / 2;
                    } else {
                        if (x < dr.getIntrinsicWidth() / 2) {
                            //left limit
                            dr_x = dr.getIntrinsicWidth() / 2;
                        }else {
                            dr_x = x;
                        }

                        if (dr.getIntrinsicWidth() + dr_x > canvas.getWidth()) {
                            dr_x = (canvas.getWidth() - dr.getIntrinsicWidth()) / 2;
                        }
                    }
                }
//                canvas.drawBitmap(bitmap, 50, 120, null);
                canvas.drawBitmap(bitmap, dr_x, startY + 5, null);
                startY += dr.getIntrinsicHeight() + 20;
            }
        }
        mShowcaseDrawable.setBounds(mShowcaseRect);
        mShowcaseDrawable.draw(canvas);

        canvas.setMatrix(new Matrix());

        return -10000;
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

    }

    @Override
    public boolean canTouch(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void setConfigOpt(ShowcaseView.ConfigOptions configOpt) {
        this.options = configOpt;
    }

}
