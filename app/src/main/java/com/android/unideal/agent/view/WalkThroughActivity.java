package com.android.unideal.agent.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.android.unideal.R;
import com.android.unideal.agent.view.fragment.WalkThroughDataFragment;
import com.android.unideal.auth.view.LogInActivity;
import com.android.unideal.data.WalkThroughData;
import com.android.unideal.databinding.WalkThroughBinding;
import com.android.unideal.flurry.FlurryManager;
import java.util.ArrayList;
import java.util.List;

public class WalkThroughActivity extends AppCompatActivity {
  private WalkThroughBinding mBinding;
  private List<WalkThroughData> walkThroughDataList = new ArrayList<>();
  private WalkThroughAdapter mAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBinding = DataBindingUtil.setContentView(this, R.layout.activity_walk_through);
    setCustomData();
    mAdapter = new WalkThroughAdapter(getSupportFragmentManager(), walkThroughDataList);
    mBinding.walkThroughPage.setAdapter(mAdapter);
    mBinding.pageIndicator.setViewPager(mBinding.walkThroughPage);
    mBinding.btnNext.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        int currentPos = mBinding.walkThroughPage.getCurrentItem();
        if (currentPos == mAdapter.getCount() - 1) {
          //track finish walk through
          FlurryManager.finishWalthThrough();
          startLoginActivity();
        } else {
          mBinding.walkThroughPage.setCurrentItem(currentPos + 1);
        }
      }
    });
    mBinding.walkThroughPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override
      public void onPageSelected(int position) {
        int lastItem = mBinding.walkThroughPage.getCurrentItem();
        if (lastItem == mAdapter.getCount() - 1) {
          mBinding.btnNext.setText(R.string.btn_go);
        } else {
          mBinding.btnNext.setText(R.string.btn_next);
        }
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });
    mBinding.btnSkip.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mBinding.walkThroughPage != null && mBinding.walkThroughPage.getCurrentItem() > -1) {
          int currentPos = mBinding.walkThroughPage.getCurrentItem();
          //track skipped walk through
          FlurryManager.skipWalkThrough(currentPos);
        }
        startLoginActivity();
      }
    });
  }

  public void startLoginActivity() {
    startActivity(LogInActivity.startLogInActivity(WalkThroughActivity.this));
    finish();
  }

  private void setCustomData() {
    WalkThroughData walkThroughData = new WalkThroughData();
    walkThroughData.setDrawableResourceId(R.drawable.ic_walk_mobile);
    walkThroughData.setStringResorId(R.string.walkthrough_text1);
    walkThroughDataList.add(walkThroughData);
    WalkThroughData walkThroughData1 = new WalkThroughData();
    walkThroughData1.setDrawableResourceId(R.drawable.ic_walk_share);
    walkThroughData1.setStringResorId(R.string.walkthrough_text2);
    walkThroughDataList.add(walkThroughData1);
    WalkThroughData walkThroughData2 = new WalkThroughData();
    walkThroughData2.setDrawableResourceId(R.drawable.ic_walk_job);
    walkThroughData2.setStringResorId(R.string.walkthrough_text3);
    walkThroughDataList.add(walkThroughData2);
    WalkThroughData walkThroughData3 = new WalkThroughData();
    walkThroughData3.setDrawableResourceId(R.drawable.ic_walk_job_share);
    walkThroughData3.setStringResorId(R.string.walkthrough_text4);
    walkThroughDataList.add(walkThroughData3);
  }

  public class WalkThroughAdapter extends FragmentPagerAdapter {
    List<WalkThroughData> walkThroughDataList;

    public WalkThroughAdapter(FragmentManager fm, List<WalkThroughData> dataList) {
      super(fm);
      this.walkThroughDataList = dataList;
    }

    @Override
    public Fragment getItem(int position) {
      return WalkThroughDataFragment.newInstance(walkThroughDataList.get(position));
    }

    @Override
    public int getCount() {
      return walkThroughDataList.size();
    }
  }
}
