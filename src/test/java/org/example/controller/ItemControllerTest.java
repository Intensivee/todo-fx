package org.example.controller;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.App;
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

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

@ExtendWith(ApplicationExtension.class)
class ItemControllerTest {

    final String HEADING_TEXT_FIELD = "#headingTextField";
    final String NOTE_TEXT_AREA = "#noteTextArea";
    final String DAY_COMBO_BOX = "#dayComboBox";
    final String ADD_ITEM_BUTTON = "#addItemButton";
    final String OK_BUTTON = "OK";

    @Start
    public void start(Stage stage) throws Exception {
        final App app = new App();
        app.start(stage);
    }

    @BeforeEach
    void beforeEach(FxRobot robot) {
        robot.clickOn(ADD_ITEM_BUTTON);
    }

    @AfterEach
    void afterEach() throws TimeoutException {
        FxToolkit.cleanupStages();
    }

    @Test
    void dayComboBoxHasAllDays(FxRobot robot){
        ComboBox<Item.Day> comboBox = robot.lookup(DAY_COMBO_BOX).queryAs(ComboBox.class);
        ObservableList<Item.Day> days = comboBox.getItems();

        for(Item.Day expectedDay : Item.Day.values()) {
            Assertions.assertTrue(days.stream().anyMatch(actualDay -> Objects.equals(expectedDay, actualDay)));
        }
    }

    @Test
    void dayComboBoxSelectsProperDefaultDay(FxRobot robot) {
        ComboBox<Item.Day> comboBox = robot.lookup(DAY_COMBO_BOX).queryAs(ComboBox.class);

        Assertions.assertEquals(Item.Day.MONDAY, comboBox.getSelectionModel().getSelectedItem());
    }

    @Test
    void emptyInputsDisablesButton(FxRobot robot){
        Button button = robot.lookup(OK_BUTTON).queryButton();

        Assertions.assertTrue(button.isDisabled());
    }

    @Test
    void emptyHeaderInputDisablesButton(FxRobot robot){
        TextField headerTextField = robot.lookup(HEADING_TEXT_FIELD).queryAs(TextField.class);
        Button button = robot.lookup(OK_BUTTON).queryButton();

        headerTextField.setText("X");

        Assertions.assertTrue(button.isDisabled());
    }

    @Test
    void emptyNoteInputDisablesButton(FxRobot robot){
        TextArea noteTextArea = robot.lookup(NOTE_TEXT_AREA).queryAs(TextArea.class);
        Button button = robot.lookup(OK_BUTTON).queryButton();

        noteTextArea.setText("X");

        Assertions.assertTrue(button.isDisabled());
    }

    @Test
    void properInputsActivateButton(FxRobot robot){
        TextField headerTextField = robot.lookup(HEADING_TEXT_FIELD).queryAs(TextField.class);
        TextArea noteTextArea = robot.lookup(NOTE_TEXT_AREA).queryAs(TextArea.class);
        Button button = robot.lookup(OK_BUTTON).queryButton();

        headerTextField.setText("X");
        noteTextArea.setText("X");

        Assertions.assertFalse(button.isDisabled());
    }

    @Test
    void headerInputTextLimiter(FxRobot robot){
        TextField headerTextField = robot.lookup(HEADING_TEXT_FIELD).queryAs(TextField.class);

        headerTextField.setText(generateFixedLengthString(ItemController.ITEM_HEADING_LENGTH + 10, 'X'));

        Assertions.assertEquals(ItemController.ITEM_HEADING_LENGTH, headerTextField.getText().length());
    }

    @Test
    void noteInputTextLimiter(FxRobot robot){
        TextArea noteTextArea = robot.lookup(NOTE_TEXT_AREA).queryAs(TextArea.class);

        noteTextArea.setText(generateFixedLengthString(ItemController.ITEM_NOTE_LENGTH + 10, 'X'));

        Assertions.assertEquals(ItemController.ITEM_NOTE_LENGTH, noteTextArea.getText().length());
    }

    private String generateFixedLengthString(int length, char c){
        char[] chars = new char[length];
        Arrays.fill(chars, c);
        return new String(chars);
    }
}
