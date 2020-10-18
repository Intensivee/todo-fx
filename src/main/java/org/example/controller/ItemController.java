package org.example.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.dao.ItemDao;
import org.example.model.Item;

public class ItemController {

    @FXML
    private TextField headingTextField;
    @FXML
    private TextArea noteTextArea;
    @FXML
    private ComboBox<Item.Day> dayComboBox;
    private Item passedItem;

    public void initialize(){
        dayComboBox.setItems(FXCollections.observableArrayList(Item.Day.MONDAY,Item.Day.TUESDAY, Item.Day.WEDNESDAY,
                Item.Day.THURSDAY, Item.Day.FRIDAY, Item.Day.SATURDAY, Item.Day.SUNDAY));
    }

    public Item createItem(){
        Item item = new Item(headingTextField.getText(),noteTextArea.getText(), dayComboBox.getValue());
        return ItemDao.getInstance().addItem(item);
    }

    public Item editItem(){
        return ItemDao.getInstance().editItem(passedItem,
                                        this.headingTextField.getText(),
                                        this.noteTextArea.getText(),
                                        dayComboBox.getSelectionModel().getSelectedItem());
    }

    public void setFields(Item item) {
        this.headingTextField.setText(item.getHeading());
        this.noteTextArea.setText(item.getNote());
        this.dayComboBox.getSelectionModel().select(item.getDay());
        passedItem = item;
    }
}
