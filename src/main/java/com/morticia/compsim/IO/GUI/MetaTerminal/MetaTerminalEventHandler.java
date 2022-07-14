package com.morticia.compsim.IO.GUI.MetaTerminal;

import com.morticia.compsim.IO.IOHandler;

import javax.swing.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class MetaTerminalEventHandler implements MouseListener, MouseWheelListener, KeyListener {
    MetaTerminal meta;

    public MetaTerminalEventHandler(MetaTerminal meta) {
        this.meta = meta;
    }

    public static void handleInput(String input) {
        List<String> args = new ArrayList<>(List.of(input.split(" ")));
        String command = args.get(0);
        args.remove(0);

        MetaTerminal meta = IOHandler.metaTerminal;

        switch (command) {
            case "test":
                meta.println("received");
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == 38) { // Up arrow
            if (meta.inputIndex == -1) {
                meta.currInput = ((JTextField) meta.userInputPanel.getComponent(0)).getText();
            }
            if (meta.inputIndex < meta.input.size() - 1) {
                meta.inputIndex++;
            }
            if (!meta.input.isEmpty()) {
                ((JTextField) meta.userInputPanel.getComponent(0)).setText(meta.input.get(meta.inputIndex));
            }
        } else if (e.getKeyCode() == 40) { // Down arrow
            if (meta.inputIndex <= 0) {
                ((JTextField) meta.userInputPanel.getComponent(0)).setText(meta.currInput);
                meta.inputIndex = -1;
                return;
            } else {
                meta.inputIndex--;
            }
            if (!meta.input.isEmpty()) {
                ((JTextField) meta.userInputPanel.getComponent(0)).setText(meta.input.get(meta.inputIndex));
            }
        } else if (e.isControlDown()) {
            if (e.getKeyCode() == 90) {
                try {
                    if (meta.undo.canUndo()) {
                        meta.undo.undo();
                    }
                } catch (CannotUndoException ignored) {}
            } else if (e.getKeyCode() == 89) {
                try {
                    if (meta.undo.canRedo()) {
                        meta.undo.redo();
                    }
                } catch (CannotRedoException ignored) {}
            }else if (e.getKeyCode() == 84) { // t
                IOHandler.metaTerminal.toggleVisibility();
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

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

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.isControlDown()) {
            if (e.getWheelRotation() < 0) {
                meta.fontSize += meta.fontSizeQuantum;
                meta.updateFont();
            } else {
                if (meta.fontSize > meta.fontSizeQuantum) {
                    meta.fontSize -= meta.fontSizeQuantum;
                }
                meta.updateFont();
            }
        }
    }
}
