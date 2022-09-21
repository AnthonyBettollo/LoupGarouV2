package loupgarou.classes.roles;

public class LGLoupGarou extends LGRole {
    public LGLoupGarou()
    {
        super();
        this.setCodeCouleur("§c§l");
        this.setName("Loup-Garou");
        this.setDescription(String.format("Tu gagnes avec les %sLoups-Garous§f. Tous les soir c'est festion avec les sanchz, enfin si vous arrivez à vous mettre d'accord...",this.getCodeCouleur()));
        this.setShortDescription(String.format("Tu gagnes avec les %sLoups-Garous",this.getCodeCouleur()));
    }

    @Override
    public String toString()
    {
        return this.getName();
    }
}
