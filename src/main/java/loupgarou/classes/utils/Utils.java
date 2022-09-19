package loupgarou.classes.utils;

import java.util.ArrayList;

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
}
