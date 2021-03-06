package com.android.unideal.util;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by DSK02 on 12/10/2015.
 */
public abstract class OnScrollTopListener extends RecyclerView.OnScrollListener {
  public static String TAG = OnScrollTopListener.class.getSimpleName();
  int firstVisibleItem, visibleItemCount, totalItemCount;
  private int previousTotal = 0; // The total number of items in the data set after the last load
  private boolean loading = true; // True if we are still waiting for the last set of data to load.
  private int visibleThreshold = 5;
  // The minimum amount of items to have below your current scroll position before loading more.
  private int current_page = 0;
  private LinearLayoutManager mLinearLayoutManager;

  public OnScrollTopListener(LinearLayoutManager linearLayoutManager) {
    this.mLinearLayoutManager = linearLayoutManager;
  }

  @Override
  public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    super.onScrolled(recyclerView, dx, dy);

    visibleItemCount = recyclerView.getChildCount();
    totalItemCount = mLinearLayoutManager.getItemCount();
    firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
    if (loading) {
      if (totalItemCount > previousTotal) {
        loading = false;
        previousTotal = totalItemCount;
      }
    }
    if (!loading && firstVisibleItem == 0) {
      current_page++;
      onLoadMore(current_page);
      loading = true;
    }
    //        if (!loading && (totalItemCount - visibleItemCount)
    //                > (firstVisibleItem + visibleThreshold)) {
    //            // End has been reached
    //            // Do something
    //            current_page++;
    //            onLoadMore(current_page);
    ////            loading = true;
    //        }
  }

  public abstract void onLoadMore(int current_page);
}
