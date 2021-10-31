package com.jackosky.currency.domain.converter;

import java.math.BigDecimal;
import java.util.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CurrencyRates {

  private final Map<String, BigDecimal> rates;

  public BigDecimal get(String currency) {
    return rates.get(currency);
  }

}
