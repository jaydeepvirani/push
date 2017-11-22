package com.android.unideal.questioner.view.payment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.data.UserDetail;
import com.android.unideal.databinding.RowCardBinding;
import com.android.unideal.questioner.data.CardData;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.SessionManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 15-11-2016.
 */

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.PayCardViewHolder> {
  private Context mContext;
  private List<CardData> cardDataList = new ArrayList<>();
  private int selectedPosition = 0;
  private CardPatternHelper mPatternHelper;
  private CardItemClickListener itemClickListener;

  public CardListAdapter(Context context, CardItemClickListener clickListener) {
    this.mContext = context;
    this.itemClickListener = clickListener;
    mPatternHelper = new CardPatternHelper();
  }

  public Context getContext() {
    return mContext;
  }

  public void addNewCard(CardData nwCardData) {
    List<CardData> tempCardDataSet = new ArrayList<>(cardDataList);
    cardDataList.clear();
    cardDataList.add(nwCardData);
    cardDataList.addAll(tempCardDataSet);
    notifyDataSetChanged();
  }

  public String getDefaultCardId() {
    UserDetail userDetail = SessionManager.get(getContext()).getActiveUser();
    return userDetail.getDefault_card();
  }

  public boolean hasDefaultCardSet() {
    return TextUtils.isEmpty(getDefaultCardId());
  }

  /**
   * Set Default into the SessionManager
   */
  public void setDefaultCard() {
    String cacheDefaultCard = getDefaultCardId();
    if (!TextUtils.isEmpty(cacheDefaultCard)) {
      setDefaultCard(cacheDefaultCard);
    } else {
      setDefaultCard(cardDataList.get(0).getId());
    }
  }

  public List<CardData> getDataSet() {
    return cardDataList;
  }

  /**
   * Must call after data set is available
   *
   * @param cardId set to as default card id
   */
  public void setDefaultCard(String cardId) {
    int indexPosition = -1;
    for (int i = 0; i < cardDataList.size(); i++) {
      if (cardDataList.get(i).getId().equals(cardId)) {
        indexPosition = i;
        break;
      }
    }
    selectedPosition = indexPosition;
    notifyDataSetChanged();
  }

  public void removeCard(int currentItemPosition) {
    if (selectedPosition == currentItemPosition) {
      selectedPosition = 0;
    }
    cardDataList.remove(currentItemPosition);
    notifyDataSetChanged();
  }

  public int getSelectedPosition() {
    return selectedPosition;
  }

  public void addNewCards(List<CardData> dataList) {
    cardDataList.clear();
    cardDataList.addAll(dataList);
    notifyDataSetChanged();
  }

  @Override
  public PayCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    RowCardBinding rowCardBinding = loadRowBinding(parent);
    return new PayCardViewHolder(rowCardBinding);
  }

  private RowCardBinding loadRowBinding(ViewGroup parent) {
    return DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
        R.layout.item_card_details, parent, false);
  }

  @Override
  public void onBindViewHolder(PayCardViewHolder holder, int position) {
    holder.bindingRows(position);
  }

  @Override
  public int getItemCount() {
    return cardDataList.size();
  }

  public interface CardItemClickListener {
    void onDeleteCard(int position, CardData cardData);

    void setDefaultCard(int position, CardData cardData);
  }

  public class PayCardViewHolder extends RecyclerView.ViewHolder {

    private RowCardBinding mRowCardBinding;
    private int currentItemPosition;
    private CardData paymentCardInfo;

    public PayCardViewHolder(RowCardBinding cardBinding) {
      super(cardBinding.getRoot());
      this.mRowCardBinding = cardBinding;
      mRowCardBinding.selection.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          selectedPosition = currentItemPosition;
          itemClickListener.setDefaultCard(currentItemPosition, paymentCardInfo);
        }
      });
      mRowCardBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          itemClickListener.onDeleteCard(currentItemPosition, paymentCardInfo);
        }
      });
    }

    public void bindingRows(int position) {
      currentItemPosition = position;
      paymentCardInfo = cardDataList.get(position);
      if (paymentCardInfo != null) {
        int cardDrawableResId = mPatternHelper.findCardIconByName(paymentCardInfo.getType());
        Drawable cardIcon = ContextCompat.getDrawable(getContext(), cardDrawableResId);
        mRowCardBinding.textViewCardNumber.setCompoundDrawablesWithIntrinsicBounds(cardIcon, null,
            null, null);
        mRowCardBinding.textViewCardNumber.setText(paymentCardInfo.getNumber());
      }
      if (selectedPosition == position) {
        AppUtility.loadSVGImage(mRowCardBinding.selection, R.drawable.ic_accepted,
            R.color.colorPersianGreen);
      } else {
        AppUtility.loadSVGImage(mRowCardBinding.selection, R.drawable.ic_accepted,
            R.color.colorAthensGray);
      }
    }
  }
}