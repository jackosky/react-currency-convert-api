package com.jackosky.currency.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ConversionRequest {

  private final String from;
  private final String to;
  private final BigDecimal amount;
}
