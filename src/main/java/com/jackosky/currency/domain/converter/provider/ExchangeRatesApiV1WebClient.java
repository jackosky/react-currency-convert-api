package com.jackosky.currency.domain.converter.provider;

import static java.util.Collections.unmodifiableMap;

import com.jackosky.currency.domain.converter.CurrencyRates;
import com.jackosky.currency.domain.converter.ExternalExchangeRateWebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ExchangeRatesApiV1WebClient implements ExternalExchangeRateWebClient {

  private final WebClient client;

  public ExchangeRatesApiV1WebClient(@Value("${external-api.currency.accesskey}") String accessKey) {
    this.client = WebClient.create("http://api.exchangeratesapi.io/v1/latest?base=EUR&access_key=" + accessKey);
  }

  @Override
  public Mono<CurrencyRates> getCurrencyRates() {
    return client.get()
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(ExchangeRateDto.class)
        .map(this::mapApiResponse);
  }

  private CurrencyRates mapApiResponse(ExchangeRateDto in) {
    return new CurrencyRates(unmodifiableMap(in.getRates()));
  }

}
