package com.jackosky.currency.domain.converter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CurrencyRates {

  private final Map<String, BigDecimal> rates;

  public Optional<BigDecimal> get(String currency) {
    return Optional.ofNullable(rates.get(currency));
  }

}
