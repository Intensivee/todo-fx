package org.example.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.example.App;
import org.example.dao.ItemDao;
import org.example.model.Item;
import org.example.util.Constants;

import java.io.IOException;
import java.util.Optional;

public class ListController {

    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private Button addItemButton;
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
        this.dayChangeEventHandler();
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
            MenuItem editItem = new MenuItem("Edytuj");
            MenuItem deleteItem = new MenuItem("Usuń");
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
    }

    public void dayChangeEventHandler(){
        Item.Day selectedDay = dayComboBox.getSelectionModel().getSelectedItem();
        itemListView.setItems(ItemDao.getInstance().getItems(selectedDay));
        boolean isLimit = itemListView.getItems().stream().count() == Constants.MAX_DAY_NOTES;
        addItemButton.setDisable(isLimit);
    }

    @FXML
    public void deleteDialog(Item item){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuń " + item.getHeading());
        alert.setHeaderText(null);
        alert.setContentText("Czy napewno chcesz usunąć: " + item.getHeading() + "?");
        Optional<ButtonType> action = alert.showAndWait();
        System.out.println(item);
        if(action.isPresent() && action.get() == ButtonType.OK){
            ItemDao.getInstance().deleteItem(item);
            this.dayChangeEventHandler();
        }
    }

    @FXML
    public void editDialog(Item item) {
        Dialog<ButtonType> dialog  = new Dialog();
        dialog.setTitle("Edycja notatki");
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("itemDialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e){
            e.fillInStackTrace();
        }
        ItemController itemController = fxmlLoader.getController();
        itemController.setFields(itemListView.getSelectionModel().getSelectedItem());
        dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(itemController.emptyInput());

        Optional<ButtonType> response = dialog.showAndWait();

        if(response.isPresent() && response.get() == ButtonType.OK){
            Item editedItem = itemController.editItem();
            dayComboBox.getSelectionModel().select(editedItem.getDay());
            this.dayChangeEventHandler();
            itemListView.refresh();
            itemListView.getSelectionModel().select(editedItem);
        }
    }

    @FXML
    public void createDialog() {
        Dialog<ButtonType> dialog  = new Dialog();
        dialog.setTitle("Nowa notatka");
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("itemDialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e){
            System.out.println("couldn't load the dialog");
            e.fillInStackTrace();
            return;
        }
        ItemController itemController = fxmlLoader.getController();
        itemController.setFields(new Item("", "", dayComboBox.getValue()));
        dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(itemController.emptyInput());

        Optional<ButtonType> response = dialog.showAndWait();
        if(response.isPresent() && response.get() == ButtonType.OK){
          Item item = itemController.createItem();
          dayComboBox.getSelectionModel().select(item.getDay());
          this.dayChangeEventHandler();
          itemListView.getSelectionModel().select(item);

        }
    }

}
