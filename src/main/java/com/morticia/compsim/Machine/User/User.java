package com.morticia.compsim.Machine.User;

import com.morticia.compsim.Machine.Filesystem.ExecutionPermissions;
import com.morticia.compsim.Machine.Filesystem.VirtualFolder;
import com.morticia.compsim.Machine.Machine;

/**
 * Class meant to make security/userspace more doable for machines
 *
 * @author Morticia
 * @version 1.0
 * @since 7/6/22
 */

public class User {
    public Machine machine;
    public String userName;
    public String password;
    public ExecutionPermissions execPerms;
    public VirtualFolder homeFolder;

    /**
     * Constructor
     *
     * @param handler The handler attached to this user (machine var is set from this)
     * @param userName The name of this user
     * @param password Password needed to assume this user
     * @param execPerms Permissions used when this user executes scripts
     */
    public User(UserHandler handler, String userName, String password, ExecutionPermissions execPerms) {
        this.machine = handler.machine;
        this.userName = userName;
        this.password = password;
        this.execPerms = execPerms;
        if (userName.equals("root") && handler.root == null) {
            this.homeFolder = machine.filesystem.getfolder("/root");
            if (homeFolder == null) {
                this.homeFolder = new VirtualFolder(machine.filesystem, machine.filesystem.root, "root");
                //machine.filesystem.getFolder("/home").addFolder(homeFolder);
            }
        } else {
            this.homeFolder = machine.filesystem.getfolder("/home/" + userName);
            if (homeFolder == null) {
                this.homeFolder = new VirtualFolder(machine.filesystem, machine.filesystem.getfolder("/home"), userName);
                //machine.filesystem.getfolder("/home").addFolder(homeFolder);
            }
        }
        machine.logHandler.log("User [" + userName + "] initialized");
    }
}
