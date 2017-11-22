package com.android.unideal.util;

import android.content.Context;
import com.android.unideal.rest.response.Category;
import com.android.unideal.rest.response.SubCategory;
import java.util.List;

/**
 * Created by ADMIN on 09-02-2017.
 */

public class CategoryList {
  public static List<Category> parentCategoryList(Context context) {
    String appMode = SessionManager.get(context).getLanguageMode();
    List<Category> categoryList = SessionManager.get(context).getCategoryList();
    for (Category category : categoryList) {
      category.setAppMode(appMode);
    }
    return categoryList;
  }

  public static List<SubCategory> subCategory(Context context) {
    String appMode = SessionManager.get(context).getLanguageMode();
    List<SubCategory> subCategoryList = SessionManager.get(context).getSubCategoryList();
    for (SubCategory category : subCategoryList) {
      category.setAppMode(appMode);
    }
    return subCategoryList;
  }

  public static List<Category> favourableCategory(Context context) {
    String appMode = SessionManager.get(context).getLanguageMode();
    List<Category> favourableCategory = SessionManager.get(context).getFavourableList();
    for (Category category : favourableCategory) {
      category.setAppMode(appMode);
    }
    return favourableCategory;
  }
}
