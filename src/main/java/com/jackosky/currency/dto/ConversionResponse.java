package com.jackosky.currency.dto;

import lombok.Data;

@Data
public class ConversionResponse {

  private final String from;
  private final String to;
  private final double amount;
  private final double converted;
}
