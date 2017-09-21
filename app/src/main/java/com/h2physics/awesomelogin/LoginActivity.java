package com.h2physics.awesomelogin;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.ImageViewTarget;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    @BindViews({R.id.logo, R.id.first, R.id.second, R.id.third})
    List<ImageView> listElements;
    @BindView(R.id.pager)
    AnimatedViewPager mPager;
    @BindView(R.id.scrolling_background)
    ImageView mBackground;
    int[] screenSize = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initData();
    }

    private void initView(){
        ButterKnife.bind(this);
        screenSize = screenSize();
        for(ImageView element : listElements){
            @ColorRes int color = element.getId()!= R.id.logo ? R.color.white_transparent : R.color.color_logo_log_in;
            DrawableCompat.setTint(element.getDrawable(), ContextCompat.getColor(this,color));
        }

        Glide.with(this)
                .load(R.drawable.busy)
                .asBitmap()
                .override(screenSize[0] * 2, screenSize[1])
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(new ImageViewTarget<Bitmap>(mBackground) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        mBackground.setImageBitmap(resource);
                        mBackground.post(new Runnable() {
                            @Override
                            public void run() {
                                mBackground.scrollTo(-mBackground.getWidth()/2,0);
                                ObjectAnimator xAnimator = ObjectAnimator.ofFloat(mBackground, View.SCALE_X,4f, mBackground.getScaleX());
                                ObjectAnimator yAnimator=ObjectAnimator.ofFloat(mBackground,View.SCALE_Y,4f,mBackground.getScaleY());
                                AnimatorSet set = new AnimatorSet();
                                set.playTogether(xAnimator,yAnimator);
                                set.setDuration(getResources().getInteger(R.integer.duration));
                                set.start();
                            }
                        });
                        mPager.post(new Runnable() {
                            @Override
                            public void run() {
                                AuthAdapter adapter = new AuthAdapter(LoginActivity.this, getSupportFragmentManager(), mPager, mBackground, listElements);
                                mPager.setAdapter(adapter);
                            }
                        });
                    }
                });
    }

    private void initData(){

    }

    private int[] screenSize(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return new int[]{size.x, size.y};
    }
}
