package org.example.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.example.model.Item;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class XmlParser {

    public List<Item> readFromXmlFile() {

        try(InputStream inputStream = new FileInputStream(new File("items.xml"))){

            XmlMapper mapper = new XmlMapper();
            TypeReference<List<Item>> typeReference = new TypeReference<>() {};
            return mapper.readValue(inputStream, typeReference);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    public void writeToXmlFile(List<Item> items) {
        try {
            XmlMapper mapper = new XmlMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(new File("items.xml"), items);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

}
