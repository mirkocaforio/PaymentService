package it.unisalento.pasproject.paymentservice.service;

import it.unisalento.pasproject.paymentservice.controller.InvoiceController;
import it.unisalento.pasproject.paymentservice.domain.Invoice;
import it.unisalento.pasproject.paymentservice.domain.User;
import it.unisalento.pasproject.paymentservice.dto.InvoiceDTO;
import it.unisalento.pasproject.paymentservice.dto.InvoiceListDTO;
import it.unisalento.pasproject.paymentservice.exceptions.InvoiceNotFoundException;
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
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    private final MongoTemplate mongoTemplate;

    private final InvoiceRepository invoiceRepository;

    private final UserRepository userRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceController.class);

    @Autowired
    public PaymentService(MongoTemplate mongoTemplate, InvoiceRepository invoiceRepository, UserRepository userRepository) {
        this.mongoTemplate = mongoTemplate;
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
    }

    public Invoice getInvoice(InvoiceDTO invoiceDTO) {
        Invoice invoice = new Invoice();

        Optional.ofNullable(invoiceDTO.getInvoiceDescription()).ifPresent(invoice::setInvoiceDescription);
        Optional.ofNullable(invoiceDTO.getUserEmail()).ifPresent(invoice::setUserEmail);
        Optional.ofNullable(invoiceDTO.getInvoicePaymentMethod()).ifPresent(invoice::setInvoicePaymentMethod);
        Optional.ofNullable(invoiceDTO.getInvoicePaymentDate()).ifPresent(invoice::setInvoicePaymentDate);
        Optional.ofNullable(invoiceDTO.getInvoiceItems()).ifPresent(invoice::setInvoiceItems);
        Optional.of(invoiceDTO.getInvoiceAmount()).ifPresent(invoice::setInvoiceAmount);
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
        Optional.ofNullable(invoice.getUserEmail()).ifPresent(invoiceDTO::setUserEmail);
        Optional.ofNullable(invoice.getInvoicePaymentMethod()).ifPresent(invoiceDTO::setInvoicePaymentMethod);
        Optional.ofNullable(invoice.getInvoicePaymentDate()).ifPresent(invoiceDTO::setInvoicePaymentDate);
        Optional.ofNullable(invoice.getInvoiceItems()).ifPresent(invoiceDTO::setInvoiceItems);
        Optional.of(invoice.getInvoiceAmount()).ifPresent(invoiceDTO::setInvoiceAmount);
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
        Optional<User> usr = userRepository.findByEmail(invoice.getUserEmail());

        if (usr.isEmpty()) {
            throw new UserNotFoundException("User with email " + invoice.getUserEmail() + " not found");
        }

        User user = usr.get();

        LocalDateTime currentDate = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth cardExpiryDate = YearMonth.parse(user.getCardExpiryDate(), formatter);

        if (cardExpiryDate.isAfter(YearMonth.from(currentDate)) || cardExpiryDate.equals(YearMonth.from(currentDate))) {
            if (invoice.getInvoiceOverdueDate().isAfter(currentDate)) {
                invoice.setInvoiceStatus(Invoice.Status.PAID);
            } else {
                invoice.setInvoiceStatus(Invoice.Status.OVERDUE);
            }
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

        if (paymentQueryFilters.getInvoiceAmount() != 0) {
            query.addCriteria(Criteria.where("invoiceAmount").is(paymentQueryFilters.getInvoiceAmount()));
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
