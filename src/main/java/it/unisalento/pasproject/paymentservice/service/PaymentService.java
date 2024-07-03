package it.unisalento.pasproject.paymentservice.service;

import it.unisalento.pasproject.paymentservice.business.CreditCardValidationStrategy;
import it.unisalento.pasproject.paymentservice.controller.InvoiceController;
import it.unisalento.pasproject.paymentservice.domain.Invoice;
import it.unisalento.pasproject.paymentservice.domain.User;
import it.unisalento.pasproject.paymentservice.dto.InvoiceDTO;
import it.unisalento.pasproject.paymentservice.dto.InvoiceListDTO;
import it.unisalento.pasproject.paymentservice.exceptions.InvoiceNotFoundException;
import it.unisalento.pasproject.paymentservice.exceptions.PaymentMethodInvalidException;
import it.unisalento.pasproject.paymentservice.exceptions.UserNotFoundException;
import it.unisalento.pasproject.paymentservice.repositories.InvoiceRepository;
import it.unisalento.pasproject.paymentservice.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    private final MongoTemplate mongoTemplate;

    private final InvoiceRepository invoiceRepository;

    private final UserRepository userRepository;

    private final CreditCardValidationStrategy creditCardValidationStrategy;

    private final CheckOutSetting checkOutSetting;

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceController.class);

    @Autowired
    public PaymentService(MongoTemplate mongoTemplate, InvoiceRepository invoiceRepository, UserRepository userRepository, CreditCardValidationStrategy creditCardValidationStrategy, CheckOutSetting checkOutSetting) {
        this.mongoTemplate = mongoTemplate;
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
        this.creditCardValidationStrategy = creditCardValidationStrategy;
        this.checkOutSetting = checkOutSetting;
    }

    public Invoice getInvoice(InvoiceDTO invoiceDTO) {
        Invoice invoice = new Invoice();

        Optional.ofNullable(invoiceDTO.getInvoiceDescription()).ifPresent(invoice::setInvoiceDescription);
        Optional.ofNullable(invoiceDTO.getUserName()).ifPresent(invoice::setUserName);
        Optional.ofNullable(invoiceDTO.getUserSurname()).ifPresent(invoice::setUserSurname);
        Optional.ofNullable(invoiceDTO.getUserEmail()).ifPresent(invoice::setUserEmail);
        Optional.ofNullable(invoiceDTO.getUserResidenceCity()).ifPresent(invoice::setUserResidenceCity);
        Optional.ofNullable(invoiceDTO.getUserResidenceAddress()).ifPresent(invoice::setUserResidenceAddress);
        Optional.ofNullable(invoiceDTO.getInvoicePaymentMethod()).ifPresent(invoice::setInvoicePaymentMethod);
        Optional.ofNullable(invoiceDTO.getInvoicePaymentDate()).ifPresent(invoice::setInvoicePaymentDate);
        Optional.ofNullable(invoiceDTO.getInvoiceItems()).ifPresent(invoice::setInvoiceItems);
        Optional.of(invoiceDTO.getInvoicePartialAmount()).ifPresent(invoice::setInvoicePartialAmount);
        Optional.of(invoiceDTO.getInvoiceDelayAmount()).ifPresent(invoice::setInvoiceDelayAmount);
        Optional.of(invoiceDTO.getInvoiceTotalAmount()).ifPresent(invoice::setInvoiceTotalAmount);
        Optional.ofNullable(invoiceDTO.getInvoiceStatus())
                .map(Enum::name)
                .map(Invoice.Status::valueOf)
                .ifPresent(invoice::setInvoiceStatus);
        Optional.ofNullable(invoiceDTO.getInvoiceOverdueDate()).ifPresent(invoice::setInvoiceOverdueDate);

        return invoice;
    }

    public InvoiceDTO getInvoiceDTO(Invoice invoice) {
        InvoiceDTO invoiceDTO = new InvoiceDTO();

        Optional.ofNullable(invoice.getInvoiceNumber()).ifPresent(invoiceDTO::setInvoiceNumber);
        Optional.ofNullable(invoice.getInvoiceDescription()).ifPresent(invoiceDTO::setInvoiceDescription);
        Optional.ofNullable(invoice.getUserName()).ifPresent(invoiceDTO::setUserName);
        Optional.ofNullable(invoice.getUserSurname()).ifPresent(invoiceDTO::setUserSurname);
        Optional.ofNullable(invoice.getUserEmail()).ifPresent(invoiceDTO::setUserEmail);
        Optional.ofNullable(invoice.getUserResidenceCity()).ifPresent(invoiceDTO::setUserResidenceCity);
        Optional.ofNullable(invoice.getUserResidenceAddress()).ifPresent(invoiceDTO::setUserResidenceAddress);
        Optional.ofNullable(invoice.getInvoicePaymentMethod()).ifPresent(invoiceDTO::setInvoicePaymentMethod);
        Optional.ofNullable(invoice.getInvoicePaymentDate()).ifPresent(invoiceDTO::setInvoicePaymentDate);
        Optional.ofNullable(invoice.getInvoiceItems()).ifPresent(invoiceDTO::setInvoiceItems);
        Optional.of(invoice.getInvoicePartialAmount()).ifPresent(invoiceDTO::setInvoicePartialAmount);
        Optional.of(invoice.getInvoiceDelayAmount()).ifPresent(invoiceDTO::setInvoiceDelayAmount);
        Optional.of(invoice.getInvoiceTotalAmount()).ifPresent(invoiceDTO::setInvoiceTotalAmount);
        Optional.ofNullable(invoice.getInvoiceStatus())
                .map(Enum::name)
                .map(InvoiceDTO.Status::valueOf)
                .ifPresent(invoiceDTO::setInvoiceStatus);
        Optional.ofNullable(invoice.getInvoiceOverdueDate()).ifPresent(invoiceDTO::setInvoiceOverdueDate);

        return invoiceDTO;
    }

    public InvoiceListDTO getInvoiceListDTO(List<Invoice> invoices) {
        InvoiceListDTO invoiceListDTO = new InvoiceListDTO();
        List<InvoiceDTO> list = invoiceListDTO.getInvoicesList();
        invoiceListDTO.setInvoicesList(list);

        for (Invoice invoice : invoices) {
            list.add(getInvoiceDTO(invoice));
        }

        return invoiceListDTO;
    }

    public InvoiceListDTO getAllInvoices() {
        List<Invoice> invoices = invoiceRepository.findAll();

        return getInvoiceListDTO(invoices);
    }

    public InvoiceListDTO getInvoicesByFilters(PaymentQueryFilters paymentQueryFilters) {
        List<Invoice> invoices = findInvoice(paymentQueryFilters);

        return getInvoiceListDTO(invoices);
    }

    public Invoice updateInvoice(Invoice invoice) {
        Optional<User> usr = userRepository.findByUserEmail(invoice.getUserEmail());

        if (usr.isEmpty()) {
            throw new UserNotFoundException("User with email " + invoice.getUserEmail() + " not found");
        }

        User user = usr.get();

        if (creditCardValidationStrategy.isPaymentMethodValid(user)) {
            if (invoice.getInvoiceStatus().equals(Invoice.Status.PENDING)) {
                invoice.setInvoiceStatus(Invoice.Status.PAID);
                invoice.setInvoicePaymentDate(LocalDateTime.now());
            } else if (invoice.getInvoiceStatus().equals(Invoice.Status.OVERDUE)) {
                float delayAmount = checkOutSetting.calculateAmountWithDelay(invoice.getInvoiceTotalAmount(), invoice.getInvoiceOverdueDate());
                invoice.setInvoiceDelayAmount(delayAmount);
                invoice.setInvoiceTotalAmount(invoice.getInvoiceTotalAmount() + delayAmount);
                invoice.setInvoiceStatus(Invoice.Status.PAID);
                invoice.setInvoicePaymentDate(LocalDateTime.now());
            }
        } else {
            throw new PaymentMethodInvalidException("Payment method is invalid");
        }

        return invoice;
    }

    public InvoiceDTO updateInvoice(String invoiceNumber) {
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(invoiceNumber);

        if (optionalInvoice.isEmpty()) {
            throw new InvoiceNotFoundException("Invoice with invoice number " + invoiceNumber + " not found");
        }

        Invoice invoice = optionalInvoice.get();

        Invoice updatedInvoice = updateInvoice(invoice);

        invoiceRepository.save(updatedInvoice);

        return getInvoiceDTO(updatedInvoice);
    }

    public List<Invoice> findInvoice(PaymentQueryFilters paymentQueryFilters) {
        Query query = new Query();

        if (paymentQueryFilters.getUserEmail() != null) {
            query.addCriteria(Criteria.where("userEmail").is(paymentQueryFilters.getUserEmail()));
        }

        if (paymentQueryFilters.getInvoicePaymentMethod() != null) {
            query.addCriteria(Criteria.where("invoicePaymentMethod").is(paymentQueryFilters.getInvoicePaymentMethod()));
        }

        if (paymentQueryFilters.getInvoicePaymentDate() != null) {
            query.addCriteria(Criteria.where("invoicePaymentDate").is(paymentQueryFilters.getInvoicePaymentDate()));
        }

        if (paymentQueryFilters.getInvoiceTotalAmount() != 0) {
            query.addCriteria(Criteria.where("invoiceTotalAmount").is(paymentQueryFilters.getInvoiceTotalAmount()));
        }

        if (paymentQueryFilters.getInvoiceStatus() != null) {
            query.addCriteria(Criteria.where("invoiceStatus").is(paymentQueryFilters.getInvoiceStatus()));
        }

        if (paymentQueryFilters.getInvoiceOverdueDate() != null) {
            query.addCriteria(Criteria.where("invoiceOverdueDate").is(paymentQueryFilters.getInvoiceOverdueDate()));
        }

        LOGGER.info("\n{}\n", query);

        List<Invoice> invoices = mongoTemplate.find(query, Invoice.class, mongoTemplate.getCollectionName(Invoice.class));

        LOGGER.info("\nInvoices: {}\n", invoices);

        return invoices;
    }
}
