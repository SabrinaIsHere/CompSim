package com.morticia.compsim.Machine.User;

import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Util.Constants;
import com.morticia.compsim.Util.Disk.DataHandler.Serializable;

import java.util.ArrayList;
import java.util.List;

/**
 * A data container making it easier to group users
 *
 * @author Morticia
 * @version 1.0
 * @since 7/7/22
 */

public class UserGroup implements Serializable {
    Machine machine;
    public String groupName;
    public List<User> users;

    public UserGroup(Machine machine, String name) {
        this.machine = machine;
        this.groupName = name;
        this.users = new ArrayList<>();
    }

    public User getUser(String name) {
        for (User i : users) {
            if (i.userName.equals(name)) {
                return i;
            }
        }
        return null;
    }

    @Override
    public String getType() {
        return Constants.user_group_type;
    }

    @Override
    public String getDesig() {
        return groupName;
    }

    @Override
    public String serialize() {
        String var = prepParams(new String[][] {
                {"group_name", groupName},
                {"group_members", users.toString()}
        });
        return var;
    }

    @Override
    public void parse(String txt) {
        List<String[]> str_1 = extractParams(txt);
        for (String[] i : str_1) {
            if (i[0].equals("n/a")) {
                continue;
            } else if (i[0].equals("group_name")) {
                groupName = i[1];
            } else if (i[0].equals("group_members")) {
                String[] members = getListMembers(i[1]);
                for (String j : members) {
                    User u = machine.userHandler.getUser(j);
                    if (u != null) {
                        users.add(u);
                    }
                }
            }
        }
    }
}
