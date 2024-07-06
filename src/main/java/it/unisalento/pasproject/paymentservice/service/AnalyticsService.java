package it.unisalento.pasproject.paymentservice.service;

import it.unisalento.pasproject.paymentservice.dto.AdminAnalyticsDTO;
import it.unisalento.pasproject.paymentservice.dto.UserAnalyticsDTO;
import it.unisalento.pasproject.paymentservice.service.Template.AdminTemplate;
import it.unisalento.pasproject.paymentservice.service.Template.UserTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnalyticsService {
    private final UserTemplate userTemplate;
    private final AdminTemplate adminTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsService.class);

    @Autowired
    public AnalyticsService(MongoTemplate mongoTemplate) {
        this.userTemplate = new UserTemplate(mongoTemplate);
        this.adminTemplate = new AdminTemplate(mongoTemplate);
    }

    public List<UserAnalyticsDTO> getUserAnalytics(String email, LocalDateTime startDate, LocalDateTime endDate, String granularity) {
        return userTemplate.getAnalyticsList(email, startDate, endDate, granularity);
    }

    public List<AdminAnalyticsDTO> getAdminAnalytics(String email, LocalDateTime startDate, LocalDateTime endDate, String granularity) {
        return adminTemplate.getAnalyticsList(email, startDate, endDate, granularity);
    }
}
