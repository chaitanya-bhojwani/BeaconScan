package com.example.affine.beaconscan;

public class Formatter {

    public static String formatPermission(String data) {
        if (Strings.areEqual(data, Constants.PERMISSION_LOCATION_COURSE)) {
            return "Location permission required";
        }
        return "";
    }


}

