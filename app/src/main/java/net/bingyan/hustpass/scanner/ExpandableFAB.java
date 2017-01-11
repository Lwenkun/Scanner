package net.bingyan.hustpass.scanner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Dimension;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.bingyan.hustpass.scanner.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import static android.animation.ObjectAnimator.ofFloat;
import static net.bingyan.hustpass.scanner.utils.DimenUtils.dp;
import static net.bingyan.hustpass.scanner.utils.DimenUtils.px;

/**
 * Created by lwenkun on 2016/12/22.
 */

public class ExpandableFAB extends RelativeLayout implements View.OnClickListener{

    private ItemViews itemViews = new ItemViews();

    private boolean isExpanded = false;
    private boolean isAnimationOver = true;

    private FloatingActionButton fab;
    private int topViewId;

    private FrameLayout f;

    public ExpandableFAB(Context context) {
        this(context, null);
    }

    public ExpandableFAB(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableFAB(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClipChildren(false);

        fab = new FloatingActionButton(getContext());
        fab.setImageResource(R.drawable.ic_add_black_24dp);
        fab.setCompatElevation(dp(2));
        FrameLayout.LayoutParams fabLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fabLayoutParams.setMargins(0, (int)px(5), 0, 0);
        fab.setOnClickListener(this);
        ViewUtil.setRandomId(fab);

        f = new FrameLayout(getContext());
        ViewUtil.setRandomId(f);
        RelativeLayout.LayoutParams fLayoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fLayoutParams.addRule(ALIGN_PARENT_BOTTOM, TRUE);
        fLayoutParams.addRule(ALIGN_PARENT_RIGHT, TRUE);
        f.addView(fab, fabLayoutParams);

        topViewId = f.getId();

        addView(f, fLayoutParams);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isExpanded && itemViews.isAllAnimOver()) {
            hide();
            return true;
        }
        return false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (isExpanded && isAnimationOver) {
            hide();
        }
    }

    void appendItemView(ItemView v) {

        RelativeLayout.LayoutParams ivLp = (RelativeLayout.LayoutParams) v.fImage.getLayoutParams();
        ivLp.addRule(ALIGN_RIGHT, f.getId());
        ivLp.addRule(ALIGN_LEFT, f.getId());
        if (itemViews.getCount() == 0) {
            ivLp.addRule(ABOVE, fab.getId());

        } else {
            ivLp.addRule(ABOVE, topViewId);
        }
        v.fImage.setLayoutParams(ivLp);
        addView(v.fImage);

        final RelativeLayout.LayoutParams tvLp = (RelativeLayout.LayoutParams) v.fText.getLayoutParams();
        tvLp.addRule(ALIGN_TOP , v.fImage.getId());
        tvLp.addRule(ALIGN_BOTTOM, v.fImage.getId());
        tvLp.addRule(LEFT_OF, v.fImage.getId());
        v.fText.setLayoutParams(tvLp);
        addView(v.fText);

        topViewId = v.fImage.getId();

    }

    public static class Item {
        public Item(int resourceId, String text) {
            this.resourceId = resourceId;
            this.text = text;
        }
        int resourceId;
        String text;
        int position = -1;
    }

    @Override
    public void onClick(View v) {
        if (v == fab) {
            if (!itemViews.isAllAnimOver()) return;
            if (isExpanded) {
                hide();
            } else {
                expand();
            }
        }
    }

    private void expand() {
        isExpanded = true;
        rotateFAB();
        popupItems();
    }

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void OnItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener l) {
       this.mListener = l;
    }

    private void hide() {
        isExpanded = false;
        rotateFAB();
        hideItems();
    }

    private void hideItems() {
        for (int i = 0; i < itemViews.getCount(); i++) {
            ItemView v = itemViews.getItemView(i);
            v.hide();
        }
    }

    private void popupItems() {
        for (int i = 0; i < itemViews.getCount(); i++) {
            ItemView v = itemViews.getItemView(i);
            v.show();
        }
    }

    private void rotateFAB() {
        isAnimationOver = false;
        ValueAnimator animator =
                ofFloat(fab, "rotation", fab.getRotation(), fab.getRotation() + 45);
        animator.setInterpolator(new OvershootInterpolator(3.0f));
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimationOver = true;
            }
        });
        animator.setDuration(250).start();
    }

    public void setItems(List<Item> items) {
        if (items == null || items.size() == 0) return;
        for (int i = 0; i < itemViews.getCount(); i++) {
            ItemView iv = itemViews.getItemView(i);
            removeView(iv.fText);
            removeView(iv.fImage);
            itemViews.removeItemView(iv);
        }
        for (Item item : items) {
            item.position = items.indexOf(item);
            ItemView v = new ItemView(item);
            itemViews.addItemView(v);
            appendItemView(v);
        }
    }

    class ItemView {

        boolean isAnimOver = true;

        Item item;
        int position;
        FrameLayout fText;
        FrameLayout fImage;
        CardView cvText;
        FloatingActionButton fabImage;

        AnimatorSet hideAnim;
        AnimatorSet showAnim;

        final float MIN = 0.0f;
        final float MAX = 1.0f;

        ItemView(Item item) {
            this.item = item;
            position = item.position;
            initView(item);
            initAnim();
        }

        void initAnim() {
            hideAnim = new AnimatorSet();
            showAnim = new AnimatorSet();
            hideAnim.playTogether(ObjectAnimator.ofFloat(fabImage, "scaleX", MAX, MIN),
                    ObjectAnimator.ofFloat(fabImage, "scaleY", MAX, MIN),
                    ObjectAnimator.ofFloat(cvText, "alpha", MAX, MIN),
                    ObjectAnimator.ofFloat(cvText, "translationX", 0, 20));
            hideAnim.setInterpolator(new AccelerateInterpolator());
            hideAnim.setDuration(250);
            hideAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    isAnimOver = false;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setVisibility(INVISIBLE);
                    isAnimOver = true;
                }
            });

            showAnim.playTogether(ObjectAnimator.ofFloat(fabImage, "scaleX", MIN, MAX),
                    ObjectAnimator.ofFloat(fabImage, "scaleY", MIN, MAX),
                    ObjectAnimator.ofFloat(cvText, "alpha", MIN, MAX),
                    ObjectAnimator.ofFloat(cvText, "translationX", 40, 0));
            showAnim.setInterpolator(new OvershootInterpolator(2.0f));
            showAnim.setDuration(250);
            showAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    setVisibility(VISIBLE);
                    isAnimOver = false;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
//                    setVisibility(INVISIBLE);
                    isAnimOver = true;
                }
            });
        }

        void setVisibility(int visibility) {
            cvText.setVisibility(visibility);
            fabImage.setVisibility(visibility);
        }

        void hide() {
            hideAnim.start();
        }

        void show() {
            showAnim.start();
        }

        void initView(Item item) {


            TextView tv = new TextView(getContext());
            tv.setTextSize(Dimension.SP, 14);
            tv.setText(item.text);
            tv.setPadding(px(7), px(3), px(7), px(3));
            tv.setTextColor(Color.WHITE);
            tv.setGravity(Gravity.CENTER);
            cvText = new CardView(getContext());
            cvText.setRadius(px(3));
            cvText.addView(tv);
            cvText.setCardBackgroundColor(Color.BLACK);
            FrameLayout.LayoutParams cvTextLayoutParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cvTextLayoutParams.setMargins(px(7), 0, 0, 0);
            cvTextLayoutParams.gravity = Gravity.CENTER_VERTICAL;
            fText = new FrameLayout(getContext());
            ViewUtil.setRandomId(fText);
            RelativeLayout.LayoutParams tvLayoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            fText.setLayoutParams(tvLayoutParams);
            fText.addView(cvText, cvTextLayoutParams);


            fabImage = new FloatingActionButton(getContext());
            fabImage.setImageResource(item.resourceId);
            fabImage.setRippleColor(Color.parseColor("#eeeeee"));
            fabImage.setSize(FloatingActionButton.SIZE_MINI);
            fabImage.setCompatElevation(10);
            fabImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExpandableFAB.this.hide();
                    if (mListener != null) {
                        mListener.OnItemClick(fabImage, position);
                    }
                }
            });
            FrameLayout.LayoutParams cvImageLayoutParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cvImageLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            cvImageLayoutParams.setMargins(0, px(7), 0, px(7));
            fImage = new FrameLayout(getContext());
            ViewUtil.setRandomId(fImage);
            RelativeLayout.LayoutParams ivLayoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            fImage.setLayoutParams(ivLayoutParams);
            fImage.addView(fabImage, cvImageLayoutParams);

            setVisibility(INVISIBLE);
        }

    }

    class ItemViews {

        List<ItemView> itemViews = new ArrayList<>();

        void addItemView(ItemView view) {
            itemViews.add(view);
        }

        void addItemView(int position, ItemView itemView) {
            itemViews.add(position, itemView);
        }

        ItemView getItemView(int position) {
            return itemViews.get(position);
        }

        void removeItemView(ItemView view) {
            itemViews.remove(view);
        }

        void removeItemView(int position) {
            itemViews.remove(position);
        }

        void setVisibility(int visibility) {
            for (ItemView v : itemViews) {
                v.setVisibility(visibility);
            }
        }

        int getCount() {
            return itemViews.size();
        }

        void clear() {
            itemViews.clear();
        }

        boolean isAllAnimOver() {
            for (ItemView v : itemViews) {
                if (! v.isAnimOver) {
                    return false;
                }
            }
            return true;
        }
    }




}
