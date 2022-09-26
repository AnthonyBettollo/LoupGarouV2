package loupgarou.classes.utils;

import java.util.Comparator;

import loupgarou.classes.LGPlayer;

public class SortLGPlayerByVote implements Comparator<LGPlayer> {
    public int compare(LGPlayer a, LGPlayer b)
    {
        return a.getVote() < b.getVote() ? 1 : -1;
    }
}