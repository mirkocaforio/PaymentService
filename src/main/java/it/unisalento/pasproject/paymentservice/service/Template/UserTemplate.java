package it.unisalento.pasproject.paymentservice.service.Template;

import it.unisalento.pasproject.paymentservice.domain.Invoice;
import it.unisalento.pasproject.paymentservice.dto.UserAnalyticsDTO;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDateTime;
import java.util.List;

public class UserTemplate extends AnalyticsTemplate<UserAnalyticsDTO> {
    public UserTemplate(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }

    @Override
    public List<UserAnalyticsDTO> getAnalyticsList(String email, LocalDateTime startDate, LocalDateTime endDate, String granularity) {
        return super.getAnalyticsList(email, startDate, endDate, granularity);
    }

    @Override
    protected MatchOperation createMatchOperation(String email, LocalDateTime startDate, LocalDateTime endDate) {
        MatchOperation matchOperation;

        if (startDate != null && endDate != null) {
            matchOperation = Aggregation.match(
                    Criteria.where("userEmail").is(email)
                            .andOperator(
                                    Criteria.where("invoicePaymentDate").gte(startDate).lte(endDate),
                                    Criteria.where("invoiceStatus").is(Invoice.Status.PAID)
                            )
            );
        } else if (startDate != null) {
            matchOperation = Aggregation.match(
                    Criteria.where("userEmail").is(email)
                            .andOperator(
                                    Criteria.where("invoicePaymentDate").gte(startDate),
                                    Criteria.where("invoiceStatus").is(Invoice.Status.PAID)
                            )
            );
        } else if (endDate != null) {
            matchOperation = Aggregation.match(
                    Criteria.where("userEmail").is(email)
                            .andOperator(
                                    Criteria.where("invoicePaymentDate").lte(endDate),
                                    Criteria.where("invoiceStatus").is(Invoice.Status.PAID)
                            )
            );
        } else {
            matchOperation = Aggregation.match(
                    Criteria.where("userEmail").is(email)
                            .andOperator(
                                    Criteria.where("invoiceStatus").is(Invoice.Status.PAID)
                            )
            );
        }

        return matchOperation;
    }

    @Override
    protected List<AggregationOperation> getAdditionalOperations() {
        return List.of();
    }

    @Override
    protected ProjectionOperation createProjectionOperation() {
        return Aggregation.project()
                .andInclude(
                        "userEmail",
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
            case "month" -> Aggregation.group("userEmail", "year", "month")
                    .sum("invoicePartialAmount").as("partialAmount")
                    .sum("invoiceDelayAmount").as("delayAmount")
                    .sum("invoiceTotalAmount").as("totalAmount");
            case "year" -> Aggregation.group("userEmail", "year")
                    .count().as("totalInvoices")
                    .sum(ConditionalOperators.when(Criteria.where("invoicePaymentDate").lte("invoiceOverdueDate"))
                            .then(1)
                            .otherwise(0)).as("overdueInvoices")
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
                .andExpression("userEmail").as("userEmail")
                .andExpression("partialAmount").as("partialAmount")
                .andExpression("delayAmount").as("delayAmount")
                .andExpression("totalAmount").as("totalAmount");

        projectionOperation = switch (granularity) {
            case "month" -> projectionOperation
                    .andExpression("toInt(month)").as("month")
                    .andExpression("toInt(year)").as("year");
            case "year" -> projectionOperation
                    .andExpression("totalInvoices").as("totalInvoices")
                    .andExpression("overdueInvoices").as("overdueInvoices")
                    .andExpression("toInt(year)").as("year")
                    .andExpression("averagePartialAmount").as("averagePartialAmount")
                    .andExpression("averageDelayAmount").as("averageDelayAmount")
                    .andExpression("averageTotalAmount").as("averageTotalAmount")
                    .andExpression("delayAmount / totalAmount").as("delayPercentage")
                    .andExpression("overdueInvoices / totalInvoices").as("overduePercentage");
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
    protected Class<UserAnalyticsDTO> getDTOClass() {
        return UserAnalyticsDTO.class;
    }
}
