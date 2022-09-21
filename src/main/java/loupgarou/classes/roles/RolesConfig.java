package loupgarou.classes.roles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Bukkit;

import lombok.Getter;
import lombok.Setter;

public class RolesConfig {
    @Getter
    private String name;
    @Getter @Setter
    private Integer count;
    @Getter
    @Setter
    private static List<RolesConfig> roles;

    public RolesConfig() {

    }

    public RolesConfig(String name, Integer count) {
        this.name = name;
        this.count = count;
    }

    public static List<String> GetDefaultConfig() {
        return new ArrayList<String>() {
            {
                add("Villageois|1");
                add("LoupGarou|1");
            }
        };
    }

    public static List<String> GetRolesNames()
    {
        List<String> rolesDefinitions = new ArrayList<String>();
        ListIterator<RolesConfig> roleIterator = RolesConfig.roles.listIterator();
        while (roleIterator.hasNext()) {
            rolesDefinitions.add(roleIterator.next().name);
        }
        return rolesDefinitions;
    }

    public static RolesConfig GetRoleByName(String role)
    {
        List<RolesConfig> configRoles = RolesConfig.roles;
        Iterator<RolesConfig> roleIterator = configRoles.iterator();
        RolesConfig roleByName = new RolesConfig();
        while(roleIterator.hasNext())
        {
            RolesConfig iterate = roleIterator.next();
            if(iterate.name.equals(role))
            {
                roleByName = iterate;
                break;
            }
        }
        return roleByName;
    }

    public static void UpdateRole(String roleName,Integer count){
        Integer index = 0;
        RolesConfig updateRole = null;
        for(RolesConfig role : RolesConfig.roles)
        {
            if(role.name.equals(roleName))
            {
                updateRole = role;
                index = RolesConfig.roles.indexOf(role);
                break;
            }
        }
        updateRole.setCount(count);
        RolesConfig.roles.set(index, updateRole);
    }

    public static List<RolesConfig> parseConfig(List<String> roles) {
        List<RolesConfig> parsedRoles = new ArrayList<RolesConfig>();
        Iterator<String> iterator = roles.iterator();
        while (iterator.hasNext()) {
            String role = iterator.next();
            String[] parsedRole = role.split("\\|");

            if (parsedRole.length == 2) {
                parsedRoles.add(new RolesConfig(parsedRole[0], Integer.parseInt(parsedRole[1])));
            } else {
                Bukkit.getLogger().info(role);
            }
        }
        return parsedRoles;
    }

    public static List<String> getConfig()
    {
        List<String> saveRoles = new ArrayList<String>();
        ListIterator<RolesConfig> roleIterator = RolesConfig.roles.listIterator();
        while (roleIterator.hasNext()) {
            saveRoles.add(roleIterator.next().toString());
        }
        return saveRoles;
    }

    public String Definition()
    {
        return String.format("%s:%s",this.count,this.name);
    }

    @Override
    public String toString() {
        return String.format("%s|%s",this.name,this.count);
    }
}
