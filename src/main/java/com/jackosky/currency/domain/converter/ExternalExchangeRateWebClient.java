package com.jackosky.currency.domain.converter;

import static java.util.Collections.unmodifiableMap;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ExternalExchangeRateWebClient {

  //create class for: build also for: http://api.exchangeratesapi.io/v1/latest?access_key=bb4012c7068fe022fb0ce4c76dbb123d
  private final WebClient client = WebClient.create("https://api.exchangerate-api.com/v4/latest/EUR");

  public Mono<CurrencyRates> getCurrencyRates() {
    return client.get().accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(ExchangeRateApiV4Dto.class)
        .map(this::mapApiResponse);
  }

  private CurrencyRates mapApiResponse(ExchangeRateApiV4Dto in) {
    return new CurrencyRates(unmodifiableMap(in.getRates()));
  }

}
