package org.example.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.App;
import org.example.dao.ItemDao;
import org.example.model.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

@ExtendWith(ApplicationExtension.class)
class ListControllerTest {

    final String ITEM_LIST_VIEW = "#itemListView";
    final String ITEM_TEXT_AREA = "#itemTextArea";
    final String ADD_ITEM_BUTTON = "#addItemButton";
    final String DAY_COMBO_BOX = "#dayComboBox";

    final String EXAMPLE_HEADING = "heading_1";
    final String EXAMPLE_NOTE = "note_1";

    @Start
    public void start(Stage stage) throws Exception {
        final App app = new App();
        app.start(stage);
    }

    @BeforeEach
    void beforeEach(FxRobot robot){
        Platform.runLater( () -> {
            ListView<Item> itemListView = (ListView<Item>) robot.lookup(ITEM_LIST_VIEW).query();
            itemListView.setItems(FXCollections.observableArrayList(new Item(EXAMPLE_HEADING, EXAMPLE_NOTE, Item.Day.MONDAY)));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @AfterEach
    void afterEach() throws TimeoutException {
        FxToolkit.cleanupStages();
    }

    @Test
    void selectingNoteHeadingShowNote(FxRobot robot) {
        robot.clickOn(EXAMPLE_HEADING);

        TextArea textArea = robot.lookup(ITEM_TEXT_AREA).queryAs(TextArea.class);
        Assertions.assertEquals(EXAMPLE_NOTE, textArea.getText());
    }

    @Test
    void dayChangeEventHandlerActualizesList(FxRobot robot) {
        ItemDao.setItems(Collections.singletonList(new Item(EXAMPLE_HEADING, EXAMPLE_NOTE, Item.Day.SUNDAY)));

        robot.clickOn(DAY_COMBO_BOX);
        robot.clickOn(Item.SUNDAY);
        robot.clickOn(EXAMPLE_HEADING);

        TextArea textArea = robot.lookup(ITEM_TEXT_AREA).queryAs(TextArea.class);
        Assertions.assertEquals(EXAMPLE_NOTE, textArea.getText());
    }

    @Test
    void opensCreateItemDialog(FxRobot robot) {
        robot.clickOn(ADD_ITEM_BUTTON);
        final Stage ItemDialogStage = getTopModalStage();

        Assertions.assertNotNull(ItemDialogStage);
        Assertions.assertEquals(ListController.CREATE_ITEM_DIALOG_TITLE, ItemDialogStage.getTitle());
    }

    @Test
    void opensEditItemDialog(FxRobot robot) {
        robot.rightClickOn(EXAMPLE_HEADING);
        robot.clickOn(ListController.MENU_ITEM_EDIT);
        final Stage ItemDialogStage= getTopModalStage();

        Assertions.assertNotNull(ItemDialogStage);
        Assertions.assertEquals(ListController.EDIT_ITEM_DIALOG_TITLE, ItemDialogStage.getTitle());
    }

    @Test
    void opensDeleteItemAlert(FxRobot robot) {
        robot.rightClickOn(EXAMPLE_HEADING);
        robot.clickOn(ListController.MENU_ITEM_DELETE);
        final Stage ItemDialogStage = getTopModalStage();

        Assertions.assertNotNull(ItemDialogStage);
        Assertions.assertEquals(ListController.MENU_ITEM_DELETE + " " + EXAMPLE_HEADING, ItemDialogStage.getTitle());
    }

    @Test
    void itemListLimiterDisablesButton(FxRobot robot) {
        List<Item> items = new ArrayList<>();
        for(int i = 0; i < ListController.MAX_DAY_NOTES; i++){
            items.add(new Item(EXAMPLE_HEADING, EXAMPLE_NOTE, Item.Day.SUNDAY));
        }
        ItemDao.setItems(items);

        // refreshing list
        robot.clickOn(DAY_COMBO_BOX);
        robot.clickOn(Item.SUNDAY);
        Button addNewItemButton = robot.lookup(ADD_ITEM_BUTTON).queryButton();

        Assertions.assertTrue(addNewItemButton.isDisabled());
    }

    @Test
    void dayComboBoxLoadsDaysProperly(FxRobot robot){
        ComboBox<Item.Day> comboBox = robot.lookup(DAY_COMBO_BOX).queryAs(ComboBox.class);
        ObservableList<Item.Day> days = comboBox.getItems();

        for(Item.Day expectedDay : Item.Day.values()) {
            Assertions.assertTrue(days.stream().anyMatch(actualDay -> Objects.equals(expectedDay, actualDay)));
        }
    }

    private Stage getTopModalStage() {
        // Get a list of windows but ordered from top[0] to bottom[n] ones.
        // It is needed to get the first found modal window.
        final List<Window> allWindows = new ArrayList<>(new FxRobot().robotContext().getWindowFinder().listWindows());
        Collections.reverse(allWindows);

        return (Stage) allWindows
                .stream()
                .filter(window -> window instanceof Stage)
                .findFirst()
                .orElse(null);
    }
}
