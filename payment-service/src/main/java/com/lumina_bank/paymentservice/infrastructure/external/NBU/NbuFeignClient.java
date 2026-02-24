package com.lumina_bank.paymentservice.infrastructure.external.NBU;

import com.lumina_bank.paymentservice.infrastructure.external.NBU.dto.ExchangeRateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "nbu-client", url = "https://bank.gov.ua/NBUStatService/v1/statdirectory")
public interface NbuFeignClient {

    @GetMapping("/exchange?json")
    List<ExchangeRateResponse> getRates();
}
