package com.example.earthquakes;

public class Shared {
    public static final String HEADER = "earthquakes";

    public static final String USER_NAME = "USERNAME";
    public static final String NORTH_COORD = "NORTH";
    public static final String SOUTH_COORD = "SOUTH";
    public static final String EAST_COORD = "EAST";
    public static final String WEST_COORD = "WEST";
    public static final String MAGNITUDE_HIGHLIGHT = "MAGNITUDE";

    public static final String LONGITUDE = "LONGITUDE";
    public static final String LATITUDE = "LATITUDE";
    public static final String MAGNITUDE_VAL = "MAGNITUDE_VAL";

    public static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    public static Double strToDouble(String str) {
        try {
            //if str is valid double return double
            return Double.parseDouble(str);
        } catch(NumberFormatException e){
            //if str is invalid double return 0
            return 0.0;
        }
    }
}
