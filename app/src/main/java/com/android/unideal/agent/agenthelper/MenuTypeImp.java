package com.android.unideal.agent.agenthelper;

import com.special.ResideMenu.MenuType;

/**
 * Created by bhavdip on 10/1/16.
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
