package loupgarou.classes.utils;

import java.util.ArrayList;

import org.bukkit.Location;

public class Utils {
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    public static String customJoin(char c, ArrayList<String> array) {
        String joinedString = "";
        for (String value : array) {
            joinedString += value + c;
        }
        return joinedString.substring(0,joinedString.length()-1);
    }

    public static double distanceSquaredXZ(Location from, Location to) {
		return Math.pow(from.getX()-to.getX(), 2)+Math.pow(from.getZ()-to.getZ(), 2);
	}
}
