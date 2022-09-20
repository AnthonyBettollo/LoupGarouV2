package loupgarou.classes.roles;

import org.bukkit.event.Listener;

import lombok.Getter;
import lombok.Setter;

public abstract class LGRole implements Listener {
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String description;
    private String codeCouleur;
    @Setter
    private int occurency;

    public LGRole()
    {

    }

    public static LGRole ParseRole(String roleName)
    {
        LGRole role = null;

        return role;
    }
}
