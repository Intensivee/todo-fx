package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemTest {

    private Item item;

    @BeforeEach
    void setUp() {
        this.item = new Item();
    }

    @Test
    void testToString() {
        String EXAMPLE_HEADING = "to string heading";

        this.item.setHeading(EXAMPLE_HEADING);

        assertEquals(EXAMPLE_HEADING, this.item.toString());
    }

    @Test
    void dayEnumToString() {
        String expected = Item.MONDAY;

        this.item.setDay(Item.Day.MONDAY);

        assertEquals(expected, this.item.getDay().toString());
    }

}
