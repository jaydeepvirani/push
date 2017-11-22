package com.android.unideal.questioner.questionhelper;

import com.special.ResideMenu.MenuType;

/**
 * Created by ADMIN on 11-10-2016.
 */

public class MenuTypeImp implements MenuType<ItemType> {

  private ItemType itemType;

  public MenuTypeImp(ItemType itemType) {
    this.itemType = itemType;
  }

  @Override
  public ItemType getType() {
    return itemType;
  }
}