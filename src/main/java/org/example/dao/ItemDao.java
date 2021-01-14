package org.example.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.model.Item;
import org.example.util.XmlParser;

import java.util.List;

public class ItemDao {
    private static final ItemDao instance = new ItemDao();
    private static final XmlParser xmlParser;
    private static ObservableList<Item> items;

    static {
        xmlParser = new XmlParser();
        items = FXCollections.observableArrayList(xmlParser.readFromXmlFile());
    }

    private ItemDao() {}

    public static ItemDao getInstance(){
        return instance;
    }

    public ObservableList<Item> getItems(Item.Day day) {
        return items.filtered(item -> item.getDay().equals(day));
    }

    public void deleteItem(Item item){
        items.remove(item);
        xmlParser.writeToXmlFile(items);
    }

    public Item addItem(Item item){
        for(Item i : items){
            if(item.equals(i)){
                return i;
            }
        }
        items.add(item);

        xmlParser.writeToXmlFile(items);
        return item;
    }

    public static void setItems(List<Item> items){
        ItemDao.items = FXCollections.observableArrayList(items);
    }

    public Item editItem(Item itemToEdit, String heading, String note, Item.Day day) {
        Item editedItem = items.stream().
                filter(itemToEdit::equals)
                .peek(item -> {
                    item.setHeading(heading);
                    item.setNote(note);
                    item.setDay(day);
                })
                .findFirst()
                .orElseThrow(RuntimeException::new);

        xmlParser.writeToXmlFile(items);
        return editedItem;
    }
}
