package it.unisalento.pasproject.paymentservice.service;

import it.unisalento.pasproject.paymentservice.domain.Setting;
import it.unisalento.pasproject.paymentservice.dto.SettingDTO;
import it.unisalento.pasproject.paymentservice.repositories.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CheckOutSetting {
    @Value("${settings.id}")
    private String settingId;

    @Value("${medium.energy.cost.init}")
    private double mediumEnergyCost;

    @Value("${medium.resource.consumption.init}")
    private float mediumResourceConsumption;

    @Value("${change.constant.init}")
    private float changeConstant;

    private final SettingRepository settingsRepository;

    @Autowired
    public CheckOutSetting(SettingRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public Setting getSetting(SettingDTO settingDTO) {
        Setting setting = new Setting();

        Optional.of(settingDTO.getMediumEnergyCost()).ifPresent(setting::setMediumEnergyCost);
        Optional.of(settingDTO.getMediumResourceConsumption()).ifPresent(setting::setMediumResourceConsumption);
        Optional.of(settingDTO.getChangeConstant()).ifPresent(setting::setChangeConstant);

        return setting;
    }

    public SettingDTO getSettingDTO(Setting setting) {
        SettingDTO settingDTO = new SettingDTO();

        Optional.of(setting.getId()).ifPresent(settingDTO::setId);
        Optional.of(setting.getMediumEnergyCost()).ifPresent(settingDTO::setMediumEnergyCost);
        Optional.of(setting.getMediumResourceConsumption()).ifPresent(settingDTO::setMediumResourceConsumption);
        Optional.of(setting.getChangeConstant()).ifPresent(settingDTO::setChangeConstant);

        return settingDTO;
    }

    public float convertCreditsToMoney(float credits) {
        double creditValue = mediumEnergyCost * mediumResourceConsumption;
        creditValue += creditValue * changeConstant;
        return (float) (credits * creditValue);
    }

    public SettingDTO getSetting() {
        Optional<Setting> setting = settingsRepository.findById(settingId);

        if (setting.isPresent()) {
            return getSettingDTO(setting.get());
        } else {
            Setting newSetting = createSetting();
            return getSettingDTO(settingsRepository.save(newSetting));
        }
    }

    public SettingDTO updateSetting(SettingDTO settingDTO) {
        Optional<Setting> setting = settingsRepository.findById(settingId);

        if (setting.isPresent()) {
            Setting settingToUpdate = setting.get();
            settingToUpdate.setMediumEnergyCost(settingDTO.getMediumEnergyCost());
            settingToUpdate.setMediumResourceConsumption(settingDTO.getMediumResourceConsumption());
            settingToUpdate.setChangeConstant(settingDTO.getChangeConstant());
            return getSettingDTO(settingsRepository.save(settingToUpdate));
        } else {
            Setting newSetting = createSetting();
            return getSettingDTO(settingsRepository.save(newSetting));
        }
    }

    public Setting createSetting() {
        Setting newSetting = new Setting();
        newSetting.setId(settingId);
        newSetting.setMediumEnergyCost(mediumEnergyCost);
        newSetting.setMediumResourceConsumption(mediumResourceConsumption);
        newSetting.setChangeConstant(changeConstant);

        return settingsRepository.save(newSetting);
    }
}
