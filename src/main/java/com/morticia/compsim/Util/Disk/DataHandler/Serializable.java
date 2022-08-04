package com.morticia.compsim.Util.Disk.DataHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface implemented by anything that can be serialized and saved
 *
 * @author Morticia
 * @version 1.0
 * @since 7/6/22
 */

public interface Serializable {
    static String[] getListMembers(String txt) {
        return txt.replaceAll("\\[", "").replaceAll("]", "").split(", ");
    }

    default String getPrefix() {
        return "[d_" + getDesig() + "--t_" + getType() + "]: ";
    }

    /**
     * Formats a string with a name so it can be retrieved easily later. This mostly exists so it's standardized
     *
     * @param name Name of the string to be formatted
     * @param data Data to be formatted/saved
     * @return Formatted string
     */
    default String prepParam(String name, String data) {
        return "{ " + name + " | " + data + " }";
    }

    /**
     * Extracts name and string from a piece of raw text that was formatted by #prepParam
     *
     * @param raw Raw string to be converted
     * @return The extracted data. First arg will be "n/a" if extraction was unsuccessful, second arg will be raw
     */
    default String[] extractParam(String raw) {
        raw = raw.replaceAll("\\{", "").replaceAll("}", "").strip();
        String[] str_1 = raw.split(" \\| ");
        if (str_1.length == 2) {
            return str_1;
        }
        return new String[] {"n/a", raw};
    }

    /**
     * Formats a list of parameters into a string which can be saved
     *
     * @param params Parameters to format into a string
     * @return Formatted string
     */
    default String prepParams(String[][] params) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < params.length; i++) {
            String[] str_1 = params[i];
            if (str_1.length == 2) {
                if (i + 1 < params.length) {
                    sb.append(prepParam(str_1[0], str_1[1])).append(", ");
                } else {
                    sb.append(prepParam(str_1[0], str_1[1]));
                }
            }
        }
        sb.append("]");
        return sb.toString().strip();
    }

    default List<String[]> extractParams(String raw) {
        List<String[]> retVal = new ArrayList<>();
        String[] str_1 = raw.replaceAll("\\[", "").replaceAll("]", "").strip().split(",");
        for (String i : str_1) {
            String[] str_2 = extractParam(i.strip());
            retVal.add(str_2);
        }
        return retVal;
    }

    /**
     * Gets the type of the data being serialized
     *
     * @return A string denoting how to parse data
     */
    String getType();

    /**
     * Gets the name of the object being serialized, no two objects can share a name
     *
     * @return The name of the serialized object
     */
    String getDesig();

    /**
     * Convert this object into a string for saving to a text file
     *
     * @return This objects data represented as a string
     */
    String serialize();

    /**
     * Convert a string given to data in this object
     *
     * @param txt Text to convert into data
     */
    void parse(String txt);
}
