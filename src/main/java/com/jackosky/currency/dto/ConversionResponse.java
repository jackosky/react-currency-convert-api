package com.jackosky.currency.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ConversionResponse {

  private final String from;
  private final String to;
  private final BigDecimal amount;
  private final BigDecimal converted;
}
