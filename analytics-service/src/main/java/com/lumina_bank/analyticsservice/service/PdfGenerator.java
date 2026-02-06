package com.lumina_bank.analyticsservice.service;

import com.lumina_bank.analyticsservice.dto.AnalyticsCategoryResponse;
import com.lumina_bank.analyticsservice.dto.AnalyticsMonthlyOverviewResponse;
import com.lumina_bank.analyticsservice.dto.AnalyticsTopRecipientResponse;
import com.lumina_bank.analyticsservice.util.HtmlToPdf;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfGenerator {

    private final SpringTemplateEngine templateEngine;

    public byte[] generate(
            AnalyticsMonthlyOverviewResponse overview,
            List<AnalyticsCategoryResponse> categories,
            List<AnalyticsTopRecipientResponse> recipients
    ){
            String html = renderHtml(overview,categories,recipients);
            return HtmlToPdf.convert(html);
    }

    private String renderHtml(
            AnalyticsMonthlyOverviewResponse overview,
            List<AnalyticsCategoryResponse> categories,
            List<AnalyticsTopRecipientResponse> recipients
    ) {
        Context context = new Context();
        context.setVariable("overview", overview);
        context.setVariable("categories", categories);
        context.setVariable("recipients", recipients);

        return templateEngine.process(
                "reports/monthly-financial",
                context
        );
    }
}
