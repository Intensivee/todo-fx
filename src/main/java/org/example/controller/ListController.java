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
    private Button addItemButton;
    @FXML
    private ComboBox<Item.Day> dayComboBox;
    @FXML
    private ListView<Item> itemListView;
    @FXML
    private TextArea itemTextArea;


    public static final int MAX_DAY_NOTES = 6;
    public static final int ITEM_DIALOG_WINDOW_PX_X_SIZE = 600;
    public static final int ITEM_DIALOG_WINDOW_PX_Y_SIZE = 300;
    public static final String MENU_ITEM_EDIT = "Edytuj";
    public static final String MENU_ITEM_DELETE = "Usuń";
    public static final String EDIT_ITEM_DIALOG_TITLE= "Edycja notatki";
    public static final String CREATE_ITEM_DIALOG_TITLE= "Nowa notatka";
    public static final String ITEM_DIALOG_WINDOW= "itemDialogWindow.fxml";

    public void initialize() {
        configureList(itemListView);
        dayComboBox.setItems(FXCollections.observableArrayList(Item.Day.MONDAY,Item.Day.TUESDAY, Item.Day.WEDNESDAY,
                Item.Day.THURSDAY, Item.Day.FRIDAY, Item.Day.SATURDAY, Item.Day.SUNDAY));
        dayComboBox.getSelectionModel().select(Item.Day.MONDAY);
        this.dayChangeHandler();
    }


    public void configureList(ListView<Item> listView){
        // show details after selecting heading
        listView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldItem, newItem) -> {
            if (newItem != null) {
                Item item = listView.getSelectionModel().getSelectedItem();
                itemTextArea.setText(item.getNote());
            }
        });

        // add context menu to each item in list
        listView.setCellFactory(lv -> {
            MenuItem editItem = new MenuItem(MENU_ITEM_EDIT);
            MenuItem deleteItem = new MenuItem(MENU_ITEM_DELETE);
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
        this.itemListLimiter();
    }

    public void itemListLimiter() {
        boolean isLimit = (long) itemListView.getItems().size() == MAX_DAY_NOTES;
        addItemButton.setDisable(isLimit);
    }

    @FXML
    public void itemDeleteDialog(Item item){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(MENU_ITEM_DELETE + " " + item.getHeading());
        alert.setHeaderText(null);
        alert.initOwner(mainBorderPane.getScene().getWindow());
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
        Dialog<ButtonType> dialog  = new Dialog<>();
        dialog.setTitle(EDIT_ITEM_DIALOG_TITLE);
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefSize(ITEM_DIALOG_WINDOW_PX_X_SIZE, ITEM_DIALOG_WINDOW_PX_Y_SIZE);
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(ITEM_DIALOG_WINDOW));

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
        Dialog<ButtonType> dialog  = new Dialog<>();
        dialog.setTitle(CREATE_ITEM_DIALOG_TITLE);
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefSize(ITEM_DIALOG_WINDOW_PX_X_SIZE, ITEM_DIALOG_WINDOW_PX_Y_SIZE);
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(ITEM_DIALOG_WINDOW));

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
