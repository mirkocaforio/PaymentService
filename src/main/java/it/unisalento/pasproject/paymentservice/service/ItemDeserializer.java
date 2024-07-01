package it.unisalento.pasproject.paymentservice.service;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import it.unisalento.pasproject.paymentservice.domain.Item;
import it.unisalento.pasproject.paymentservice.domain.ItemList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemDeserializer extends JsonDeserializer<ItemList> {
    @Override
    public ItemList deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JacksonException {
        var treeNode = p.getCodec().readTree(p);
        var itemList = new ItemList();

        if (treeNode instanceof ArrayNode arrayNode) {
            for (JsonNode node : arrayNode) {
                var item = deserializeItem(node);
                itemList.getItems().add(item);
            }
        }

        return itemList;
    }

    private Item deserializeItem(JsonNode node) {
        var item = new Item();

        item.setSenderEmail(deserializeSenderEmail(node));
        item.setDescription(deserializeDescription(node));
        item.setAmount(deserializeAmount(node));

        return item;
    }

    private String deserializeSenderEmail(JsonNode node) {
        return node.get("senderEmail").asText();
    }

    private String deserializeDescription(JsonNode node) {
        return node.get("itemDescription").asText();
    }

    private float deserializeAmount(JsonNode node) {
        return node.get("itemAmount").floatValue();
    }

}
