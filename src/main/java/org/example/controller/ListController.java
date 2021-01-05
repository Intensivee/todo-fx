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
        dayComboBox.setItems(FXCollections.observableArrayList(Item.Day.Monday,Item.Day.Tuesday, Item.Day.Wednesday,
                Item.Day.Thursday, Item.Day.Friday, Item.Day.Saturday, Item.Day.Sunday));
        dayComboBox.getSelectionModel().select(Item.Day.Monday);
        this.dayChangeHandler();
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
            editItem.setOnAction(event -> itemEditDialog(listView.getSelectionModel().getSelectedItem()));
            deleteItem.setOnAction(event -> itemDeleteDialog(listView.getSelectionModel().getSelectedItem()));
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

    public void dayChangeHandler(){
        Item.Day selectedDay = dayComboBox.getSelectionModel().getSelectedItem();
        itemListView.setItems(ItemDao.getInstance().getItems(selectedDay));
        itemTextArea.setText("");
        itemListView.getSelectionModel().select(null);
        boolean isLimit = (long) itemListView.getItems().size() == Constants.MAX_DAY_NOTES;
        addItemButton.setDisable(isLimit);
    }

    @FXML
    public void itemDeleteDialog(Item item){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuń " + item.getHeading());
        alert.setHeaderText(null);
        alert.setContentText("Czy napewno chcesz usunąć: " + item.getHeading() + "?");
        alert.getDialogPane().setPrefSize(300, 40);
        Optional<ButtonType> action = alert.showAndWait();
        if(action.isPresent() && action.get() == ButtonType.OK){
            ItemDao.getInstance().deleteItem(item);
            this.dayChangeHandler();
        }
    }

    @FXML
    public void itemEditDialog(Item item) {
        Dialog<ButtonType> dialog  = new Dialog();
        dialog.setTitle("Edycja notatki");
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefSize(600, 300);
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("itemDialogWindow.fxml"));

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
            this.dayChangeHandler();
            itemListView.refresh();
            itemListView.getSelectionModel().select(editedItem);
        }
    }

    @FXML
    public void itemCreateDialog() {
        Dialog<ButtonType> dialog  = new Dialog();
        dialog.setTitle("Nowa notatka");
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefSize(600, 300);
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("itemDialogWindow.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e){
            System.out.println("couldn't load the dialog");
            return;
        }
        ItemController itemController = fxmlLoader.getController();
        itemController.setFields(new Item("", "", dayComboBox.getValue()));
        dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(itemController.emptyInput());

        Optional<ButtonType> response = dialog.showAndWait();
        if(response.isPresent() && response.get() == ButtonType.OK){
          Item item = itemController.createItem();
          dayComboBox.getSelectionModel().select(item.getDay());
          this.dayChangeHandler();
          itemListView.getSelectionModel().select(item);
        }
    }

}
