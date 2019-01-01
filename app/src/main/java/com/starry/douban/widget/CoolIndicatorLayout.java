
/*
 * Copyright (C)  Justson(https://github.com/Justson/AgentWeb)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.starry.douban.widget;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.just.agentweb.AgentWebUtils;
import com.just.agentweb.BaseIndicatorView;
import com.starry.coolindicator.CoolIndicator;

/**
 * @author Starry Jerry
 * @since 2018/11/30.
 */
public class CoolIndicatorLayout extends BaseIndicatorView {

    private CoolIndicator mCoolIndicator = null;


    public CoolIndicatorLayout(Context context) {
        this(context, null);
    }

    public CoolIndicatorLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CoolIndicatorLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCoolIndicator = CoolIndicator.create((Activity) context);
        mCoolIndicator.setProgressDrawable(context.getResources().getDrawable(com.starry.coolindicator.R.drawable.default_drawable_indicator, context.getTheme()));
        this.addView(mCoolIndicator, offerLayoutParams());

    }

    @Override
    public void show() {
        this.setVisibility(View.VISIBLE);
        mCoolIndicator.start();
    }

    @Override
    public void setProgress(int newProgress) {
    }

    @Override
    public void hide() {
        mCoolIndicator.complete();
    }

    @Override
    public LayoutParams offerLayoutParams() {
        return new FrameLayout.LayoutParams(-1, AgentWebUtils.dp2px(getContext(), 3));
    }
}
