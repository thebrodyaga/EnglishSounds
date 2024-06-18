package com.thebrodyaga.feature.mainScreen.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("RestrictedApi")
public class AppBottomNavigationMenuView extends BottomNavigationMenuView {

    // huck for margin in center
    private static final float SHADOW_WEIGHT_PERCENT = 0.25f;

    private final List<Integer> tempChildWidths = new ArrayList<>();
    private final int activeItemMaxWidth;

    public AppBottomNavigationMenuView(@NonNull Context context) {
        super(context);
        final Resources res = getResources();
        activeItemMaxWidth =
                res.getDimensionPixelSize(R.dimen.design_bottom_navigation_active_item_max_width);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final MenuBuilder menu = getMenu();
        final int visibleCount = menu.getVisibleItems().size();
        final int totalCount = getChildCount();

        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        final int heightSpec = MeasureSpec.makeMeasureSpec(parentHeight, MeasureSpec.EXACTLY);

        if (isShifting(getLabelVisibilityMode(), visibleCount) && isItemHorizontalTranslationEnabled()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            final int width = MeasureSpec.getSize(widthMeasureSpec);

            // huck for margin in center
            final int shadowVisibleCount = visibleCount - 1;

            final int maxAvailable = width / (shadowVisibleCount == 0 ? 1 : shadowVisibleCount);
            final int childWidth = Math.min(maxAvailable, activeItemMaxWidth);
            tempChildWidths.clear();

            int extra = width - childWidth * visibleCount;
            for (int i = 0; i < totalCount; i++) {
                int tempChildWidth = 0;
                if (getChildAt(i).getVisibility() != View.GONE) {
                    if (i == 2) {
                        // huck for margin in center
                        tempChildWidth = ((int) (childWidth * SHADOW_WEIGHT_PERCENT));
                    } else {
                        tempChildWidth = childWidth;
                    }
                    if (extra > 0) {
                        tempChildWidth++;
                        extra--;
                    }
                }
                tempChildWidths.add(tempChildWidth);
            }
        }

        int totalWidth = 0;
        for (int i = 0; i < totalCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            child.measure(
                    MeasureSpec.makeMeasureSpec(tempChildWidths.get(i), MeasureSpec.EXACTLY), heightSpec);
            ViewGroup.LayoutParams params = child.getLayoutParams();
            params.width = child.getMeasuredWidth();
            totalWidth += child.getMeasuredWidth();
        }

        setMeasuredDimension(totalWidth, parentHeight);
    }
}
