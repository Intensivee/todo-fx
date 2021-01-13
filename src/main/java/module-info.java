module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.xml;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.xml;


    opens org.example to javafx.fxml, javafx.graphics;
    opens org.example.controller to javafx.fxml, javafx.graphics;

    exports org.example;
    exports org.example.controller;
    exports org.example.model;
}
