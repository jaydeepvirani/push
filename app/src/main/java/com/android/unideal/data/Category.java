package com.android.unideal.data;

/**
 * Created by bhavdip on 10/1/16.
 */

public enum Category {
  TAXATION("Taxation"), REAL_ESTATE("Real Estate"), EDUCATION("Education"), HOUSING(
      "Housing"), TAILORING("Tailoring");

  private String categoryName;

  Category(String category) {
    this.categoryName = category;
  }

  public String getCategoryName() {
    return categoryName;
  }
}
