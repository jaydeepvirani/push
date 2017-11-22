package com.special.ResideMenu;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResideMenuItem extends LinearLayout implements View.OnClickListener {

  private ImageView iv_icon;
  private TextView tv_title;
  private MenuItem mMenuItem;
  private View highLightView;
  private MenuItemClickListener itemClickListener;
  private LinearLayout mRootLayout;

  private Context context;
  public ResideMenuItem(Context context, MenuItem menuItem) {
    super(context);
    initViews(context, menuItem);
  }

  private void initViews(Context context, MenuItem menuItem) {
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.residemenu_item, this);
    this.mMenuItem = menuItem;
    this.mRootLayout = (LinearLayout) findViewById(R.id.containerMenuItem);
    this.mRootLayout.setOnClickListener(this);
    iv_icon = (ImageView) findViewById(R.id.iv_icon);
    tv_title = (TextView) findViewById(R.id.tv_title);
    highLightView = (View) findViewById(R.id.viewHighLight);
    iv_icon.setVisibility(View.GONE);
    tv_title.setText(mMenuItem.getMenuName());
    //Refresh at the time of initViews
    refreshMenuItem();
  }

  public TextView textView() {
    return tv_title;
  }


  public ResideMenuItem registerMenuListener(MenuItemClickListener listener) {
    this.itemClickListener = listener;
    return this;
  }

  /**
   * set the icon color;
   */
  public void setIcon(int icon) {
    iv_icon.setImageResource(icon);
  }

  /**
   * set the title with resource
   * ;
   */
  public void setTitle(int title) {
    tv_title.setText(title);
  }

  public MenuItem getMenuItem() {
    return mMenuItem;
  }

  public void refreshMenuItem() {
    if (mMenuItem.isSelected()) {
      highLightView.setVisibility(View.VISIBLE);
    } else {
      highLightView.setVisibility(View.INVISIBLE);
    }
  }

  /**
   * set the title with string;
   */
  public void setTitle(String title) {
    tv_title.setText(title);
  }

  @Override
  public void onClick(View v) {
    if (itemClickListener != null) {
      itemClickListener.onMenuItemClick(mMenuItem);
    }
  }
}
