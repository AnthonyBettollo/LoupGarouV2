package loupgarou.classes.roles;

public class LGVillageois extends LGRole {
    public LGVillageois() {
        super();
        this.setCodeCouleur("§a§l");
        this.setName("Villageois");
        this.setDescription(String.format(
                "Tu gagnes avec le %sVillage§f. Tu sers à rien en vrai, juste essaie d'être maire histoire de pas te pendre pendant la nuit...",
                this.getCodeCouleur()));
        this.setShortDescription(String.format("Tu gagnes avec le %sVillage", this.getCodeCouleur()));
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
