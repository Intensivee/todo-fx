package org.example.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.example.model.Item;

import java.io.*;
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
            e.printStackTrace();
        }
    }

}
