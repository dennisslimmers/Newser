package com.newsapp.newsapplication.controllers;

public class NewsApiController {
    private static NewsSource currentSource;

    public static void setCurrentNewsSource(NewsSource source) {
        currentSource = source;
    }

    public static NewsSource getCurrentSource() {
        return currentSource;
    }

    public enum NewsSource {
        RTL_NIEUWS,
        THE_VERGE,
        POLYGON,
        THE_GUARDIAN,
        CNN,
        NATIONAL_GEOGRAPHIC,
        TECH_RADAR,
        NEW_YORK_TIMES,
        TECH_CRUNCH
    }
}
