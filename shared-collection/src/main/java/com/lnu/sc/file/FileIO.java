package com.lnu.sc.file;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
 
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Nils
 */
public class FileIO {
    
    private JSONParser parser = new JSONParser();

    public JSONArray readFile(String filename) {
        JSONArray jsonArray = new JSONArray();
        try {
            Object source = parser.parse(new FileReader(filename));
            JSONObject jsonOSource = (JSONObject) source;
            
            System.out.println("=======read '" + filename + "' begin");
            System.out.println("read jsonObject.size(): " + jsonOSource.size());
            System.out.println("read name: " + jsonOSource.get("name"));
            System.out.println("read content: " + jsonOSource.get("content").toString());

            jsonArray = (JSONArray) jsonOSource.get("content");
            System.out.println("read contentList size: " + jsonArray.size());                

            
//            for (Object obj : jsonArray) {
//                JSONObject jsonObject = (JSONObject) obj;
//                System.out.println("id: " + jsonObject.get("id"));
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return jsonArray;
        
    }
    
    public boolean writeFile(String filename, JSONArray jsonArray) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", filename);
        jsonObject.put("content", jsonArray);
        

        try {
            FileWriter file = new FileWriter(filename);
            file.write(jsonObject.toJSONString());
            System.out.println("Successfully Copied JSON Object to File...");
            System.out.println("\nJSON Object: " + jsonObject);
            
            file.flush();
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return false;
    }

    
}
