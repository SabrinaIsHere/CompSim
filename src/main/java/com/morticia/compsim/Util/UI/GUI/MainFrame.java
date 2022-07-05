package com.morticia.compsim.Util.UI.GUI;

import com.morticia.compsim.IO.GUI.Terminal;
import com.morticia.compsim.RuntimeHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame {
    public static final int defaultWidth = 1200;
    public static final int defaultHeight = 800;

    public JFrame frame;

    public MainFrame() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setTitle("CompSim");
        frame.setSize(defaultWidth, defaultHeight);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                RuntimeHandler.stop();
                super.windowClosed(e);
            }
        });

        frame.setBackground(Color.BLACK);
    }

    public void show(Terminal t) {
        t.start(this);
        frame.setVisible(true);
    }

    public void removeAllComponents() {
        frame.getContentPane().removeAll();
        frame.repaint();
    }
}
