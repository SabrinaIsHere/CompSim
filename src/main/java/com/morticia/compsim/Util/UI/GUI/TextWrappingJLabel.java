package com.morticia.compsim.Util.UI.GUI;

import com.morticia.compsim.Util.Constants;

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
        StringBuilder builder = new StringBuilder();
        String[] str = text.replaceAll("<br>", "\n").split("\n");
        for (String ln : str) {
            String processedText = ln;
            processedText = stripHtml(processedText);
            String retVal = ln;
            int compLength = getParent().getParent().getParent().getSize().width;
            int stringLength = metrics.stringWidth(processedText);

            if (stringLength < compLength) {
                builder.append(retVal).append("<br>");
                continue;
            }

            List<String> words = Arrays.asList(processedText.split(" "));

            int len = 0;
            for (int i = 0; i < words.size(); i++) {
                String currString;
                try {
                    currString = words.get(i);
                } catch (Exception ignored) {
                    currString = words.get(i);
                }
                int strlen = metrics.stringWidth(currString + " ");
                if (len + strlen > compLength) {
                    len = strlen;
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
                    len += strlen;
                }
            }
            builder.append(retVal).append("<br>");
        }
        return builder.toString();
    }

    /**
     * Replaces a specific subtring with another, has to be weird because of the html/nonhtml thing
     *
     * @param string Master string to manipulate
     * @param regex Text to replace
     * @param num Which match to the regex to be replaced
     * @param replacement New string
     * @return Modified master string
     */
    public String replaceSubstring(String string, String regex, int num, String replacement) {
        int subLen = regex.length();

        if (string.length() < subLen) {
            return string;
        }

        if (num < 1) {
            num = 1;
        }

        int count = 1;
        int iterationNum = string.length() + 1 - subLen;
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
        in = in.replaceAll(Constants.htmlSpace, " ");
        if (in.contains("<") && in.contains(">")) {
            String[] htmlLines = in.split("<");
            for (String i : htmlLines) {
                String[] components = i.split(">");
                if (components.length == 2) {
                    processedText.append(components[1]);
                }
            }
            return processedText.toString();
        } else {
            return in;
        }
    }
}
