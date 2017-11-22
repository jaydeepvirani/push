package com.android.unideal.questioner.view.payment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.view.MaterialIntroView;
import com.android.unideal.R;
import com.android.unideal.databinding.BindingManageCard;
import com.android.unideal.enums.AppMode;
import com.android.unideal.flurry.FlurryManager;
import com.android.unideal.questioner.data.CardData;
import com.android.unideal.util.Consts;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.SessionManager;
import java.util.List;

import static com.android.unideal.questioner.view.payment.ManageCardActivity.REQUEST_NW_CARD;

/**
 * Created by bhavdip on 26/12/16.
 */
public class CardsFragment extends Fragment
    implements CardsViewModel.CardsListListener, CardListAdapter.CardItemClickListener {

  private BindingManageCard mBindingManageCard;
  private CardListAdapter mCardListAdapter;
  private CardsViewModel mCardsViewModel;
  private String FOCUS_ADD_CARD = "addCard";

  public static Fragment getCardsListFragment() {
    return new CardsFragment();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mBindingManageCard = loadCardView(inflater, container);
    return mBindingManageCard.getRoot();
  }

  private BindingManageCard loadCardView(LayoutInflater inflater, ViewGroup container) {
    return DataBindingUtil.inflate(inflater, R.layout.fragment_manage_card, container, false);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mCardsViewModel = new CardsViewModel(getActivity(), this);
    mCardsViewModel.onActivityCreated();
  }

  @Override
  public void startDataBinding() {
    setUpRecyclerView();
    fetchStoredCards();
  }

  @Override
  public void showProgressBar(boolean showOutSide) {
    if (showOutSide) {
      DialogUtils.getInstance().showProgressDialog(getActivity());
    } else {
      mBindingManageCard.showProgressbar.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void hideProgressBar(boolean showOutSide) {
    if (showOutSide) {
      DialogUtils.getInstance().hideProgressDialog();
    } else {
      mBindingManageCard.showProgressbar.setVisibility(View.GONE);
    }
  }

  private void fetchStoredCards() {
    int currentUserId = SessionManager.get(getActivity()).getUserId();
    String customerId = String.valueOf(currentUserId);
    //Flurry
    FlurryManager.cardCreation(currentUserId, AppMode.QUESTIONER.name());
    mCardsViewModel.getCardsListByCustomerId(customerId);
  }

  @Override
  public void onReceiveCardList(List<CardData> cardDataList) {
    showAddCardOption();
    if (cardDataList != null && cardDataList.size() > 0) {
      mBindingManageCard.emptyView.setVisibility(View.GONE);
      mCardListAdapter.addNewCards(cardDataList);
      mBindingManageCard.paypalLayout.setVisibility(View.VISIBLE);
      //visible add card icon
      if (mCardListAdapter.hasDefaultCardSet()) {
        //we have not default card set make a call and set the default card
        mCardsViewModel.saveDefaultCardId(cardDataList.get(0).getId());
      } else {
        mCardListAdapter.setDefaultCard();
      }
    } else {
      mBindingManageCard.emptyView.setVisibility(View.VISIBLE);
      mBindingManageCard.paypalLayout.setVisibility(View.GONE);
    }
  }

  private void setUpShowCaseLayout() {
    if (SessionManager.get(getActivity()).shouldShowTourGuide() == Consts.SHOWCASE_GUIDE_SHOW) {
      new MaterialIntroView.Builder(getActivity()).enableDotAnimation(true)
          .setFocusGravity(FocusGravity.CENTER)
          .setFocusType(Focus.ALL)
          .setDelayMillis(200)
          .enableFadeAnimation(true)
          .enableIcon(false)
          .dismissOnTouch(Consts.dismissOnTouch)
          .performClick(false)
          .setInfoText(getString(R.string.txt_show_case_agt_add_card))
          .setTarget(getActivity().findViewById(R.id.btnAddCreditCard))
          .setUsageId(FOCUS_ADD_CARD)
          .show();
    }
  }

  @Override
  public void onErrorCardList(String errorMessage) {
    showAddCardOption();
    if (!TextUtils.isEmpty(errorMessage)) {
      mBindingManageCard.emptyView.setVisibility(View.VISIBLE);
      mBindingManageCard.textViewEmptyMessage.setText(errorMessage);
    }
  }

  @Override
  public void onFailToCardList(String errorMessage) {
    showAddCardOption();
    mBindingManageCard.emptyView.setVisibility(View.VISIBLE);
    mBindingManageCard.textViewEmptyMessage.setText(getString(R.string.error_vault_card_list));
    if (errorMessage != null) {
      showCardListError(errorMessage);
    }
  }

  private void setUpRecyclerView() {
    mCardListAdapter = new CardListAdapter(getContext(), this);
    mBindingManageCard.recyclerCardListViews.setLayoutManager(
        new LinearLayoutManager(getActivity()));
    mBindingManageCard.recyclerCardListViews.setHasFixedSize(true);
    mBindingManageCard.recyclerCardListViews.setAdapter(mCardListAdapter);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_NW_CARD) {
      if (resultCode == Activity.RESULT_OK) {
        //reset the page index and refresh the page
        mBindingManageCard.emptyView.setVisibility(View.GONE);
        mCardsViewModel.resetIndex();
        fetchStoredCards();
      }
    }
  }

  private void showCardListError(String errorMessage) {
    DialogUtils.showDialog(getActivity(), errorMessage, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
      }
    });
  }

  @Override
  public void onDeleteCard(final int selectPosition, final CardData cardData) {
    DialogUtils.showDialog(getActivity(), R.string.title_app_name, R.string.error_vault_delete_card,
        R.string.btn_ok, R.string.btn_cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            int defaultPosition = mCardListAdapter.getSelectedPosition();
            if (defaultPosition == selectPosition) {
              mCardsViewModel.detachedCardByUserId(selectPosition, cardData.getId());
            } else {
              mCardsViewModel.removeCardById(selectPosition, cardData.getId());
            }
          }
        });
  }

  @Override
  public void onCardRemoveSuccessfully(int removePosition) {
    mCardListAdapter.removeCard(removePosition);
    if (mCardListAdapter.getItemCount() > 0) {
      mBindingManageCard.emptyView.setVisibility(View.GONE);
      if (mCardListAdapter.getDataSet() != null && mCardListAdapter.getDataSet().size() > 0) {
        if (mCardListAdapter.getSelectedPosition() == removePosition) {
          mCardsViewModel.saveDefaultCardId(mCardListAdapter.getDataSet().get(0).getId());
        }
      }
    } else {
      mBindingManageCard.emptyView.setVisibility(View.VISIBLE);
      //if all cards have been deleted we would require to update
      //the session manager active user info
      SessionManager.get(getActivity()).saveUserDefaultCard("");
    }
  }

  @Override
  public void setDefaultCardSuccessfully(String cardId) {
    //update the card token in session
    if (!TextUtils.isEmpty(cardId)) {
      if (getActivity() != null) {
        SessionManager.get(getActivity()).saveUserDefaultCard(cardId);
        mCardListAdapter.setDefaultCard();
      }
    }
  }

  @Override
  public void onFailToSetDefaultCard(String errorMessage) {
    if (!TextUtils.isEmpty(errorMessage)) {
      DialogUtils.showToast(getActivity(), errorMessage);
    }
  }

  @Override
  public void onCardRemoveError(String errorMessage) {
    if (!TextUtils.isEmpty(errorMessage)) {
      DialogUtils.showToast(getActivity(), errorMessage);
    }
  }

  @Override
  public void setDefaultCard(int position, CardData cardData) {
    if (cardData != null) {
      mCardsViewModel.saveDefaultCardId(cardData.getId());
    }
  }

  private void showAddCardOption() {
    if (getActivity() != null) {
      getActivity().findViewById(R.id.btnAddCreditCard).setVisibility(View.VISIBLE);
      setUpShowCaseLayout();
    }
  }
}
