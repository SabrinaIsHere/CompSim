package com.morticia.compsim.Machine.User;

import com.morticia.compsim.Machine.Filesystem.ExecutionPermissions;
import com.morticia.compsim.Machine.Filesystem.VirtualFolder;
import com.morticia.compsim.Machine.Machine;

public class User {
    public Machine machine;
    public String userName;
    public String password;
    public ExecutionPermissions execPerms;
    public VirtualFolder homeFolder;

    public User(UserHandler handler, String userName, String password, ExecutionPermissions execPerms) {
        this.machine = handler.machine;
        this.userName = userName;
        this.password = password;
        this.execPerms = execPerms;
        if (userName.equals("root") && handler.root == null) {
            this.homeFolder = machine.filesystem.getfolder("/root");
            if (homeFolder == null) {
                this.homeFolder = new VirtualFolder(machine.filesystem, machine.filesystem.root, "root");
                //machine.filesystem.getfolder("/home").addFolder(homeFolder);
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
