package com.example.affine.beaconscan;

import java.util.Arrays;
import java.util.List;

public class Strings {

    public static final boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        return str.trim().equals("") ? true : false;
    }

    public static final boolean notEmpty(String str) {
        return !isEmpty(str);
    }

    public static final boolean notEmpty(List<String> strs) {
        for (String str : strs) {
            if (isEmpty(str)) {
                return false;
            }
        }
        return true;
    }


    public static String join(List<String> strings, String join) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.size(); i++) {
            if (i != 0) {
                sb.append(join);
            }
            sb.append(strings.get(i));
        }
        return sb.toString();
    }

    public static String join(String join, String... strings) {
        return join(Arrays.asList(strings), join);
    }

    public static String convertJson(String json) {
        String result = json;
        result = result.replace("{", "(");
        result = result.replace("}", ")");
        result = result.replace("[", "<");
        result = result.replace("]", ">");
        result = result.replace("\"", "-");
        result = result.replace(":", "^");
        result = result.replace(",", "|");
        return result;
    }

    public static String reverseConvertJson(String json) {
        String result = json;
        result = result.replace("(", "{");
        result = result.replace(")", "}");
        result = result.replace("<", "[");
        result = result.replace(">", "]");
        result = result.replace("-", "\"");
        result = result.replace("^", ":");
        result = result.replace("|", ",");
        return result;
    }

    public static boolean areEqual(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equals(str2);
    }

    public static String upCaseFirstLetter(String string) {
        String trim = string.trim();
        if (trim.length() == 0) {
            return trim;
        } else if (trim.length() == 1) {
            return trim.substring(0, 1).toUpperCase();
        } else {
            return trim.substring(0, 1).toUpperCase() + trim.substring(1);
        }
    }

    public static String lowerCaseFirstLetter(String string) {
        String trim = string.trim();
        if (trim.length() == 0) {
            return trim;
        } else if (trim.length() == 1) {
            return trim.substring(0, 1).toLowerCase();
        } else {
            return trim.substring(0, 1).toLowerCase() + trim.substring(1);
        }
    }

    public static String toTitleCase(String givenString) {
        if (Strings.isEmpty(givenString)) {
            return "";
        } else {
            String[] arr = givenString.split(" ");
            StringBuffer sb = new StringBuffer();
            for (String value : arr) {
                if (value.length() > 0) {
                    sb.append(Character.toUpperCase(value.charAt(0)))
                            .append(value.substring(1).toLowerCase()).append(" ");
                }
            }
            return sb.toString().trim();
        }
    }

    public static String removeSurroundingQuotes(String string) {

        int first = string.indexOf('\"');
        if (first >= 0) {
            int second = string.indexOf('\"', first + 1);

            String stringWithoutQuotes = string.substring(first + 1, second);
            return stringWithoutQuotes;
        }
        return string;
    }

    public static String joinBySeperatorIfNotEmpty(String join, List<String> strings) {
        StringBuilder sb = new StringBuilder();
        boolean firstEntryEncountered = false;
        for (int i = 0; i < strings.size(); i++) {
            boolean currStringNotEmpty = Strings.notEmpty(strings.get(i));
            if (firstEntryEncountered && currStringNotEmpty) {
                sb.append(join);
            } else if (currStringNotEmpty) {
                firstEntryEncountered = true;
            }
            sb.append(strings.get(i));
        }
        return sb.toString();
    }

    public static String joinBySeperatorIfNotEmpty(String join, String... strings) {
        return joinBySeperatorIfNotEmpty(join, Lists.newArrayList(strings));
    }

    public static List<String> getLowerCaseList(List<String> list) {
        List<String> listLowerCase = Lists.newArrayList();
        for (String s : list) {
            listLowerCase.add(s.toLowerCase());
        }
        return listLowerCase;
    }

    public static List<String> getUpperCaseList(List<String> list) {
        List<String> listLowerCase = Lists.newArrayList();
        for (String s : list) {
            listLowerCase.add(s.toUpperCase());
        }
        return listLowerCase;
    }
}
