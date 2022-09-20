package loupgarou.classes.roles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;

import lombok.Getter;
import lombok.Setter;

public class LGRole {
    private String name;
    @Setter
    private int occurency;
    @Getter @Setter
    private static List<LGRole> roles;

    public LGRole()
    {

    }

    public LGRole(String name) {
        this.name = name;
        this.occurency = 0;
    }

    public LGRole(String name, int occurency) {
        this.name = name;
        this.occurency = occurency;
    }

    public static List<String> InitRoles() {
        return new ArrayList<String>() {
            {
                add("Villageois|1");
                add("LoupGarou|1");
            }
        };
    };

    public static LGRole GetRoleByName(String role)
    {
        List<LGRole> configRoles = LGRole.roles;
        Iterator<LGRole> roleIterator = configRoles.iterator();
        LGRole roleByName = new LGRole();
        while(roleIterator.hasNext())
        {
            LGRole iterate = roleIterator.next();
            if(iterate.name.equals(role))
            {
                roleByName = iterate;
                break;
            }
        }
        return roleByName;
    }

    public static List<String> GetRolesNames() {
        return new ArrayList<String>() {
            {
                add("Villageois");
                add("LoupGarou");
            }
        };
    }

    public static List<LGRole> GlobalParse(List<String> roles) {
        List<LGRole> parsedRoles = new ArrayList<LGRole>();
        Iterator<String> iterator = roles.iterator();
        while (iterator.hasNext()) {
            String role = iterator.next();
            String[] parsedRole = role.split("\\|");
            if (parsedRole.length == 2) {
                parsedRoles.add(new LGRole(parsedRole[0], Integer.parseInt(parsedRole[1])));
            } else {
                Bukkit.getLogger().info(role);
            }
        }
        return parsedRoles;
    }

    public String Definition() {
        return String.format("%s : %s", this.occurency, this.name);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
