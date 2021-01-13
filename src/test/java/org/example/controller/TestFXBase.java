package org.example.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.App;
import org.example.model.Item;
import org.junit.jupiter.api.*;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

class TestFXBase extends ApplicationTest{

    String ITEM_LIST_VIEW = "#itemListView";
    String ITEM_TEXT_AREA = "#itemTextArea";
    String ADD_ITEM_BUTTON = "#addItemButton";

    String EXAMPLE_HEADING = "heading_1";
    String EXAMPLE_NOTE = "note_1";

    @Override
    public void start(Stage stage) throws Exception {
        final App app = new App();
        app.start(stage);
    }

    @BeforeEach
    void beforeEach(){
        Platform.runLater( () -> {
            // given
            ListView<Item> itemListView = findFxComponent(ITEM_LIST_VIEW);
            itemListView.getItems();
            itemListView.setItems(FXCollections.observableArrayList(new Item(EXAMPLE_HEADING, EXAMPLE_NOTE, Item.Day.MONDAY)));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @AfterEach
    void afterEach() throws TimeoutException {
        FxToolkit.cleanupStages();
        release(new KeyCode[] {});
        release(new MouseButton[] {});
    }

    @Test
    public void selectingNoteHeading() {
        clickOn(EXAMPLE_HEADING);

        TextArea textArea = findFxComponent(ITEM_TEXT_AREA);
        Assertions.assertEquals(EXAMPLE_NOTE, textArea.getText());
    }

    @Test
    public void createItemDialogHasHeader() {
        clickOn(ADD_ITEM_BUTTON);
        final Stage ItemDialogStage= getTopModalStage();

        Assertions.assertNotNull(ItemDialogStage);

        Assertions.assertEquals(ListController.CREATE_ITEM_DIALOG_TITLE, ItemDialogStage.getTitle());
    }

    @Test
    public void editItemDialogHasHeader() {
        rightClickOn(EXAMPLE_HEADING);
        clickOn(ListController.MENU_ITEM_EDIT);
        final Stage ItemDialogStage= getTopModalStage();

        Assertions.assertNotNull(ItemDialogStage);

        Assertions.assertEquals(ListController.EDIT_ITEM_DIALOG_TITLE, ItemDialogStage.getTitle());
    }

    @Test
    public void deleteItemAlertHasHeader() {
        rightClickOn(EXAMPLE_HEADING);
        clickOn(ListController.MENU_ITEM_DELETE);
        final Stage ItemDialogStage= getTopModalStage();

        Assertions.assertNotNull(ItemDialogStage);

        Assertions.assertEquals(ListController.MENU_ITEM_DELETE + " " + EXAMPLE_HEADING, ItemDialogStage.getTitle());
    }

    private  <T extends Node> T findFxComponent(final String query) {
        return (T) lookup(query).query();
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
