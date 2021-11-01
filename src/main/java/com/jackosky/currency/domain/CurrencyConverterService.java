/*
 * SonarQube
 * Copyright (C) 2009-2021 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.jackosky.currency.domain;

import static java.math.BigDecimal.ONE;

import com.jackosky.currency.domain.converter.CurrencyRates;
import com.jackosky.currency.domain.converter.ExternalExchangeRateWebClient;
import com.jackosky.currency.dto.ConversionResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CurrencyConverterService {

  private final List<ExternalExchangeRateWebClient> exchangeRateWebClients;

  public CurrencyConverterService(List<ExternalExchangeRateWebClient> exchangeRateWebClients) {
    this.exchangeRateWebClients = exchangeRateWebClients;
  }

  public Mono<ConversionResponse> convert(String from, String to, BigDecimal amount) {
    return getCurrencyRate(new ArrayList<>(this.exchangeRateWebClients),
        ThreadLocalRandom.current().nextInt(exchangeRateWebClients.size()),
        from,
        to)
        .map(rate -> new ConversionResponse(from, to, amount,
            amount.multiply(rate).setScale(2, RoundingMode.DOWN)
        ));
  }

  private Mono<BigDecimal> getCurrencyRate(List<ExternalExchangeRateWebClient> exchangeRateWebClients,
      int selectedProvider, String from, String to) {
    return Flux.fromIterable(exchangeRateWebClients)
        .elementAt(selectedProvider)
        .flatMap(ExternalExchangeRateWebClient::getCurrencyRates)
        .map(currencyRates -> calculateRating(from, to, currencyRates))
        .onErrorResume(throwable -> retry(exchangeRateWebClients, selectedProvider, from, to));
  }

  private BigDecimal calculateRating(String from, String to, CurrencyRates currencyRates) {
    var fromRate = currencyRates.get(from)
        .orElseThrow(() -> new IllegalArgumentException("Unsupported conversion"));
    var toRate = currencyRates.get(to)
        .orElseThrow(() -> new IllegalArgumentException("Unsupported conversion"));
    return ONE.setScale(4, RoundingMode.DOWN)
        .divide(fromRate, RoundingMode.DOWN).multiply(toRate);
  }

  private Mono<BigDecimal> retry(List<ExternalExchangeRateWebClient> exchangeRateWebClients,
      int selectedProvider, String from, String to) {
    exchangeRateWebClients.remove(selectedProvider);
    if (exchangeRateWebClients.isEmpty()) {
      return Mono.error(new IllegalStateException("No providers available"));
    }
    return getCurrencyRate(exchangeRateWebClients,
        ThreadLocalRandom.current().nextInt(exchangeRateWebClients.size()), from, to);
  }
}
