package com.android.unideal.questioner.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.android.unideal.R;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 14-10-2016.
 */

public class NewJobImageAdapter extends PagerAdapter {
  int size;
  private List<String> jobPhoto = new ArrayList<>();

  @Override
  public int getCount() {
    return jobPhoto.size();
  }

  @Override
  public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }

  public void addImage(String photo) {
    jobPhoto.add(photo);
    notifyDataSetChanged();
  }

  @Override
  public Object instantiateItem(ViewGroup container, int position) {
    String imagePath = jobPhoto.get(position);
    View itemView = LayoutInflater.from(container.getContext())
        .inflate(R.layout.item_job_image, container, false);
    ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
    Picasso.with(imageView.getContext()).load(new File(imagePath)).into(imageView);
    container.addView(itemView);
    return itemView;
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((RelativeLayout) object);
  }
}