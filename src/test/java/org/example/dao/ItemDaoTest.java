package org.example.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.model.Item;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class ItemDaoTest {

    @Test
    void filteringListByDay() {
        String exampleNote = "Example note";
        String exampleHeading = "Example heading";
        ItemDao.setItems(FXCollections.observableList(Arrays.asList(
                new Item(exampleHeading, exampleNote, Item.Day.SUNDAY),
                new Item(exampleHeading, exampleNote, Item.Day.SUNDAY),
                new Item(exampleHeading, exampleNote, Item.Day.MONDAY),
                new Item(exampleHeading, exampleNote, Item.Day.THURSDAY)
        )));

        ObservableList<Item> items = ItemDao.getInstance().getItems(Item.Day.SUNDAY);

        Assertions.assertEquals(2, items.size());
    }
}
