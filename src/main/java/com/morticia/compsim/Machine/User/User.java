package com.morticia.compsim.Machine.User;

import com.morticia.compsim.Machine.Filesystem.ExecutionPermissions;
import com.morticia.compsim.Machine.Filesystem.VirtualFolder;
import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Util.Constants;
import com.morticia.compsim.Util.Disk.DataHandler.Serializable;

/**
 * Class meant to make security/userspace more doable for machines
 *
 * @author Morticia
 * @version 1.0
 * @since 7/6/22
 */

public class User implements Serializable {
    public Machine machine;
    public UserGroup group;
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
        this.group = new UserGroup(machine, userName);
        handler.addGroup(group);
        if (machine.filesystem != null) {
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
        }
        machine.logHandler.log("User [" + userName + "] initialized");
    }

    @Override
    public String getType() {
        return Constants.user_type;
    }

    @Override
    public String getDesig() {
        return userName;
    }

    @Override
    public String serialize() {
        String var = prepParams(new String[][]{
                {"user_name", userName},
                {"password", password},
                {"group", group.getDesig()},
        });
        return getPrefix() + var;;
    }

    @Override
    public void parse(String txt) {
        // TODO: 7/7/22 Handle groups and stuff, if it doesn't already exist make it etc. 
    }
}
