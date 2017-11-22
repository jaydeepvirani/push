package com.android.unideal.agent.agenthelper;

import com.special.ResideMenu.MenuItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhavdip on 30/9/16.
 */

public class AgentMenuProvider {
  private static List<MenuItem> menuItemList = new ArrayList<>();
  private static AgentMenuProvider menuProvider = new AgentMenuProvider();

  static {
    for (ItemType type : ItemType.values()) {
      menuItemList.add(new MenuItem(type.getItemName(), -1, new MenuTypeImp(type)));
    }

    //default home selected
    menuItemList.get(0).setSelected(true);
  }

  private AgentMenuProvider() {
  }

  public static AgentMenuProvider getProvider() {
    return menuProvider;
  }

  public List<MenuItem> getMenuItemList() {
    return menuItemList;
  }
}
