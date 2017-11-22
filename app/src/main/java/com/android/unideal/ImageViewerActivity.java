package com.android.unideal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.android.unideal.enums.AppMode;
import com.android.unideal.util.AppUtility;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class ImageViewerActivity extends AppCompatActivity {

  private static final String ARG_IMAGE_LIST = "image_list";
  private static final String ARG_APP_MODE = "app_mode";
  private static final String ARG_POSITION = "selected_pos";
  private SectionsPagerAdapter mSectionsPagerAdapter;
  private ViewPager mViewPager;
  private int selectedPos;
  private AppMode mAppMode;

  public static Intent getActivityIntent(Context context, int selectedPos,
      ArrayList<String> imageList, AppMode appMode) {
    if (selectedPos < 0 || selectedPos >= imageList.size()) {
      selectedPos = 0;
    }
    Intent intent = new Intent(context, ImageViewerActivity.class);
    intent.putStringArrayListExtra(ARG_IMAGE_LIST, imageList);
    intent.putExtra(ARG_APP_MODE, appMode.name());
    intent.putExtra(ARG_POSITION, selectedPos);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_image_viewer);
    extractAppMode();
    ArrayList<String> imageList = new ArrayList<>();
    imageList.addAll(getIntent().getStringArrayListExtra(ARG_IMAGE_LIST));
    selectedPos = getIntent().getIntExtra(ARG_POSITION, 0);
    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), imageList);
    mViewPager = (ViewPager) findViewById(R.id.container);
    mViewPager.setAdapter(mSectionsPagerAdapter);
    mViewPager.setCurrentItem(selectedPos, false);
    ImageView backImage = (ImageView) findViewById(R.id.backButton);
    backImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
  }

  private void extractAppMode() {
    mAppMode = AppMode.valueOf(getIntent().getStringExtra(ARG_APP_MODE));
    if (mAppMode == AppMode.AGENT) {
      int colorInt = ContextCompat.getColor(this, R.color.colorCuriousBlue);
      AppUtility.changeStatusBarColor(getWindow(), colorInt);
    } else if (mAppMode == AppMode.QUESTIONER) {
      int colorInt = ContextCompat.getColor(this, R.color.colorPersianGreen);
      AppUtility.changeStatusBarColor(getWindow(), colorInt);
    }
  }

  public static class PlaceholderFragment extends Fragment {
    private static final String ARG_IMAGE_URL = "image_url";

    public PlaceholderFragment() {
    }

    public static PlaceholderFragment newInstance(String imageUrl) {
      PlaceholderFragment fragment = new PlaceholderFragment();
      Bundle args = new Bundle();
      args.putString(ARG_IMAGE_URL, imageUrl);
      fragment.setArguments(args);
      return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.fragment_image_viewer, container, false);
      ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
      String url = getArguments().getString(ARG_IMAGE_URL);
      if (url != null) {
        Picasso.with(getActivity()).load(url).into(imageView);
      }
      return rootView;
    }
  }

  public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private List<String> imageUrlList;

    public SectionsPagerAdapter(FragmentManager fm, ArrayList<String> imageUrlList) {
      super(fm);
      this.imageUrlList = imageUrlList;
    }

    @Override
    public Fragment getItem(int position) {
      return PlaceholderFragment.newInstance(imageUrlList.get(position));
    }

    @Override
    public int getCount() {
      return imageUrlList.size();
    }
  }
}
