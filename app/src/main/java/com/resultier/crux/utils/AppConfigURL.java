package com.resultier.crux.utils;

public class AppConfigURL {
    public static String version = "v1.0";
    
    private static String BASE_URL = "https://factory-app-cammy92.c9users.io/api_crux/" + version + "/";
    //private static String BASE_URL = "http://actipatient.com/crux/api/" + version + "/";
    
    public static String LOGIN = BASE_URL + "app/login";
    public static String GET_POLLS = BASE_URL + "app/surveys";
    public static String GET_SURVEY_DETAIL = BASE_URL + "app/survey/questions";
    public static String SUBMIT_RESPONSES = BASE_URL + "app/survey/responses";
    public static String FORGOT_PASSWORD = BASE_URL + "app/forgot-password";
}

