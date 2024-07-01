package it.unisalento.pasproject.paymentservice.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ItemList {
    private List<Item> items;

    public ItemList() {
        this.items = new ArrayList<>();
    }
}
