package com.android.unideal.auth.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.android.unideal.R;
import com.android.unideal.agent.adapter.CategoryAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 22-09-2016.
 */

public class SpinnerAdapter extends ArrayAdapter<String> {
  // Initialise custom font, for example:
  private List<String> items = new ArrayList<>();
  private Context context;

  //    Typeface font = Typeface.createFromAsset(getContext().getAssets(),
  //            Consts.FONT_PT_SANS_REGULAR);

  // (In reality I used a manager which caches the Typeface objects)
  public SpinnerAdapter(Context context, int resource, List<String> items) {
    super(context, resource, items);
    this.context = context;
    this.items = items;
  }

  // Affects default (closed) state of the spinner
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    TextView txt = new TextView(context);
    txt.setGravity(Gravity.LEFT);
    txt.setText(items.get(position));
    txt.setTextColor(Color.parseColor("#1D1D26"));
    return txt;
  }

  // Affects opened state of the spinner
  @Override
  public View getDropDownView(int position, View convertView, ViewGroup parent) {
    return getCustomView(position, convertView, parent);
  }

  public View getCustomView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View mySpinner = inflater.inflate(R.layout.spinner_item, parent, false);
    TextView view = (TextView) mySpinner.findViewById(R.id.textViewItem);
    view.setText(items.get(position));
    view.setGravity(Gravity.CENTER);
    return mySpinner;
  }
}