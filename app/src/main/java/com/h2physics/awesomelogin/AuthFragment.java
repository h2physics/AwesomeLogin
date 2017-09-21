package com.h2physics.awesomelogin;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by YukiNoHara on 9/12/2017.
 */

public abstract class AuthFragment extends Fragment{
    private static final String TAG = AuthFragment.class.getSimpleName();
    protected Callback callback;

    @BindView(R.id.caption)
    protected VerticalTextView caption;

    @BindView(R.id.root)
    protected ViewGroup parent;

    protected boolean lock;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(getContentLayout(), container, false);
        ButterKnife.bind(this, root);
        KeyboardVisibilityEvent.setEventListener(getActivity(), new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                callback.scale(isOpen);
                if (!isOpen){
                    clearFocus();
                }
            }
        });
        return root;
    }

    public void setCallback(@NonNull Callback callback){
        this.callback = callback;
    }

    @OnClick(R.id.caption)
    public void unfold(){
        if(!lock){
            caption.setVerticalText(false);
            caption.requestLayout();
            Rotate rotate = new Rotate();
            rotate.setStartAngle(-90f);
            rotate.setEndAngle(0f);
            rotate.addTarget(caption);
            final TransitionSet set = new TransitionSet();
            set.setDuration(300);
            ChangeBounds changeBounds = new ChangeBounds();
            set.addTransition(changeBounds);
            set.addTransition(rotate);
            TextSizeTransition textSizeTransition = new TextSizeTransition();
            textSizeTransition.addTarget(caption);
            set.addTransition(textSizeTransition);
            set.setOrdering(TransitionSet.ORDERING_TOGETHER);
            caption.post(new Runnable() {
                @Override
                public void run() {
                    TransitionManager.beginDelayedTransition(parent, set);
                    caption.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.unfolded_size));
                    caption.setTextColor(ContextCompat.getColor(getContext(),R.color.color_label));
                    caption.setTranslationX(0);
                    ConstraintLayout.LayoutParams params = getParams();
                    params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
                    params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                    params.verticalBias = 0.78f;
                    caption.setLayoutParams(params);
                }
            });
            callback.show(this);
            lock = true;

        }
    }

    protected ConstraintLayout.LayoutParams getParams(){
        return ConstraintLayout.LayoutParams.class.cast(caption.getLayoutParams());
    }

    @LayoutRes
    public abstract int getContentLayout();
    public abstract void fold();
    public abstract void clearFocus();

    interface Callback{
        void show(AuthFragment fragment);
        void scale(boolean hasFocus);
    }
}
