package com.morticia.compsim.IO.GUI.MetaTerminal;

import com.morticia.compsim.IO.GUI.TerminalEventHandler;
import com.morticia.compsim.RuntimeHandler;
import com.morticia.compsim.Util.UI.GUI.MainFrame;
import com.morticia.compsim.Util.UI.GUI.TextWrappingJLabel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import static com.morticia.compsim.Util.UI.GUI.MainFrame.defaultHeight;
import static com.morticia.compsim.Util.UI.GUI.MainFrame.defaultWidth;

public class MetaTerminal {
    JFrame frame;
    public JPanel centerPanel;
    public JPanel userInputPanel;

    public JLabel prefixDisplay;

    public boolean inputRequested;
    public int fontSize;
    public int fontSizeQuantum;

    public List<String> input;
    public String currInput;
    public int inputIndex;

    public final UndoManager undo;
    public Document doc;

    public JScrollPane scrollPane;
    public JScrollBar horizontal;
    public JScrollBar vertical;

    public MainFrame mainFrame;

    public JTextField inputField;

    public MetaTerminal() {
        centerPanel = new JPanel();
        userInputPanel = new JPanel();

        prefixDisplay = new JLabel("<html> ");

        inputRequested = false;
        fontSize = 12;
        fontSizeQuantum = 2;

        input = new ArrayList<>();
        currInput = "";
        inputIndex = -1;

        undo = new UndoManager();

        scrollPane = new JScrollPane() {
            @Override
            public void setBorder(Border border) {
                // No border
            }
        };

        inputField = new JTextField() {
            @Override public void setBorder(Border border) {
                // No border
            }
        };

        frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setTitle("Metaterminal");
        frame.setSize(defaultWidth, defaultHeight);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                frame.setVisible(false);
            }
        });

        frame.setBackground(Color.BLACK);

        frame.setVisible(false);
    }

    public void start() {
        centerPanel.setLayout(new GridBagLayout());
        userInputPanel.setLayout(new BorderLayout());

        centerPanel.setAlignmentX(0.0F);
        userInputPanel.setAlignmentX(0.0F);

        centerPanel.setAlignmentY(0.0F);
        userInputPanel.setAlignmentY(0.0F);

        centerPanel.setBackground(Color.BLACK);
        userInputPanel.setBackground(Color.BLACK);

        TextWrappingJLabel outputDisplay = new TextWrappingJLabel("<html>");
        outputDisplay.setBackground(Color.WHITE);
        outputDisplay.setForeground(Color.WHITE);

        scrollPane.addMouseWheelListener(new MetaTerminalEventHandler(this));
        scrollPane.addMouseListener(new MetaTerminalEventHandler(this));

        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        scrollPane.setBackground(Color.BLACK);
        scrollPane.setForeground(Color.WHITE);

        vertical = scrollPane.getVerticalScrollBar();
        horizontal = scrollPane.getHorizontalScrollBar();

        vertical.setUnitIncrement(16);
        horizontal.setUnitIncrement(16);

        vertical.setPreferredSize(new Dimension(0, 0));
        horizontal.setPreferredSize(new Dimension(0, 0));

        GridBagConstraints c1 = new GridBagConstraints();
        c1.gridx = 0;
        c1.gridy = 0;
        c1.gridwidth = GridBagConstraints.REMAINDER;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.FIRST_LINE_START;
        c1.weightx = 1.0F;

        GridBagConstraints c2 = new GridBagConstraints();
        c2.gridx = 0;
        c2.gridy = 1;
        c2.gridwidth = GridBagConstraints.REMAINDER;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.anchor = GridBagConstraints.FIRST_LINE_START;
        c2.weightx = 1.0F;
        c2.weighty = 1.0F;

        centerPanel.add(outputDisplay, c1);
        centerPanel.add(userInputPanel, c2);


        inputField.setCaretColor(Color.WHITE);

        userInputPanel.add(inputField, BorderLayout.CENTER);
        userInputPanel.add(prefixDisplay, BorderLayout.WEST);

        inputField.addActionListener(e -> {
            // New input processing, called when enter is pressed
            if (!inputField.getText().isBlank()) {
                println(prefixDisplay.getText() + inputField.getText());
                MetaTerminalEventHandler.handleInput(inputField.getText());
                inputField.setText("");

                input.add(0, inputField.getText());
                inputIndex = -1;
            }
        });
        // New terminals made here, it isn't working because now it isn't static. Needs to use a different object and pass in this
        inputField.addMouseWheelListener(new MetaTerminalEventHandler(this));
        inputField.addKeyListener(new MetaTerminalEventHandler(this));
        doc = inputField.getDocument();
        doc.addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undo.addEdit(e.getEdit());
            }
        });

        inputField.setForeground(Color.WHITE);
        inputField.setBackground(Color.BLACK);

        prefixDisplay.setOpaque(true);
        prefixDisplay.setForeground(Color.WHITE);
        prefixDisplay.setBackground(Color.BLACK);

        updateFont();

        scrollPane.getViewport().add(centerPanel, BorderLayout.NORTH);
        frame.add(scrollPane);
        SwingUtilities.updateComponentTreeUI(frame);
    }

    public void toggleVisibility() {
        frame.setVisible(!frame.isVisible());
    }

    public void updateFont() {
        Component[] components = centerPanel.getComponents();
        for (Component i : components) {
            i.setFont(new Font("Dialog", Font.PLAIN, fontSize));
        }
        components = userInputPanel.getComponents();
        for (Component i : components) {
            i.setFont(new Font("Dialog", Font.PLAIN, fontSize));
        }
    }

    public void scrollToBottom() {
        vertical.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                Adjustable adjustable = e.getAdjustable();
                adjustable.setValue(adjustable.getMaximum());
                vertical.removeAdjustmentListener(this);
            }
        });
    }

    private final int cmpn = 0;
    protected volatile boolean inputAdded = false;
    public String terminalPrefix = "<html>";

    public void println(Object arg) {
        JLabel label = ((JLabel) centerPanel.getComponent(cmpn));
        String currText = label.getText();
        label.setText(currText + "<br>" + ((TextWrappingJLabel) label).wrapText(arg.toString()));
        scrollToBottom();
    }

    public synchronized void print(Object arg) {
        JLabel label = ((JLabel) centerPanel.getComponent(cmpn));
        String currText = label.getText();
        label.setText(currText + ((TextWrappingJLabel) label).wrapText(arg.toString()));
        scrollToBottom();
    }

    public synchronized String nextLine() {
        inputRequested = true;
        while (input.size() < 1) {
            SwingUtilities.updateComponentTreeUI(userInputPanel);
        }
        inputRequested = false;
        String buffer = input.get(0);
        input.remove(0);
        return buffer;
    }

    public synchronized String nextLine(String in) {
        inputRequested = true;
        prefixDisplay.setText(in);
        while (!inputAdded) {
            Thread.onSpinWait();
        }
        inputAdded = false;
        inputRequested = false;
        String buffer = input.get(0);
        input.remove(0);
        return buffer;
    }

    /**
     * Clears the terminal of all text
     */
    public synchronized void clearTerminal() {
        ((JLabel) centerPanel.getComponent(cmpn)).setText("<html>");
    }

    // I spent 2 hours debugging prefix issues before realizing I had to delete the static keyword I'm so salty ;-;
    private String prefix = "";

    /**
     * Sets the prefix shown to the user on the terminal GUI
     *
     * @param iPrefix New prefix
     */
    public synchronized void setPrefix(String iPrefix) {
        prefix = iPrefix;
        prefixDisplay.setText("<html>" + prefix + "</html>");
        terminalPrefix = "<html>" + prefix;
    }
}
