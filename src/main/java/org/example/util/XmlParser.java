package org.example.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import javafx.scene.control.Alert;
import org.example.model.Item;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlParser {

    public static final String FILE_NAME = "items.xml";

    public List<Item> readFromXmlFile() {

        try(InputStream inputStream = new FileInputStream(FILE_NAME)){

            XmlMapper mapper = new XmlMapper();
            TypeReference<List<Item>> typeReference = new TypeReference<>() {};
            return mapper.readValue(inputStream, typeReference);
        }
        catch(IOException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Błąd");
            alert.setHeaderText(null);
            alert.setContentText("Wczytanie listy notatek nie powiodło się.\nLista zostanie nadpisana.");
            alert.showAndWait();
            return new ArrayList<>();
        }
    }


    public void writeToXmlFile(List<Item> items) {
        try {
            XmlMapper mapper = new XmlMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(new File(FILE_NAME), items);
        }
        catch (IOException e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Błąd");
            alert.setHeaderText(null);
            alert.setContentText("Zapis listy notatek do pliku nie powiódł się.");
            alert.showAndWait();

            e.printStackTrace();
        }
    }

}
