package com.jackosky.currency.domain.converter.provider;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;

@Data
public class ExchangeRateDto {

  private Map<String, BigDecimal> rates;
}
