package com.morticia.compsim.Machine.Filesystem;

import com.morticia.compsim.Machine.User.User;
import com.morticia.compsim.Machine.User.UserGroup;

/**
 * A class meant to hold data mimicking the linux system of file permissions
 *
 * @author Morticia
 * @version 1.0
 * @since 7/7/22
 */

public class FilePerms {
    public User owner;
    public UserGroup group;

    // This is a really ugly way to do this but it's easier to work with outside this class and faster
    // to reference so whatever, sorry @ anyone who has to read this lmao
    public boolean ownerExecute;
    public boolean ownerRead;
    public boolean ownerWrite;

    public boolean groupExecute;
    public boolean groupRead;
    public boolean groupWrite;

    public boolean otherExecute;
    public boolean otherRead;
    public boolean otherWrite;

    public FilePerms(User owner) {
        this.owner = owner;
        this.group = owner.group;

        ownerExecute = false;
        ownerRead = true;
        ownerWrite = true;

        groupExecute = false;
        groupRead = true;
        groupWrite = false;

        otherExecute = false;
        otherRead = true;
        otherWrite = false;
    }

    public void initPerms(String perms) {
        char[] c = perms.toCharArray();

        ownerExecute = c[0] == 'x';
        ownerRead = c[1] == 'r';
        ownerWrite = c[2] == 'w';

        groupExecute = c[3] == 'x';
        groupRead = c[4] == 'r';
        groupWrite = c[5] == 'w';

        otherExecute = c[6] == 'x';
        otherRead = c[7] == 'r';
        otherWrite = c[8] == 'w';
    }

    public String getPerms() {
        String str = "---------";
        char[] c = str.toCharArray();
        if (ownerExecute) {
            c[0] = 'x';
        }
        if (ownerRead) {
            c[1] = 'r';
        }
        if (ownerWrite) {
            c[2] = 'w';
        }

        if (groupExecute) {
            c[3] = 'x';
        }
        if (groupRead) {
            c[4] = 'r';
        }
        if (groupWrite) {
            c[5] = 'w';
        }

        if (otherExecute) {
            c[6] = 'x';
        }
        if (otherRead) {
            c[7] = 'r';
        }
        if (otherWrite) {
            c[8] = 'w';
        }

        StringBuilder sb = new StringBuilder();
        for (char i : c) {
            sb.append(i);
        }
        return sb.toString();
    }

    public void or(FilePerms perms) {
        this.ownerExecute |= perms.ownerExecute;
        this.ownerRead |= perms.ownerRead;
        this.ownerWrite |= perms.ownerWrite;

        this.groupExecute |= perms.groupExecute;
        this.groupRead |= perms.groupRead;
        this.groupWrite |= perms.groupWrite;

        this.otherExecute |= perms.otherExecute;
        this.otherRead |= perms.otherRead;
        this.otherWrite |= perms.otherWrite;
    }
}
