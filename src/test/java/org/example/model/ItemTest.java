package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    private Item item;

    @BeforeEach
    void setUp() {
        this.item = new Item();
    }

    @Test
    void testToString() {
        String expectedHeading = "to string heading";

        this.item.setHeading("to string heading");

        assertEquals(expectedHeading, this.item.toString());
    }

    @Test
    void dayEnumToString() {
        String expected = Item.MONDAY;

        this.item.setDay(Item.Day.MONDAY);

        assertEquals(expected, this.item.getDay().toString());
    }

}
