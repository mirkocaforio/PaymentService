package it.unisalento.pasproject.requestmanagmentservice.controller;

import it.unisalento.pasproject.requestmanagmentservice.service.RequestQueryFilters;
import it.unisalento.pasproject.requestmanagmentservice.service.RequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import static it.unisalento.pasproject.requestmanagmentservice.security.SecurityConstants.ROLE_ADMIN;

@RestController
@RequestMapping("/api/request")
public class RequestController {
    private final RequestService requestService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestController.class);

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping(value="/find/all")
    @Secured({ROLE_ADMIN})
    public void getAllRequests() {

    }

    @GetMapping(value="/find/{requestEmail}")
    @Secured({ROLE_ADMIN})
    public void getRequest(@PathVariable String requestEmail) {

    }

    @GetMapping(value="/find")
    @Secured({ROLE_ADMIN})
    public void getRequestByFilters(@ModelAttribute RequestQueryFilters filters) {

    }

    @PutMapping(value="/approve/{requestEmail}")
    @Secured({ROLE_ADMIN})
    public void approveRequest(@PathVariable String requestEmail) {

    }

    @PutMapping(value="/reject/{requestEmail}")
    @Secured({ROLE_ADMIN})
    public void rejectRequest(@PathVariable String requestEmail) {

    }
}
