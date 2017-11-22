package com.android.unideal.agent.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.unideal.R;
import com.android.unideal.agent.adapter.CategoryAdapter;
import com.android.unideal.databinding.FilterBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.flurry.FlurryManager;
import com.android.unideal.rest.response.Category;
import com.android.unideal.rest.response.SubCategory;
import com.android.unideal.util.CategoryList;
import com.android.unideal.util.DateTimeUtils;
import com.android.unideal.util.SessionManager;
import com.android.unideal.util.datetimehelper.DateTimerHelper;
import java.util.Calendar;
import java.util.List;
import org.florescu.android.rangeseekbar.RangeSeekBar;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FilterActivity extends AppCompatActivity
    implements DateTimerHelper.onDateTimeListener {
  public static final String DATA_POSTING_DATE = "posting_date";
  public static final String DATA_EXPIRY_DATE = "expiry_date";
  public static final String DATA_CATEGORY_ID = "category";
  public static final String DATA_SUB_CATEGORY_ID = "subcategory";
  public static final String DATA_START_RANG = "start_rang";
  public static final String DATA_END_RANG = "end_rang";
  private static int MIN_FEE_RANGE;
  private static int MAX_FEE_RANGE;
  private RangeSeekBar<Integer> rangeSeekBar;
  private FilterBinding mDataBinding;
  private boolean flagSetDate = false;
  private Calendar selectedExpiry, selectedPosting;
  private List<Category> categories;
  private List<SubCategory> subCategories;
  private Category mCategory = null;
  private SubCategory mSubCategory = null;
  private CategoryAdapter<Category> mCategoryAdapter;
  private CategoryAdapter<SubCategory> mSubCategoryAdapter;
  private int categoryPosition = -1, subCategoryPosition = -1;
  private boolean foundPosition = false;
  //android-spinner-avoid-on item selected-calls-during-initialization
  private boolean userIsInteracting = false;
  private DateTimerHelper mDateTimerHelper;

  public static Intent startFilterActivity(Activity activity, String startDate, String endDate,
      String startFee, String endFee, String category, String subCategory) {
    Intent intent = new Intent(activity, FilterActivity.class);
    if (!TextUtils.isEmpty(startDate)) {
      intent.putExtra(DATA_POSTING_DATE, startDate);
    }
    if (!TextUtils.isEmpty(endDate)) {
      intent.putExtra(DATA_EXPIRY_DATE, endDate);
    }
    if (!TextUtils.isEmpty(startFee)) {
      intent.putExtra(DATA_START_RANG, Integer.parseInt(startFee));
    }
    if (!TextUtils.isEmpty(endFee)) {
      intent.putExtra(DATA_END_RANG, Integer.parseInt(endFee));
    }
    if (!TextUtils.isEmpty(category)) {
      intent.putExtra(DATA_CATEGORY_ID, Integer.parseInt(category));
    }
    if (!TextUtils.isEmpty(subCategory)) {
      intent.putExtra(DATA_SUB_CATEGORY_ID, Integer.parseInt(subCategory));
    }
    return intent;
  }

  private void setDateToText(TextView textView, Calendar selectCalendar) {
    if (selectCalendar == null) {
      textView.setText(R.string.ddmmyy);
    } else {
      String expiryDateString =
          DateTimeUtils.getDateToString(selectCalendar.getTime(), DateTimeUtils.DATE_FORMAT);
      textView.setText(expiryDateString);
    }
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_filter);
    MIN_FEE_RANGE = SessionManager.get(this).getMinFee();
    MAX_FEE_RANGE = SessionManager.get(this).getMaxFee();
    setUpCategorySpinner();
    setUpSubCategorySpinner();
    bindRangBar();
    setDefaultData(getIntent());
    mDataBinding.postingDateLayout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        flagSetDate = false;
        openDatePickerDialog();
      }
    });
    mDataBinding.expiryDateLayout.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {
        flagSetDate = true;
        openDatePickerDialog();
      }
    });
    mDataBinding.apply.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        validateInfo();
      }
    });
    mDataBinding.reset.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        selectedExpiry = null;
        selectedPosting = null;
        setDateToText(mDataBinding.expiryDate, null);
        setDateToText(mDataBinding.postingDate, null);
        rangeSeekBar.setSelectedMaxValue(MAX_FEE_RANGE);
        rangeSeekBar.setSelectedMinValue(MIN_FEE_RANGE);
        mDataBinding.category.setSelection(0);
        mDataBinding.subCategory.setSelection(0);
        foundPosition = false;
        mSubCategory = null;
        mCategory = null;
        categoryPosition = -1;
        subCategoryPosition = -1;
      }
    });
    mDataBinding.close.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (mDateTimerHelper != null) {
      mDateTimerHelper.closeAllDialog();
    }
  }

  @Override
  public void onUserInteraction() {
    super.onUserInteraction();
    userIsInteracting = true;
  }

  private void setDefaultData(Intent intent) {
    String startDate = intent.getStringExtra(DATA_POSTING_DATE);
    String endDate = intent.getStringExtra(DATA_EXPIRY_DATE);
    final int categoryId = intent.getIntExtra(DATA_CATEGORY_ID, -1);
    final int subCategoryId = intent.getIntExtra(DATA_SUB_CATEGORY_ID, -1);
    int startRang = intent.getIntExtra(DATA_START_RANG, MIN_FEE_RANGE);
    int endRang = intent.getIntExtra(DATA_END_RANG, MAX_FEE_RANGE);

    // set default date
    selectedPosting =
        DateTimeUtils.convertToCalender(startDate, DateTimeUtils.DATE_FORMAT_DD_MM_YYYY);
    selectedExpiry = DateTimeUtils.convertToCalender(endDate, DateTimeUtils.DATE_FORMAT_DD_MM_YYYY);
    setDateToText(mDataBinding.postingDate, selectedPosting);
    setDateToText(mDataBinding.expiryDate, selectedExpiry);
    // set default rang
    rangeSeekBar.setSelectedMaxValue(endRang);
    rangeSeekBar.setSelectedMinValue(startRang);

    // set default/last time choose category
    if (categoryId == -1) {
      categoryPosition = 0;
    } else {
      for (int i = 0; i < categories.size(); i++) {
        Category category = categories.get(i);
        if (category.getCategoryId() != null && category.getCategoryId() == categoryId) {
          categoryPosition = i;
          break;
        }
      }
    }
    mDataBinding.category.setSelection(categoryPosition);

    // set default/last time choose category
    if (subCategoryId == -1) {
      subCategoryPosition = 0;
    } else {
      //This will explicitly filter sub category from all sub category list from server
      Observable<SubCategory> resultsObjectObservable = Observable.from(subCategories);
      resultsObjectObservable.map(new Func1<SubCategory, SubCategory>() {
        @Override
        public SubCategory call(SubCategory subCategory) {
          if (subCategory.getCategory_id() == -1 || subCategory.getCategory_id() == categoryId) {
            return subCategory;
          }
          return null;
        }
      }).doOnCompleted(new Action0() {
        @Override
        public void call() {
          mDataBinding.subCategory.setSelection(subCategoryPosition);
        }
      }).subscribe(new Action1<SubCategory>() {
        @Override
        public void call(SubCategory subCategory) {
          if (subCategory != null && !foundPosition) {
            subCategoryPosition++;
            if (subCategory.getSub_category_id() == subCategoryId) {
              foundPosition = true;
            }
          }
        }
      });
    }
  }

  private void bindRangBar() {
    rangeSeekBar = new RangeSeekBar<>(this);
    rangeSeekBar.setRangeValues(MIN_FEE_RANGE, MAX_FEE_RANGE);
    rangeSeekBar.setActiveColor(ContextCompat.getColor(this, R.color.colorCuriousBlue));
    rangeSeekBar.setDefaultColor(ContextCompat.getColor(this, R.color.colorShark));
    rangeSeekBar.setInternalPadding(80);
    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.thumb);
    rangeSeekBar.setThumbImage(bitmap);
    rangeSeekBar.setAlwaysActive(true);
    rangeSeekBar.setThumbShadowBlur(3);
    rangeSeekBar.setThumbShadowXOffset(1);
    rangeSeekBar.setThumbShadowYOffset(2);
    rangeSeekBar.setThumbShadowColor(R.color.primary_material_light);
    LinearLayout layout = (LinearLayout) findViewById(R.id.rangeSeekBarLayout);
    layout.addView(rangeSeekBar);
  }

  private void setUpCategorySpinner() {
    categories = CategoryList.parentCategoryList(this);
    subCategories = CategoryList.subCategory(this);
    mCategoryAdapter =
        new CategoryAdapter<>(FilterActivity.this, CategoryList.parentCategoryList(this));
    //This will explicitly filter sub category from all sub category list from server
    mDataBinding.category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mCategory = mCategoryAdapter.getItem(position);
        mSubCategoryAdapter.clearAllItems();
        Observable<SubCategory> resultsObjectObservable =
            Observable.from(CategoryList.subCategory(FilterActivity.this));
        resultsObjectObservable.map(new Func1<SubCategory, SubCategory>() {
          @Override
          public SubCategory call(SubCategory subCategory) {
            if (subCategory.getCategory_id() == -1
                || subCategory.getCategory_id() == mCategory.getCategoryId()) {
              return subCategory;
            }
            return null;
          }
        }).doOnCompleted(new Action0() {
          @Override
          public void call() {
            if (userIsInteracting) {
              mDataBinding.subCategory.setSelection(0);
            }
          }
        }).subscribe(new Action1<SubCategory>() {
          @Override
          public void call(SubCategory subCategory) {
            if (subCategory != null) {
              mSubCategoryAdapter.addItems(subCategory);
              mSubCategoryAdapter.notifyDataSetChanged();
            }
          }
        });
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
    mDataBinding.category.setAdapter(mCategoryAdapter);
  }

  private void setUpSubCategorySpinner() {
    subCategories = CategoryList.subCategory(this);
    mSubCategoryAdapter =
        new CategoryAdapter<>(FilterActivity.this, CategoryList.subCategory(FilterActivity.this));
    mDataBinding.subCategory.setAdapter(mSubCategoryAdapter);
  }

  private void validateInfo() {
    Intent intent = new Intent();
    int max = rangeSeekBar.getSelectedMaxValue();
    int min = rangeSeekBar.getSelectedMinValue();

    if (mDataBinding.category.getSelectedItemPosition() != 0) {
      int selectCat = mDataBinding.category.getSelectedItemPosition();
      mCategory = categories.get(selectCat);
    }
    if (mDataBinding.subCategory.getSelectedItemPosition() != 0) {
      int selectSubCat = (mDataBinding.subCategory.getSelectedItemPosition());
      mSubCategory = mSubCategoryAdapter.getItem(selectSubCat);
    }
    if (selectedPosting == null
        && selectedExpiry == null
        && mCategory == null
        && mSubCategory == null
        && min == MIN_FEE_RANGE
        && max == MAX_FEE_RANGE) {
      setResult(RESULT_OK);
      finish();
    } else {
      if (selectedPosting != null) {
        String postingDate =
            DateTimeUtils.getDateToString(selectedPosting.getTime(), DateTimeUtils.DATE_FORMAT);
        intent.putExtra(DATA_POSTING_DATE, postingDate);
      }
      if (selectedExpiry != null) {
        String expiryDate =
            DateTimeUtils.getDateToString(selectedExpiry.getTime(), DateTimeUtils.DATE_FORMAT);
        intent.putExtra(DATA_EXPIRY_DATE, expiryDate);
      }
      if (mCategoryAdapter != null) {
        intent.putExtra(DATA_CATEGORY_ID, mCategory.getCategoryId());
      }
      if (mSubCategory != null) {
        intent.putExtra(DATA_SUB_CATEGORY_ID, mSubCategory.getSub_category_id());
      }
      intent.putExtra(DATA_START_RANG, min);
      intent.putExtra(DATA_END_RANG, max);
      setResult(RESULT_OK, intent);

      //Flurry
      String location = "";
      if (mCategory != null) {
        location = mCategory.getCategoryName();
      }

      String category = "";
      if (mCategory != null) {
        category = mCategory.getSubCategoryName();
      }

      String price_rang = (min + "-" + max);
      FlurryManager.agentFilterApply(location, category, price_rang);
      finish();
    }
  }

  private void openDatePickerDialog() {
    mDateTimerHelper = new DateTimerHelper(FilterActivity.this, AppMode.AGENT, this);
    if (!flagSetDate) {
      if (selectedPosting != null) {
        mDateTimerHelper.setSelectedCalendar(selectedPosting);
      }
      mDateTimerHelper.showCalendar(1);
    } else {
      if (selectedExpiry != null) {
        mDateTimerHelper.setSelectedCalendar(selectedExpiry);
      }
      mDateTimerHelper.showCalendar(2);
    }
  }

  @Override
  public void onDateSet() {
    if (flagSetDate) {
      selectedExpiry = mDateTimerHelper.getSelectedCalendar();
      setDateToText(mDataBinding.expiryDate, selectedExpiry);
    } else {
      selectedPosting = mDateTimerHelper.getSelectedCalendar();
      setDateToText(mDataBinding.postingDate, selectedPosting);
    }
  }
}
