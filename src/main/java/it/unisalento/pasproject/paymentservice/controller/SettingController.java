package it.unisalento.pasproject.paymentservice.controller;

import it.unisalento.pasproject.paymentservice.dto.SettingDTO;
import it.unisalento.pasproject.paymentservice.service.CheckOutSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import static it.unisalento.pasproject.paymentservice.security.SecurityConstants.ROLE_ADMIN;

@RestController
@RequestMapping("/api/settings/payment")
public class SettingController {
    private final CheckOutSetting checkOutSetting;

    @Autowired
    public SettingController(CheckOutSetting checkOutSetting) {
        this.checkOutSetting = checkOutSetting;
    }

    @GetMapping("/get")
    @Secured(ROLE_ADMIN)
    public SettingDTO getSettings() {
        return checkOutSetting.getSetting();
    }

    @PutMapping("/update")
    @Secured(ROLE_ADMIN)
    public SettingDTO setSettings(@RequestBody SettingDTO settingsDTO) {
        return checkOutSetting.updateSetting(settingsDTO);
    }
}
