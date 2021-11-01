package com.jackosky.currency.domain.converter;

import reactor.core.publisher.Mono;

public interface ExternalExchangeRateWebClient {

  Mono<CurrencyRates> getCurrencyRates();

}
