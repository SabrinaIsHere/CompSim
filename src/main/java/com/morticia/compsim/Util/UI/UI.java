package com.morticia.compsim.Util.UI;

import com.morticia.compsim.Util.UI.GUI.MainFrame;

import javax.swing.*;

// TODO: 7/5/22 Make this not static so I can have multiple
public class UI {
    //public static MainFrame mainFrame = new MainFrame();

    // TODO: 7/5/22 remove?
    public static void start() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //mainFrame.show();
            }
        });
    }
}
