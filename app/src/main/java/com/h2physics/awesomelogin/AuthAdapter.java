package com.h2physics.awesomelogin;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by YukiNoHara on 9/12/2017.
 */

public class AuthAdapter extends FragmentStatePagerAdapter implements AuthFragment.Callback {
    private static final String TAG = AuthAdapter.class.getSimpleName();

    private Context mContext;
    private AnimatedViewPager pager;
    private SparseArray<AuthFragment> authArray;
    private List<ImageView> sharedElements;
    private ImageView authBackground;
    private float factor;

    public AuthAdapter(Context context, FragmentManager manager, AnimatedViewPager pager,
                       ImageView authBackground, List<ImageView> sharedElements) {
        super(manager);
        this.mContext = context;
        this.pager = pager;
        this.authBackground = authBackground;
        this.sharedElements = sharedElements;
        this.authArray = new SparseArray<>(getCount());
        pager.setDuration(context.getResources().getInteger(R.integer.duration));
        final float textSize = pager.getResources().getDimension(R.dimen.folded_size);
        final float textPadding = pager.getResources().getDimension(R.dimen.folded_label_padding);
        factor = 1 - (textSize + textPadding) / pager.getWidth();
    }

    @Override
    public AuthFragment getItem(int position) {
        AuthFragment fragment = authArray.get(position);
        if (fragment == null){
            fragment = position != 1 ? new LoginFragment() : new SignUpFragment();
            authArray.put(position, fragment);
            fragment.setCallback(this);
        }
        return fragment;
    }

    @Override
    public float getPageWidth(int position) {
        return factor;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public void show(AuthFragment fragment) {
        final int index = authArray.keyAt(authArray.indexOfValue(fragment));
        pager.setCurrentItem(index, true);
        shiftSharedElements(getPagerOffsetX(fragment), index == 1);
        for (int jIndex = 0; jIndex < authArray.size(); jIndex++){
            if (jIndex != index){
                authArray.get(jIndex).fold();
            }
        }
    }

    @Override
    public void scale(boolean hasFocus) {
        final float scale = hasFocus ? 1f : 1.4f;
        final float logoScale = hasFocus ? 0.75f : 1f;
        View logo = sharedElements.get(0);

        AnimatorSet scaleAnimation = new AnimatorSet();
        scaleAnimation.playTogether(ObjectAnimator.ofFloat(logo, View.SCALE_X, logoScale));
        scaleAnimation.playTogether(ObjectAnimator.ofFloat(logo, View.SCALE_Y, logoScale));
        scaleAnimation.playTogether(ObjectAnimator.ofFloat(authBackground, View.SCALE_X, scale));
        scaleAnimation.playTogether(ObjectAnimator.ofFloat(authBackground, View.SCALE_Y, scale));
        scaleAnimation.setDuration(200);
        scaleAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleAnimation.start();
    }

    private float getPagerOffsetX(AuthFragment fragment){
        if (fragment.getView() == null){
            return -1;
        }
        int pageWidth = fragment.getView().getWidth();
        return pageWidth - pageWidth * factor;
    }

    private void shiftSharedElements(float pagerOffsetX, boolean forward){
        Context context = pager.getContext();
        AnimatorSet animatorSet = new AnimatorSet();
        for (View view : sharedElements){
            float translationX = forward ? pagerOffsetX : -pagerOffsetX;
            float temp = view.getWidth() / 3f;
            translationX -= forward ? temp : -temp;
            ObjectAnimator shift = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0, translationX);
            animatorSet.playTogether(shift);
        }

        int color = ContextCompat.getColor(context, forward ? R.color.color_logo_log_in : R.color.color_logo_sign_up);
        DrawableCompat.setTint(sharedElements.get(0).getDrawable(), color);

        //Scroll the background by X
        int offset = authBackground.getWidth() / 2;
        ObjectAnimator scrollAnimator = ObjectAnimator.ofInt(authBackground, "scrollX", forward ? offset : -offset);
        animatorSet.playTogether(scrollAnimator);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setDuration(context.getResources().getInteger(R.integer.duration) / 2);
        animatorSet.start();
    }
}
