package loupgarou.classes.roles;

import java.util.ArrayList;
import java.util.List;

public class LGRole {
    private String name;
    private int occurency;

    public LGRole(String name) {
        this.name = name;
        this.occurency = 0;
    }

    public LGRole(String name, int occurency) {
        this.name = name;
        this.occurency = occurency;
    }

    public static List<LGRole> InitRoles() {
        return new ArrayList<LGRole>() {
            {
                new LGRole("Villageois", 1);
                new LGRole("LoupGarou", 1);
            }
        };
    }

    public String Definition() {
        return String.format("%i : %s\n", this.occurency, this.name);
    }
}
