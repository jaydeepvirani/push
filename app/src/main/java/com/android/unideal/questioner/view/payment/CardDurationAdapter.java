package com.android.unideal.questioner.view.payment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.android.unideal.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 15-11-2016.
 */

public class CardDurationAdapter<T> extends BaseAdapter {
  private Context mContext;
  private List<T> list = new ArrayList<>();
  private LayoutInflater mLayoutInflater;

  public CardDurationAdapter(Context context) {
    this(context, null);
  }

  public CardDurationAdapter(Context context, List<T> itemList) {
    this.mContext = context;
    this.mLayoutInflater = getLayoutInflater();
    this.list = new ArrayList<>();
    if (itemList != null && itemList.size() > 0) {
      this.list.addAll(itemList);
    }
  }

  private LayoutInflater getLayoutInflater() {
    return (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  public int getCount() {
    return list.size();
  }

  public T getItem(int position) {
    return list.get(position);
  }

  public long getItemId(int position) {
    return position;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    View mRowView = convertView;
    ItemViewHolder mItemViewHolder;
    if (convertView == null) {
      mRowView = mLayoutInflater.inflate(R.layout.spinner_payment_item, parent, false);
      mItemViewHolder = new ItemViewHolder(mRowView);
      mRowView.setTag(mItemViewHolder);
    } else {
      mItemViewHolder = (ItemViewHolder) mRowView.getTag();
    }
    mItemViewHolder.mTextView.setText(list.get(position).toString());
    return mRowView;
  }

  public View getDropDownView(int position, View convertView, ViewGroup parent) {

    View mRowView = convertView;
    DropDownItemViewHolder mItemViewHolder;
    if (convertView == null) {
      mRowView = mLayoutInflater.inflate(R.layout.spinner_item, parent, false);
      mItemViewHolder = new DropDownItemViewHolder(mRowView);
      mRowView.setTag(mItemViewHolder);
    } else {
      mItemViewHolder = (DropDownItemViewHolder) mRowView.getTag();
    }
    mItemViewHolder.mTextView.setText(list.get(position).toString());
    return mRowView;
  }

  private class DropDownItemViewHolder {
    private TextView mTextView;

    DropDownItemViewHolder(View itemView) {
      mTextView = (TextView) itemView.findViewById(R.id.textViewItem);
    }
  }

  private class ItemViewHolder {
    private TextView mTextView;

    ItemViewHolder(View itemView) {
      mTextView = (TextView) itemView.findViewById(R.id.spinnerText);
    }
  }
}