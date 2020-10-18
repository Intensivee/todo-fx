package org.example.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.model.Item;

import java.util.Arrays;

public class ItemDao {
    private static final ItemDao instance = new ItemDao();
    private static ObservableList<Item> items;

    static {
        items = FXCollections.observableArrayList(Arrays.asList(
                new Item("halo", "sagsagasagsags", Item.Day.MONDAY),
                new Item("alo", "xxxxxxxxxxxxxxxxxxxxx", Item.Day.MONDAY),
                new Item("olo", "asdsdax", Item.Day.FRIDAY),
                new Item("kolo", "yyyyyyyyyyyyy", Item.Day.SATURDAY),
                new Item("dsaas", "aaaaaaaaaaaaaa", Item.Day.SUNDAY)
        ));
    }

    private ItemDao() {}

    public static ItemDao getInstance(){
        return instance;
    }

    public ObservableList<Item> getItems() {
        return items;
    }
    public ObservableList<Item> getItems(Item.Day day) {
        return items.filtered(item -> item.getDay().equals(day));
    }

    public void deleteItem(Item item){
        items.remove(item);
    }

    public Item addItem(Item item){
        for(Item i : items){
            if(item.equals(i)){
                return i;
            }
        }
        items.add(item);
        return item;
    }

    public Item editItem(Item itemToEdit, String heading, String note, Item.Day day) {
        return items.stream().
                filter(itemToEdit::equals)
                .peek(item -> {
                    item.setHeading(heading);
                    item.setNote(note);
                    item.setDay(day);
                })
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }
}
