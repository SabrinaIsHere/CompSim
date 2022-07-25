package com.morticia.compsim.IO.GUI;

import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Machine.MachineIOStream.IOComponent;
import com.morticia.compsim.Machine.MachineIOStream.MachineIOStream;
import com.morticia.compsim.RuntimeHandler;
import com.morticia.compsim.Util.Lua.Lib.TerminalLib;
import com.morticia.compsim.Util.Lua.LuaParamData;
import com.morticia.compsim.Util.UI.GUI.MainFrame;
import com.morticia.compsim.Util.UI.GUI.TextWrappingJLabel;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

// Credit to the lunan project for this gui (I wrote that this isn't immoral)
public class Terminal implements IOComponent {
    public int id; // This is used by the machine it's connected to, not an objective id
    public Machine machine;
    public boolean ready;

    // GUI stuff

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

    public JTextField inputField;

    public Terminal(Machine machine, int id) {
        this.machine = machine;
        this.id = id;
        this.ready = false;

        if (machine.guiHandler.p_terminal == null) {
            machine.guiHandler.p_terminal = this;
            machine.defaultStream = new MachineIOStream("terminal_" + id, this);
        }

        centerPanel = new JPanel();
        userInputPanel = new JPanel();

        prefixDisplay = new JLabel("<html>");

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
    }

    public void start(MainFrame mainFrame) {
        frame = mainFrame.frame;

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

        scrollPane.addMouseWheelListener(new TerminalEventHandler(this));
        scrollPane.addMouseListener(new TerminalEventHandler(this));

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
                // For this specific use case I have to trigger the event because if I don't the graphics start to glitch
                // This is such a mess I got 5 hours of sleep last night help sjkhghdfgkh
                String text = inputField.getText().strip();
                List<String> str = new ArrayList<>(List.of(text.split(" ")));
                List<String> params = new ArrayList<>(machine.eventHandler.getEvent("text_entered").eventData);
                params.add("text: " + inputField.getText());
                //params.add("command: " + str.get(0));
                LuaParamData d = new LuaParamData(params, false);
                str.remove(0);
                LuaTable table = new LuaTable();
                for (int i = 0; i < str.size(); i++) {
                    table.set(i + 1, str.get(i));
                }
                //d.addTable("args", table);
                d.addTable("m_terminal", toTable());

                input.add(0, inputField.getText());
                inputIndex = -1;

                // Ik this is a weird place to do this but it has to go somewhere that isn't called hundreds of times per second
                RuntimeHandler.machineHandler.updateFilesystems();

                machine.eventHandler.triggerEvent("text_entered", d);
            }
        });
        // New terminals made here, it isn't working because now it isn't static. Needs to use a different object and pass in this
        inputField.addMouseWheelListener(new TerminalEventHandler(this));
        inputField.addKeyListener(new TerminalEventHandler(this));
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

        UIManager.getDefaults().put("ScrollPane.ancestorInputMap",
                new UIDefaults.LazyInputMap(new Object[] {}));

        scrollPane.getViewport().add(centerPanel, BorderLayout.NORTH);
        frame.add(scrollPane);
        SwingUtilities.updateComponentTreeUI(frame);
        this.ready = true;
    }

    public String getTitle() {
        return frame.getTitle();
    }

    public void setTitle(String title) {
        frame.setTitle(title);
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
        scrollToBottom();
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

    // IO functions

    public final int cmpn = 0; // Component number, codes for output
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
        label.setText(currText + "<br>" + ((TextWrappingJLabel) label).wrapText(arg.toString()).replaceAll("\n", "<br>"));
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
        label.setText(currText + ((TextWrappingJLabel) label).wrapText(arg.toString().replaceAll("\n", "<br>")));
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

    public LuaTable toTable() {
        LuaTable retVal = new LuaTable();
        retVal.set("is_null", LuaValue.valueOf(false));
        retVal.set("id", id);
        retVal.set("object_type", "terminal");
        retVal.set("update", new TerminalLib.update(machine, id));
        retVal.set("is_ready", new TerminalLib.is_ready(machine, id));
        retVal.set("get_prefix", new TerminalLib.get_prefix(this));
        retVal.set("set_prefix", new TerminalLib.set_prefix(this));
        retVal.set("get_buffer", new TerminalLib.get_buffer(this));
        retVal.set("set_buffer", new TerminalLib.set_buffer(this));
        retVal.set("print", new TerminalLib.print(getStream()));
        retVal.set("write", new TerminalLib.write(getStream()));
        retVal.set("get_title", new TerminalLib.get_title(this));
        retVal.set("set_title", new TerminalLib.set_title(this));
        retVal.set("set_output", new TerminalLib.set_output(this));
        retVal.set("get_text", new TerminalLib.get_text(this));
        retVal.set("set_text", new TerminalLib.set_text(this));
        retVal.set("get_line", new TerminalLib.get_line(this));
        return retVal;
    }

    public MachineIOStream getStream() {
        return new MachineIOStream("terminal_" + id, this);
    }

    @Override
    public String readLine() {
        return nextLine();
    }

    @Override
    public void writeLine(String data) {
        print(data);
    }

    @Override
    public LuaTable getAllData() {
        LuaTable table = new LuaTable();
        String[] str = ((JLabel) centerPanel.getComponent(cmpn)).getText().split("\n");
        for (int i = 0; i < str.length; i++) {
            table.set(i + 1, str[i]);
        }
        return table;
    }
}
