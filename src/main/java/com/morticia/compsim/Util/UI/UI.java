package com.morticia.compsim.Util.UI;

import javax.swing.*;

// TODO: 7/5/22 Make this not static so I can have multiple
public class UI {
    //public static MainFrame mainFrame = new MainFrame();

    // TODO: 7/5/22 remove?
    public static void start() {
        SwingUtilities.invokeLater(() -> {
            //mainFrame.show();
        });
    }
}
