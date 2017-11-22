package com.android.unideal.questioner.questionhelper;

import android.app.Activity;
import android.view.View;

import com.special.ResideMenu.MenuItem;
import java.util.ArrayList;
import java.util.List;

import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.view.MaterialIntroView;

/**
 * Created by ADMIN on 11-10-2016.
 */

public class QuestionerProvider {
  static MaterialIntroView materialIntroView;
  private static Activity activity;
  private static List<MenuItem> menuItemList = new ArrayList<>();
  private static QuestionerProvider menuProvider = new QuestionerProvider(activity);

  static {
    for (ItemType type :ItemType.values()) {
      menuItemList.add(new MenuItem(type.getItemName(), -1, new MenuTypeImp(type)));
    }

    //default home selected
  }

  private QuestionerProvider(Activity activity) {
    this.activity = activity;
  }

  public static QuestionerProvider getProvider() {
    return menuProvider;
  }

  public List<MenuItem> getMenuItemList() {
    return menuItemList;
  }
}

