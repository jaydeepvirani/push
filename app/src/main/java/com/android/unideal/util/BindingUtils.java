package com.android.unideal.util;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.unideal.R;
import com.squareup.picasso.Picasso;

public class BindingUtils {

  @BindingAdapter({ "bind:imageUrl" })
  public static void profileImage(ImageView view, String imageUrl) {
    if (!TextUtils.isEmpty(imageUrl) && view != null) {
      Picasso.with(view.getContext()).load(imageUrl).placeholder(R.color.colorAlto).into(view);
    }
  }

  @BindingAdapter({ "bind:textViewColor" })
  public static void setTextViewColor(TextView textView, int colorCode) {
    textView.setTextColor(colorCode);
  }
}
