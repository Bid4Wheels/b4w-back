package com.b4w.b4wback.service;

import java.util.List;

public class JSONBuilder {

    /**
     * Builds the array in json format.
     * The strings should be in "par1":"par2" format
     * @param list the list to pass to JSON
     * @return A String in JSON format
     */
    public static String ArrayToJSON(List<String> list){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append(list.get(i));
            if (i == list.size()-1) break;
            stringBuilder.append(",\n");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
