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
package com.jackosky.currency.api;

import static org.springframework.web.reactive.function.BodyExtractors.toMono;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import com.jackosky.currency.domain.CurrencyConverterService;
import com.jackosky.currency.dto.ConversionRequest;
import com.jackosky.currency.dto.ConversionResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Configuration
public class CurrencyConverterFunctionsConfig {

  private final CurrencyConverterService currencyConverter;

  public CurrencyConverterFunctionsConfig(CurrencyConverterService currencyConverter) {
    this.currencyConverter = currencyConverter;
  }

  @Bean
  RouterFunction<ServerResponse> convert() {
    return route(POST("/currency/convert"),
        req -> req.body(toMono(ConversionRequest.class))
            .flatMap(request -> currencyConverter.convert(request.getFrom(), request.getTo(), request.getAmount()))
            .flatMap(response -> ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(response), ConversionResponse.class)
            ));
  }

}
