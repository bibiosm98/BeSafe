package com.example.besafe;

public class GlobalToken {

    private static String TOKEN = "40e3d3c39e2c1e24b1af5144d0388222d2e87e9c55a9c898";

    public static String getTOKEN() {
        return TOKEN;
    }

    public static void setTOKEN(String TOKEN) {
        GlobalToken.TOKEN = TOKEN;
    }

}