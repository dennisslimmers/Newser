package com.newsapp.newsapplication.config;

public class Config {
    private static final String API_KEY = "c7a7729e84894fc1b377ae4c3d27df55";
    private static final String API_URL = "https://newsapi.org/v2/top-headlines?sources=rtl-nieuws&apiKey=";
    private static final String APP_NAME = "Newser";
    private static final String APP_NAME_FONT = "fonts/cocogoose.ttf";

    private static final String APP_VERSION = "0.1";

    public static String getVersion() {
        return "v." + APP_VERSION;
    }

    public static String getAppName() {
        return APP_NAME;
    }

    public static String getAppNameFont() {
        return APP_NAME_FONT;
    }

    public static String getApiKey() {
        return API_KEY;
    }

    public static String getFullApiUrl() {
        return API_URL + API_KEY;
    }
}
