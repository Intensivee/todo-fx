package org.example.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.dao.ItemDao;
import org.example.model.Item;
import org.example.util.Constants;

public class ItemController {

    @FXML
    private TextField headingTextField;
    @FXML
    private TextArea noteTextArea;
    @FXML
    private ComboBox<Item.Day> dayComboBox;
    private Item passedItem;

    public void initialize(){
        dayComboBox.setItems(FXCollections.observableArrayList(Item.Day.Monday,Item.Day.Tuesday, Item.Day.Wednesday,
                Item.Day.Thursday, Item.Day.Friday, Item.Day.Saturday, Item.Day.Sunday));
        this.setTextLimiter();
    }

    public void setTextLimiter(){

        headingTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue != null && headingTextField.getText().length() > Constants.ITEM_HEADING_LENGTH) {
                headingTextField.setText(headingTextField.getText().substring(0, Constants.ITEM_HEADING_LENGTH));
            }
        });

        noteTextArea.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue != null && noteTextArea.getText().length() > Constants.ITEM_NOTE_LENGTH) {
                noteTextArea.setText(noteTextArea.getText().substring(0, Constants.ITEM_NOTE_LENGTH));
            }
        });
    }

    public BooleanExpression emptyInput() {

         return Bindings.createBooleanBinding( () ->
                         headingTextField.getText().isEmpty()||
                         noteTextArea.getText().isEmpty(),
                         headingTextField.textProperty(),
                         noteTextArea.textProperty()
                                                );
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
