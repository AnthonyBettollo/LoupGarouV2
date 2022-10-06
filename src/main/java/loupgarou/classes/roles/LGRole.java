package loupgarou.classes.roles;

import java.util.List;

import org.bukkit.event.Listener;

import lombok.Getter;
import lombok.Setter;
import loupgarou.classes.LGPlayer;

public abstract class LGRole implements Listener {
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String description;
    @Getter
    @Setter
    private String shortDescription;
    @Getter
    @Setter
    private String codeCouleur;
    @Setter
    private int occurency;
    @Getter @Setter
    private int nightOrder;
    
	public void onNightTurn(List<LGPlayer> player) {}

    public LGRole() {

    }

    public static LGRole ParseRole(String roleName) {
        LGRole role = null;
        switch (roleName) {
            case "Villageois":
                role = new LGVillageois();
                break;
            case "LoupGarou":
                role = new LGLoupGarou();
                break;
        }
        return role;
    }
}
