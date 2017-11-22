package com.android.unideal.rest.response.payment;

import com.android.unideal.questioner.data.CardData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardsResponse {
  private List<CardData> items;
  private Long total_items;
  private Long total_pages;

  public List<CardData> getItems() {
    return items;
  }

  public CardsResponse setItems(List<CardData> items) {
    this.items = items;
    return this;
  }

  public Long getTotal_items() {
    return total_items;
  }

  public CardsResponse setTotal_items(Long total_items) {
    this.total_items = total_items;
    return this;
  }

  public Long getTotal_pages() {
    return total_pages;
  }

  public CardsResponse setTotal_pages(Long total_pages) {
    this.total_pages = total_pages;
    return this;
  }
}