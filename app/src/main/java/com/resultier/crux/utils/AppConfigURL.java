package com.resultier.crux.utils;

public class AppConfigURL {
    public static String version = "v1.0";
    
    private static String BASE_URL = "https://project-ncrapp-cammy92.c9users.io/api/" + version + "/";
    
    public static String LOGIN = BASE_URL + "app/login";
    public static String GET_POLLS = "https://factory-app-cammy92.c9users.io/test3.php";
    public static String GET_SURVEY_DETAIL = "https://factory-app-cammy92.c9users.io/test2.php";//BASE_URL + "app/tickets/recent";
    public static String FORGOT_PASSWORD = BASE_URL + "app/forgot-password";
}

