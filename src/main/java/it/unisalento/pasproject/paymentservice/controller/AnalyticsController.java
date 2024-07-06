package it.unisalento.pasproject.paymentservice.controller;

import it.unisalento.pasproject.paymentservice.dto.AdminAnalyticsDTO;
import it.unisalento.pasproject.paymentservice.dto.UserAnalyticsDTO;
import it.unisalento.pasproject.paymentservice.exception.MissingDataException;
import it.unisalento.pasproject.paymentservice.service.AnalyticsService;
import it.unisalento.pasproject.paymentservice.service.UserCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static it.unisalento.pasproject.paymentservice.security.SecurityConstants.ROLE_ADMIN;
import static it.unisalento.pasproject.paymentservice.security.SecurityConstants.ROLE_UTENTE;

@RestController
@RequestMapping("/api/payment/analytics")
public class AnalyticsController {
    private final UserCheckService userCheckService;
    private final AnalyticsService analyticsService;

    @Autowired
    public AnalyticsController(UserCheckService userCheckService, AnalyticsService analyticsService) {
        this.userCheckService = userCheckService;
        this.analyticsService = analyticsService;
    }

    @GetMapping("/user")
    @Secured(ROLE_UTENTE)
    public List<UserAnalyticsDTO> getUserAnalytics(@RequestParam String granularity) {
        String userEmail = userCheckService.getCurrentUserEmail();
        LocalDateTime startDate = LocalDateTime.now().withDayOfYear(1).toLocalDate().atStartOfDay();
        LocalDateTime endDate = LocalDateTime.now();

        try {
            return analyticsService.getUserAnalytics(userEmail, startDate, endDate, granularity);
        } catch (Exception e) {
            throw new MissingDataException(e.getMessage());
        }
    }

    @GetMapping("/admin")
    @Secured(ROLE_ADMIN)
    public List<AdminAnalyticsDTO> getAdminAnalytics(@RequestParam String granularity) {
        LocalDateTime startDate = LocalDateTime.now().withDayOfYear(1).toLocalDate().atStartOfDay();
        LocalDateTime endDate = LocalDateTime.now();

        try {
            return analyticsService.getAdminAnalytics("", startDate, endDate, granularity);
        } catch (Exception e) {
            throw new MissingDataException(e.getMessage());
        }
    }
}
