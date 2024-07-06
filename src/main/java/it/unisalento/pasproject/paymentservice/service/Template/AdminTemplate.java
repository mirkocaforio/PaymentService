package it.unisalento.pasproject.paymentservice.service.Template;

import it.unisalento.pasproject.paymentservice.domain.Invoice;
import it.unisalento.pasproject.paymentservice.dto.AdminAnalyticsDTO;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDateTime;
import java.util.List;

public class AdminTemplate extends AnalyticsTemplate<AdminAnalyticsDTO> {
    public AdminTemplate(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }

    @Override
    public List<AdminAnalyticsDTO> getAnalyticsList(String email, LocalDateTime startDate, LocalDateTime endDate, String granularity) {
        return super.getAnalyticsList(email, startDate, endDate, granularity);
    }

    @Override
    protected MatchOperation createMatchOperation(String email, LocalDateTime startDate, LocalDateTime endDate) {
        MatchOperation matchOperation;

        if (startDate != null && endDate != null) {
            matchOperation = Aggregation.match(
                    Criteria.where("invoicePaymentDate").gte(startDate).lte(endDate)
                            .and("invoiceStatus").is(Invoice.Status.PAID)
            );
        } else if (startDate != null) {
            matchOperation = Aggregation.match(
                    Criteria.where("invoicePaymentDate").gte(startDate)
                            .and("invoiceStatus").is(Invoice.Status.PAID)
            );
        } else if (endDate != null) {
            matchOperation = Aggregation.match(
                    Criteria.where("invoicePaymentDate").lte(endDate)
                            .and("invoiceStatus").is(Invoice.Status.PAID)
            );
        } else {
            matchOperation = Aggregation.match(
                    Criteria.where("invoiceStatus").is(Invoice.Status.PAID)
            );
        }

        return matchOperation;
    }

    @Override
    protected List<AggregationOperation> getAdditionalOperations() {
        AggregationOperation convertOverdueDate = Aggregation.addFields().addFieldWithValue("convertedOverdueDate",
                ConvertOperators.Convert.convertValueOf("invoiceOverdueDate").to("date")).build();

        return List.of(convertOverdueDate);
    }

    @Override
    protected ProjectionOperation createProjectionOperation() {
        return Aggregation.project()
                .andInclude(
                        "amount",
                        "invoiceOverdueDate",
                        "invoicePaymentDate",
                        "invoiceStatus",
                        "invoicePartialAmount",
                        "invoiceDelayAmount",
                        "invoiceTotalAmount"
                )
                .and(DateOperators.dateOf("invoicePaymentDate").withTimezone(DateOperators.Timezone.valueOf("UTC")).toString("%m")).as("month")
                .and(DateOperators.dateOf("invoicePaymentDate").withTimezone(DateOperators.Timezone.valueOf("UTC")).toString("%Y")).as("year");
    }

    @Override
    protected GroupOperation createGroupOperation(String granularity) {
        return switch (granularity) {
            case "month" -> Aggregation.group("year", "month")
                    .count().as("totalInvoices")
                    .sum(ConditionalOperators.when(Criteria.where("invoicePaymentDate").lte("convertedOverdueDate"))
                            .then(0)
                            .otherwise(1)).as("overdueInvoices")
                    .sum("invoicePartialAmount").as("partialAmount")
                    .sum("invoiceDelayAmount").as("delayAmount")
                    .sum("invoiceTotalAmount").as("totalAmount")
                    .avg("invoicePartialAmount").as("averagePartialAmount")
                    .avg("invoiceDelayAmount").as("averageDelayAmount")
                    .avg("invoiceTotalAmount").as("averageTotalAmount");
            case "year" -> Aggregation.group("year")
                    .count().as("totalInvoices")
                    .sum(ConditionalOperators.when(Criteria.where("invoicePaymentDate").lte(ConvertOperators.ToDate.toDate("invoiceOverdueDate")))
                            .then(0)
                            .otherwise(1)).as("overdueInvoices")
                    .sum("invoicePartialAmount").as("partialAmount")
                    .sum("invoiceDelayAmount").as("delayAmount")
                    .sum("invoiceTotalAmount").as("totalAmount")
                    .avg("invoicePartialAmount").as("averagePartialAmount")
                    .avg("invoiceDelayAmount").as("averageDelayAmount")
                    .avg("invoiceTotalAmount").as("averageTotalAmount");
            default -> null;
        };
    }

    @Override
    protected ProjectionOperation createFinalProjection(String granularity) {
        ProjectionOperation projectionOperation = Aggregation.project()
                .andExpression("totalInvoices").as("totalInvoices")
                .andExpression("overdueInvoices").as("overdueInvoices")
                .andExpression("partialAmount").as("partialAmount")
                .andExpression("delayAmount").as("delayAmount")
                .andExpression("totalAmount").as("totalAmount")
                .andExpression("averagePartialAmount").as("averagePartialAmount")
                .andExpression("averageDelayAmount").as("averageDelayAmount")
                .andExpression("averageTotalAmount").as("averageTotalAmount")
                .andExpression("delayAmount / totalAmount").as("delayPercentage")
                .andExpression("overdueInvoices / totalInvoices").as("overduePercentage");

        projectionOperation = switch (granularity) {
            case "month" -> projectionOperation
                    .andExpression("toInt(month)").as("month")
                    .andExpression("toInt(year)").as("year");
            case "year" -> projectionOperation
                    .andExpression("toInt(year)").as("year");
            default -> projectionOperation;
        };

        return projectionOperation;
    }

    @Override
    protected SortOperation createSortOperation(String granularity) {
        return switch (granularity) {
            case "month" -> Aggregation.sort(Sort.by(Sort.Order.asc("year"), Sort.Order.asc("month")));
            case "year" -> Aggregation.sort(Sort.by(Sort.Order.asc("year")));
            default -> null;
        };
    }

    @Override
    protected String getCollectionName() {
        return this.mongoTemplate.getCollectionName(Invoice.class);
    }

    @Override
    protected Class<AdminAnalyticsDTO> getDTOClass() {
        return AdminAnalyticsDTO.class;
    }
}
