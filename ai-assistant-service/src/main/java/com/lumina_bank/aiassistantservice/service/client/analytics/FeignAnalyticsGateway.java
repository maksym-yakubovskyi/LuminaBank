package com.lumina_bank.aiassistantservice.service.client.analytics;

import com.lumina_bank.aiassistantservice.domain.dto.client.analytics.*;
import com.lumina_bank.aiassistantservice.util.FeignExceptionMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeignAnalyticsGateway {
    private final AnalyticsClientService client;
    private final FeignExceptionMapper mapper;

    public AnalyticsMonthlyOverviewResponse  getMonthlyOverview(Long accountId, YearMonth yearMonth){
        try{
            String formatted = yearMonth != null ? yearMonth.toString() : null;
            return client.getMonthlyOverview(accountId, formatted).getBody();
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public AnalyticsDailyOverviewResponse getDailyOverview(Long accountId, LocalDate date){
        try{
            String formatted = date != null ? date.toString() : null;
            return client.getDailyOverview(accountId, formatted).getBody();
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public List<AnalyticsCategoryResponse> getCategoriesAnalytics(YearMonth yearMonth){
        try{
            String formatted = yearMonth != null ? yearMonth.toString() : null;
            return Optional.ofNullable(client.getCategoriesAnalytics(formatted).getBody())
                    .orElse(List.of());
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public List<AnalyticsTopRecipientResponse> getTopRecipients(){
        try{
            return Optional.ofNullable(client.getTopRecipients().getBody())
                    .orElse(List.of());
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public RecommendationResponse getRecommendationInfo(){
        try{
            return  client.getRecommendations().getBody();
        }catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public AiForecastResponse getForecast(){
        try{
            return  client.getAiForecast().getBody();
        }catch (FeignException e) {
            throw mapper.map(e);
        }
    }
}
