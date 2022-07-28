package com.morticia.compsim.IO.GUI;

import com.morticia.compsim.IO.IOHandler;
import com.morticia.compsim.RuntimeHandler;
import com.morticia.compsim.Util.Lua.LuaParamData;
import org.luaj.vm2.LuaTable;

import javax.swing.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

// TODO: 7/8/22 Get rid of the code that was only useful for lunan lmao
public class TerminalEventHandler implements MouseListener, MouseWheelListener, KeyListener {
    public Terminal terminal;

    public TerminalEventHandler(Terminal terminal) {
        this.terminal = terminal;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Shift focus on key press events
        terminal.machine.guiHandler.p_terminal = terminal;
        if (e.getKeyCode() == 38) { // Up arrow
            if (terminal.inputIndex == -1) {
                terminal.currInput = ((JTextField) terminal.userInputPanel.getComponent(0)).getText();
            }
            if (terminal.inputIndex < terminal.input.size() - 1) {
                terminal.inputIndex++;
            }
            if (!terminal.input.isEmpty()) {
                ((JTextField) terminal.userInputPanel.getComponent(0)).setText(terminal.input.get(terminal.inputIndex));
            }
            terminal.scrollToBottom();
        } else if (e.getKeyCode() == 40) { // Down arrow
            if (terminal.inputIndex <= 0) {
                ((JTextField) terminal.userInputPanel.getComponent(0)).setText(terminal.currInput);
                terminal.inputIndex = -1;
                return;
            } else {
                terminal.inputIndex--;
            }
            if (!terminal.input.isEmpty()) {
                ((JTextField) terminal.userInputPanel.getComponent(0)).setText(terminal.input.get(terminal.inputIndex));
            }
            terminal.scrollToBottom();
        } else if (e.isControlDown()) {
            if (e.getKeyCode() == 90) { // z
                try {
                    if (terminal.undo.canUndo()) {
                        terminal.undo.undo();
                    }
                } catch (CannotUndoException ignored) {}
            } else if (e.getKeyCode() == 89) { // y
                try {
                    if (terminal.undo.canRedo()) {
                        terminal.undo.redo();
                    }
                } catch (CannotRedoException ignored) {}
            } else if (e.getKeyCode() == 84) { // t
                IOHandler.metaTerminal.toggleVisibility();
            } else {
                List<String> params = new ArrayList<>(terminal.machine.eventHandler.getEvent("ctrl").eventData);
                params.add("key_code: " + e.getKeyCode());
                LuaParamData d = new LuaParamData(params, false);
                d.addTable("m_terminal", terminal.toTable());

                terminal.machine.eventHandler.triggerEvent("ctrl", d);
            }
        }
        List<String> params = new ArrayList<>(terminal.machine.eventHandler.getEvent("key_released").eventData);
        params.add("key_code: " + e.getKeyCode());
        LuaParamData d = new LuaParamData(params, false);
        d.addTable("m_terminal", terminal.toTable());

        terminal.machine.eventHandler.triggerEvent("key_released", d);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.isControlDown()) {
            if (e.getWheelRotation() < 0) {
                terminal.fontSize += terminal.fontSizeQuantum;
                terminal.updateFont();
            } else {
                if (terminal.fontSize > terminal.fontSizeQuantum) {
                    terminal.fontSize -= terminal.fontSizeQuantum;
                }
                terminal.updateFont();
            }
            terminal.scrollToBottom();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        terminal.machine.guiHandler.p_terminal = terminal;
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
