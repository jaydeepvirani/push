package com.special.ResideMenu;

import java.util.UUID;

/**
 * Created by bhavdip on 30/9/16.
 * This is the raw class that contents
 * the menu title,icon for items, and Item Type
 */

public class MenuItem {
  private String menuId;
  private int menuName;
  private int resourceId;
  private boolean isSelected;
  private MenuType menuType;

  public MenuItem(int menuName, int resourceId, MenuType type) {
    this.menuId = UUID.randomUUID().toString();
    this.menuName = menuName;
    this.resourceId = resourceId;
    this.menuType = type;
  }

  public String getMenuId() {
    return menuId;
  }

  public int getMenuName() {
    return menuName;
  }

  public void setMenuName(int menuName) {
    this.menuName = menuName;
  }

  public boolean isSelected() {
    return isSelected;
  }

  public MenuItem setSelected(boolean selected) {
    isSelected = selected;
    return this;
  }

  public MenuType getMenuType() {
    return menuType;
  }

  public MenuItem setMenuType(MenuType menuType) {
    this.menuType = menuType;
    return this;
  }
}
