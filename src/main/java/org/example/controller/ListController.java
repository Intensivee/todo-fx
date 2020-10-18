package org.example.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.example.App;
import org.example.dao.ItemDao;
import org.example.model.Item;

import java.io.IOException;
import java.util.Optional;

public class ListController {

    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ListView<Item> itemListView;
    @FXML
    private TextArea itemTextArea;
    @FXML
    private ComboBox<Item.Day> dayComboBox;


    public void initialize() {
        configureList(itemListView);
        dayComboBox.setItems(FXCollections.observableArrayList(Item.Day.MONDAY,Item.Day.TUESDAY, Item.Day.WEDNESDAY,
                Item.Day.THURSDAY, Item.Day.FRIDAY, Item.Day.SATURDAY, Item.Day.SUNDAY));
        dayComboBox.getSelectionModel().select(Item.Day.MONDAY);
    }


    public void configureList(ListView<Item> listView){
        if(listView == null){
            throw new RuntimeException();
        }
        // show details after selecting heading
        listView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldItem, newItem) -> {
            if (newItem != null) {
                Item item = listView.getSelectionModel().getSelectedItem();
                itemTextArea.setText(item.getNote());
            }
        });

        // add context menu to each item in list
        listView.setCellFactory(lv -> {
            MenuItem editItem = new MenuItem("Edit");
            MenuItem deleteItem = new MenuItem("Delete");
            editItem.setOnAction(event -> editDialog(listView.getSelectionModel().getSelectedItem()));
            deleteItem.setOnAction(event -> deleteDialog(listView.getSelectionModel().getSelectedItem()));
            ContextMenu contextMenu = new ContextMenu(editItem, deleteItem);

            ListCell<Item> cell = new ListCell<>(){
                @Override
                protected void updateItem(Item item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty){
                        setText(null);
                    } else {
                        setText(item.getHeading());
                    }
                }
            };

            cell.emptyProperty().addListener((observable, wasEmpty, isEmptyNow)-> {
                if (isEmptyNow) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell;
        });
        listView.setItems(ItemDao.getInstance().getItems());
    }

    public void dayChangeEventHandler(){
        Item.Day selectedDay = dayComboBox.getSelectionModel().getSelectedItem();
        itemListView.setItems(ItemDao.getInstance().getItems(selectedDay));
    }

    @FXML
    public void deleteDialog(Item item){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuń " + item.getHeading());
        alert.setHeaderText(null);
        alert.setContentText("Czy napewno chcesz usunąć: " + item.getHeading() + "?");
        Optional<ButtonType> action = alert.showAndWait();
        if(action.isPresent() && action.get() == ButtonType.OK){
            ItemDao.getInstance().deleteItem(item);
        }
    }

    @FXML
    public void editDialog(Item item) {
        Dialog<ButtonType> dialog  = new Dialog();
        dialog.setTitle("Edycja notatki");
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("itemDialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e){
            e.fillInStackTrace();
        }
        ItemController itemController = fxmlLoader.getController();
        itemController.setFields(itemListView.getSelectionModel().getSelectedItem());

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> response = dialog.showAndWait();

        if(response.isPresent() && response.get() == ButtonType.OK){
            Item editedItem = itemController.editItem();
            dayComboBox.getSelectionModel().select(editedItem.getDay());
            itemListView.refresh();
            itemListView.getSelectionModel().select(editedItem);
        }
    }

    @FXML
    public void createDialog() {
        Dialog<ButtonType> dialog  = new Dialog();
        dialog.setTitle("Nowa notatka");
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("itemDialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e){
            e.fillInStackTrace();
        }
        ItemController itemController = fxmlLoader.getController();
        itemController.setFields(new Item(null, null, dayComboBox.getValue()));

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> response = dialog.showAndWait();

        if(response.isPresent() && response.get() == ButtonType.OK){
          Item item = itemController.createItem();
          dayComboBox.getSelectionModel().select(item.getDay());
          itemListView.getSelectionModel().select(item);
        }
    }

}