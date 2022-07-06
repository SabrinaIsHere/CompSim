package com.morticia.compsim.IO.GUI;

import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Util.UI.GUI.MainFrame;
import com.morticia.compsim.Util.UI.GUI.TextWrappingJLabel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

// Credit to the lunan project for this gui (I wrote that this isn't immoral)
public class Terminal implements MouseWheelListener, KeyListener {
    public int id; // This is used by the machine it's connected to, not an objective id
    public Machine machine;

    // GUI stuff

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

    public Terminal(Machine machine, int id) {
        this.machine = machine;
        this.id = id;

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
    }

    public void start(MainFrame mainFrame) {
        JFrame frame = mainFrame.frame;

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

        scrollPane.addMouseWheelListener(new Terminal(machine, id));

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

        JTextField inputField = new JTextField() {
            @Override public void setBorder(Border border) {
                // No border
            }
        };
        inputField.setCaretColor(Color.WHITE);

        userInputPanel.add(inputField, BorderLayout.CENTER);
        userInputPanel.add(prefixDisplay, BorderLayout.WEST);

        inputField.addActionListener(e -> {
            // New input processing, called when enter is pressed
            if (!inputField.getText().isBlank()) {
                machine.eventHandler.triggerEvent("text_entered", new String[] {
                        "text: " + inputField.getText()
                });

                input.add(0, inputField.getText());
                currInput = "";
                inputIndex = -1;
                inputField.setText("");
            }
        });
        inputField.addMouseWheelListener(new Terminal(machine, id));
        inputField.addKeyListener(new Terminal(machine, id));
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

    public void updateFont() {
        // TODO: 7/5/22 Figure out why this doesn't work
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

    // TODO: 7/5/22 events
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == 38) { // Up arrow
            if (inputIndex == -1) {
                currInput = ((JTextField) userInputPanel.getComponent(0)).getText();
            }
            if (inputIndex < input.size() - 1) {
                inputIndex++;
            }
            if (!input.isEmpty()) {
                ((JTextField) userInputPanel.getComponent(0)).setText(input.get(inputIndex));
            }
        } else if (e.getKeyCode() == 40) { // Down arrow
            if (inputIndex <= 0) {
                ((JTextField) userInputPanel.getComponent(0)).setText(currInput);
                inputIndex = -1;
                return;
            } else {
                inputIndex--;
            }
            if (!input.isEmpty()) {
                ((JTextField) userInputPanel.getComponent(0)).setText(input.get(inputIndex));
            }
        } else if (e.isControlDown()) {
            if (e.getKeyCode() == 90) {
                try {
                    if (undo.canUndo()) {
                        undo.undo();
                    }
                } catch (CannotUndoException ignored) {}
            } else if (e.getKeyCode() == 89) {
                try {
                    if (undo.canRedo()) {
                        undo.redo();
                    }
                } catch (CannotRedoException ignored) {}
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.isControlDown()) {
            if (e.getWheelRotation() < 0) {
                fontSize += fontSizeQuantum;
                updateFont();
            } else {
                if (fontSize > fontSizeQuantum) {
                    fontSize -= fontSizeQuantum;
                }
                updateFont();
            }
        }
    }

    // IO functions

    private final int cmpn = 0; // Component number, codes for output
    protected volatile boolean inputAdded = false;
    public String terminalPrefix = "<html>"; // Copy of the data in prefix display

    /**
     * Prints a line to the terminal, acts similarly to System.out,println()
     *
     * @param arg Thing to be printed to terminal
     */
    public void println(Object arg) {
        JLabel label = ((JLabel) centerPanel.getComponent(cmpn));
        String currText = label.getText();
        label.setText(currText + "<br>" + ((TextWrappingJLabel) label).wrapText(arg.toString()));
        scrollToBottom();
    }

    /**
     * Prints text to the terminal, acts similarly to System.out.print()
     *
     * @param arg Thing to be printed to terminal
     */
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

    private static String prefix = "";

    /**
     * Sets the prefix shown to the user on the terminal GUI
     *
     * @param iPrefix New prefix
     */
    public synchronized void setPrefix(String iPrefix) {
        // TODO: 7/5/22 Call this when user stuff is set up
        prefix = iPrefix;
        prefixDisplay.setText("<html>" + prefix + "</html>");
        terminalPrefix = "<html>" + prefix;
    }

    /**
     * Gets the prefix currently being displayed
     *
     * @return Current prefix
     */
    public synchronized String getPrefix() {
        return prefix;
    }

    public static String colorReset = "<font color=white>";
    public static synchronized String getColor(String hex) {
        return "<font color=" + hex + ">";
    }

    public static synchronized  String wrapInColor(String text, String hex) {
        return getColor(hex) + text + colorReset;
    }
}
