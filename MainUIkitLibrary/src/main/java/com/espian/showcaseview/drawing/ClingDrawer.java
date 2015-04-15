package com.espian.showcaseview.drawing;

import android.view.MotionEvent;

import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.utils.ShowcaseAreaCalculator;

import android.graphics.Canvas;

/**
 * Created by curraa01 on 13/10/2013.
 */
public interface ClingDrawer extends ShowcaseAreaCalculator {

    float drawShowcase(Canvas canvas, float x, float y, float scaleMultiplier, float radius);

    int getShowcaseWidth();

    int getShowcaseHeight();

    void drawRect(int x, int y, int w, int h);

    boolean canTouch(MotionEvent motionEvent);

    void setConfigOpt(ShowcaseView.ConfigOptions configOpt);
}
