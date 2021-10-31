package com.jackosky.currency.dto;

import lombok.Data;

@Data
public class ConversionRequest {

  private final String from;
  private final String to;
  private final double amount;
}
