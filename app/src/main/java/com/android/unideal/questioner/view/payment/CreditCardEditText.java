package com.android.unideal.questioner.view.payment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import com.android.unideal.R;

/**
 * Created by bhavdip on 26/12/16.
 */

public class CreditCardEditText extends AppCompatEditText {

  private static final String TAG = "CreditCardEditText";
  private final int mDefaultDrawableResId = R.drawable.card_back; //default credit card image
  private int mCurrentDrawableResId = 0;
  private Drawable mCurrentDrawable;
  private CardPatternHelper mCardPatternHelper;

  public CreditCardEditText(Context context) {
    super(context);
    init();
  }

  public CreditCardEditText(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public CreditCardEditText(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    mCardPatternHelper = new CardPatternHelper();
  }

  public CardPatternHelper getCardHelper() {
    return mCardPatternHelper;
  }

  @Override
  protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
    if (mCardPatternHelper == null) {
      init();
    }
    int mDrawableResId = mCardPatternHelper.checkCardType(text);
    if (mDrawableResId > 0 && mDrawableResId != mCurrentDrawableResId) {
      mCurrentDrawableResId = mDrawableResId;
    } else if (mDrawableResId == 0) {
      mCurrentDrawableResId = mDefaultDrawableResId;
    }
    mCurrentDrawable = ContextCompat.getDrawable(getContext(), mCurrentDrawableResId);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (mCurrentDrawable == null) {
      return;
    }
    int rightOffset = 0;
    if (getError() != null && getError().length() > 0) {
      rightOffset = (int) getResources().getDisplayMetrics().density * 32;
    }
    int right = getWidth() - getPaddingRight() - rightOffset;
    int top = getPaddingTop();
    int bottom = getHeight() - getPaddingBottom();
    float ratio = (float) mCurrentDrawable.getIntrinsicWidth()
        / (float) mCurrentDrawable.getIntrinsicHeight();
    //int left = right - mCurrentDrawable.getIntrinsicWidth(); //If images are correct size.
    int left = (int) (right - ((bottom - top) * ratio)); //scale image depeding on height available.
    mCurrentDrawable.setBounds(left, top, right, bottom);
    mCurrentDrawable.draw(canvas);
  }
}
