package com.jackosky.currency.domain;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.jackosky.currency.domain.converter.CurrencyRates;
import com.jackosky.currency.domain.converter.ExternalExchangeRateWebClient;
import com.jackosky.currency.dto.ConversionResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

class CurrencyConverterServiceTest {

  private final ExternalExchangeRateWebClient workingClient1 = mock(ExternalExchangeRateWebClient.class);
  private final ExternalExchangeRateWebClient workingClient2 = mock(ExternalExchangeRateWebClient.class);

  private final ExternalExchangeRateWebClient failingClient = mock(ExternalExchangeRateWebClient.class);

  @BeforeEach
  void before() {
    var rates1 = Map.of("EUR", BigDecimal.ONE,
        "USD", new BigDecimal("1.16"),
        "PLN", new BigDecimal("4.62")
    );

    var rates2 = Map.of("EUR", BigDecimal.ONE,
        "USD", new BigDecimal("1.16"),
        "PLN", new BigDecimal("4.62"),
        "GBP", new BigDecimal("0.84")
    );
    given(workingClient1.getCurrencyRates()).willReturn(Mono.just(new CurrencyRates(rates1)));
    given(workingClient2.getCurrencyRates()).willReturn(Mono.just(new CurrencyRates(rates2)));
    given(failingClient.getCurrencyRates()).willReturn(Mono.error(new IllegalStateException("expected error")));
  }

  @Test
  void success_when_clients_working() {
    CurrencyConverterService underTest = new CurrencyConverterService(List.of(workingClient1, workingClient2));

    assertThat(underTest.convert("USD", "PLN", BigDecimal.ONE))
        .extracting(Mono::block)
        .extracting(ConversionResponse::getFrom,
            ConversionResponse::getTo,
            ConversionResponse::getAmount,
            ConversionResponse::getConverted)
        .containsExactly("USD", "PLN", BigDecimal.ONE, new BigDecimal("3.98"));
  }

  @Test
  void use_other_client_when_one_is_failing() {
    CurrencyConverterService underTest = new CurrencyConverterService(List.of(failingClient, workingClient2));

    assertThat(underTest.convert("USD", "PLN", BigDecimal.ONE))
        .extracting(Mono::block)
        .extracting(ConversionResponse::getFrom,
            ConversionResponse::getTo,
            ConversionResponse::getAmount,
            ConversionResponse::getConverted)
        .containsExactly("USD", "PLN", BigDecimal.ONE, new BigDecimal("3.98"));
  }

  @Test
  void fallback_to_other_client_if_conversion_not_supported() {
    CurrencyConverterService underTest = new CurrencyConverterService(List.of(workingClient1, workingClient2));

    assertThat(underTest.convert("GBP", "PLN", BigDecimal.ONE))
        .extracting(Mono::block)
        .extracting(ConversionResponse::getFrom,
            ConversionResponse::getTo,
            ConversionResponse::getAmount,
            ConversionResponse::getConverted)
        .containsExactly("GBP", "PLN", BigDecimal.ONE, new BigDecimal("5.49"));
  }

  @Test
  void fail_if_no_available_clients() {
    CurrencyConverterService underTest = new CurrencyConverterService(List.of(failingClient, failingClient));

    var convert = underTest.convert("USD", "PLN", BigDecimal.ONE);
    assertThatThrownBy(convert::block)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("No providers available");
  }

}
