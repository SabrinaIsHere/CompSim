package com.morticia.compsim.Util.UI.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class TextWrappingJLabel extends JLabel {
    public TextWrappingJLabel(String text) {
        super(text);
    }

    public String wrapText(String text) {
        FontMetrics metrics = getGraphics().getFontMetrics(getFont());
        String processedText = text;
        processedText = processedText.replaceAll("&nbsp;", " ");
        processedText = stripHtml(processedText);
        String retVal = text;
        int compLength = getParent().getParent().getParent().getSize().width - 350;
        int stringLength = metrics.stringWidth(processedText);

        if (stringLength < compLength) {
            return retVal;
        }

        List<String> words = Arrays.asList(processedText.split(" "));

        int len = 0;
        for (int i = 0; i < words.size() - 1; i++) {
            String currString;
            try {
                currString = words.get(i);
            } catch (Exception ignored) {
                currString = words.get(i);
            }
            if (len + metrics.stringWidth(currString) > compLength) {
                len = 0;
                int count = 0;
                for (int j = 0; j < words.size() - 1; j++) {
                    if (words.get(j).equals(currString)) {
                        count++;
                        if (j == i) {
                            break;
                        }
                    }
                }
                retVal = replaceSubstring(retVal, currString, count, "<br>");
            } else {
                len += metrics.stringWidth(currString);
            }
        }
        return retVal;
    }

    public String replaceSubstring(String string, String regex, int num, String replacement) {
        int subLen = regex.length();

        if (string.length() < subLen) {
            return string;
        }

        int count = 1;
        int iterationNum = string.length() - subLen;
        for (int i = 0; i < iterationNum; i++) {
            String subStr = string.substring(i, i + subLen);
            if (subStr.equals(regex)) {
                if (count == num) {
                    return new StringBuilder(string).insert(i, replacement).toString();
                }
                count++;
            }
        }

        return string;
    }

    public String stripHtml(String in) {
        StringBuilder processedText = new StringBuilder();
        String[] htmlLines = in.split("<");
        //System.out.println(Arrays.toString(htmlLines));
        for (String i : htmlLines) {
            String[] components = i.split(">");
            if (components.length == 2) {
                processedText.append(components[1]);
            }
        }
        //System.out.println(processedText);
        return processedText.toString();
    }
}
