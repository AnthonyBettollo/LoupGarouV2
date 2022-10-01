package loupgarou.classes.utils;

import java.util.Comparator;

import loupgarou.classes.roles.LGRole;

public class SortLGRolesByNightOrder implements Comparator<LGRole> {
    public int compare(LGRole a, LGRole b)
    {
        return a.getNightOrder() < b.getNightOrder() ? 1 : -1;
    }
}
