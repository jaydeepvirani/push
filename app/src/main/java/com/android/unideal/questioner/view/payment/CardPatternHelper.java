package com.android.unideal.questioner.view.payment;

import android.util.Log;
import android.util.SparseArray;
import com.android.unideal.R;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bhavdip on 26/12/16.
 */

public class CardPatternHelper {

  private static final String TAG = "CardPatternHelper";
  private static final Pattern CODE_PATTERN =
      Pattern.compile("([0-9]{0,4})|([0-9]{4}-)+|([0-9]{4}-[0-9]{0,4})+");
  private SparseArray<Pattern> mCCPatterns = null;
  private SparseArray<String> cardName = null;
  private boolean isValid;
  private int findDrawableID = 0;

  public CardPatternHelper() {
    mCCPatterns = new SparseArray<>();
    cardName = new SparseArray<>();
    mCCPatterns.put(R.drawable.card_visa, Pattern.compile("^4[0-9]{2,12}(?:[0-9]{3})?$"));
    cardName.put(R.drawable.card_visa, CardType.VISA);

    mCCPatterns.put(R.drawable.card_master, Pattern.compile("^5[1-5][0-9]{1,14}$"));
    cardName.put(R.drawable.card_master, CardType.MASTER);

    mCCPatterns.put(R.drawable.card_amx, Pattern.compile("^3[47][0-9]{1,13}$"));
    cardName.put(R.drawable.card_amx, CardType.AMX);

    mCCPatterns.put(R.drawable.card_diner_club,
        Pattern.compile("^3(?:0[0-5]|[68][0-9])[0-9]{4,}$"));
    cardName.put(R.drawable.card_diner_club, CardType.DINERS_CLUB);

    mCCPatterns.put(R.drawable.card_discover, Pattern.compile("^6(?:011|5[0-9]{2})[0-9]{3,}$"));
    cardName.put(R.drawable.card_discover, CardType.DISCOVER);

    mCCPatterns.put(R.drawable.card_maestro,
        Pattern.compile("^(5018|5020|5038|5612|5893|6304|6759|6761|6762|6763|0604|6390)+$"));
    cardName.put(R.drawable.card_maestro, CardType.MAESTRO);

    mCCPatterns.put(R.drawable.card_jcb, Pattern.compile("^(?:2131|1800|35[0-9]{3})[0-9]{3,}$"));
    cardName.put(R.drawable.card_jcb, CardType.JCB);
  }

  public String checkCardTypeById(int cardDrwableId) {
    return cardName.get(cardDrwableId);
  }

  public int findCardIconByName(String cardTypeName) {
    int carDrawableIcon = -1;
    for (int i = 0; i < cardName.size(); i++) {
      int indexKey = cardName.keyAt(i);
      String value = cardName.get(indexKey);
      if (cardTypeName.equals(value)) {
        carDrawableIcon = indexKey;
        break;
      }
    }
    return carDrawableIcon;
  }

  public int checkCardType(CharSequence text) {
    for (int i = 0; i < mCCPatterns.size(); i++) {
      int key = mCCPatterns.keyAt(i);
      // get the object by the key.
      Pattern p = mCCPatterns.get(key);
      String findTheNumber = text.toString().replace("-", "");
      Log.d(TAG, "Start Match Card Number: " + findTheNumber);
      Matcher m = p.matcher(findTheNumber);
      if (m.find()) {
        findDrawableID = key;
        break;
      } else {
        findDrawableID = 0;
      }
    }
    if (findDrawableID == 0) {
      isValid = false;
      Log.d(TAG, "No Match the Credit Card...[" + isValid + "]");
    } else {
      isValid = true;
      Log.d(TAG, "Wow Match the Credit Card...[" + isValid + "]");
    }
    return findDrawableID;
  }

  public boolean isValid() {
    return isValid;
  }

  public boolean findCardNumber(String input) {
    return CODE_PATTERN.matcher(input).matches();
  }

  public String keepNumbersOnly(CharSequence s) {
    // Should of course be more robust
    return s.toString().replaceAll("[^0-9]", "");
  }

  public String formatNumbersAsCode(CharSequence s) {
    int groupDigits = 0;
    String tmp = "";
    for (int i = 0; i < s.length(); ++i) {
      tmp += s.charAt(i);
      ++groupDigits;
      if (groupDigits == 4) {
        tmp += "-";
        groupDigits = 0;
      }
    }
    return tmp;
  }

  public String getNormalCardNumber(CharSequence charSequence) {
    return charSequence.toString().replaceAll("-", "");
  }

  public String getEncodedCardNumber(CharSequence charSequence) {
    char[] temp = charSequence.toString().toCharArray();
    int end = ((temp.length) - 4);
    for (int i = 0; i < end; i++) {
      temp[i] = 'X';
    }
    return String.valueOf(temp);
  }

  public interface CardType {
    String VISA = "visa";
    String MASTER = "mastercard";
    String AMX = "amex";
    String DINERS_CLUB = "dinersClub";
    String DISCOVER = "discover";
    String MAESTRO = "maestro";
    String JCB = "jcb";
  }
}
