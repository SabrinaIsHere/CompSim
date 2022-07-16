package com.morticia.compsim.IO.GUI.MetaTerminal;

import com.morticia.compsim.IO.IOHandler;
import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Machine.MachineHandler;
import com.morticia.compsim.RuntimeHandler;
import com.morticia.compsim.Util.Disk.DiskUtil;

import javax.swing.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The metaterminal is a static terminal used to interact with the "real world" (requisition new computers, check base email, etc)
 *
 * @author Morticia
 * @version 1.0
 * @since 7/14/22
 */

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

        // These commands are mostly for debugging and making new machines and stuff. When the story is implemented
        // this won't be at all the same
        label:
        switch (command) {
            case "help":
                meta.println("list_machines\nmk_machine [string name]\nrm_machine [string name]\nopen_terminal [string name]");
                break;
            case "list_machines":
                // TODO: 7/14/22 During story mode make this more selective
                for (Machine i : RuntimeHandler.machineHandler.machines) {
                    meta.println(i.desig);
                }
                break;
            case "mk_machine":
                if (args.size() < 1) {meta.println("Please enter [1] argument"); break;}
                RuntimeHandler.machineHandler.machines.add(new Machine(args.get(0)));
                meta.println("Machine created");
                break;
            case "rm_machine":
                if (args.size() < 1) {meta.println("Please enter [1] argument"); break;}
                for (Machine i : RuntimeHandler.machineHandler.machines) {
                    if (i.desig.equals(args.get(0))) {
                        i.save();
                        meta.println(RuntimeHandler.machineHandler.machines.remove(i) && DiskUtil.deleteFolder("/Machines/" + i.desig)
                        ? "Removed" : "No such machine found");
                        break label;
                    }
                }
                meta.println("No such machine found");
                break;
            case "open_terminal":
                if (args.size() < 1) {meta.println("Please enter [1] argument"); break;}
                for (Machine i : RuntimeHandler.machineHandler.machines) {
                    if (i.desig.equals(args.get(0))) {
                        i.guiHandler.startTerminal();
                        meta.println("Terminal started");
                        break label;
                    }
                }
                meta.println("No [" + args.get(0) + "] machine found");
                break;
            default:
                meta.println("Please enter a valid command");
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
