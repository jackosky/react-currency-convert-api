package com.jackosky.currency.domain.converter;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;

@Data
public class ExchangeRateApiV4Dto {

  private Map<String, BigDecimal> rates;
}
