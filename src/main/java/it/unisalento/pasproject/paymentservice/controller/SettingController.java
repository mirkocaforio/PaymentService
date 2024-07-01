package it.unisalento.pasproject.paymentservice.controller;

import it.unisalento.pasproject.paymentservice.dto.SettingDTO;
import it.unisalento.pasproject.paymentservice.service.CheckOutSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment/settings")
public class SettingController {
    private final CheckOutSetting checkOutSetting;

    @Autowired
    public SettingController(CheckOutSetting checkOutSetting) {
        this.checkOutSetting = checkOutSetting;
    }

    @GetMapping("/get")
    public SettingDTO getSettings() {
        return checkOutSetting.getSetting();
    }

    @PutMapping("/update")
    public SettingDTO setSettings(@RequestBody SettingDTO settingsDTO) {
        return checkOutSetting.updateSetting(settingsDTO);
    }
}
