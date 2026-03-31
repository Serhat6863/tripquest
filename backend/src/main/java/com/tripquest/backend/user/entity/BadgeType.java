package com.tripquest.backend.user.entity;

public enum BadgeType {

    FIRST_POST("First Post", "Published your first post"),
    EXPLORER_5("Explorer 5", "Visited 5 countries"),
    EXPLORER_10("Explorer 10", "Visited 10 countries"),
    EXPLORER_20("Explorer 20", "Visited 20 countries"),
    SOCIAL_5("Social 5", "Made 5 friends"),
    SOCIAL_10("Social 10", "Made 10 friends"),
    CONTINENT_EUROPE("European Traveler", "Visited a country in Europe"),
    CONTINENT_ASIA("Asian Traveler", "Visited a country in Asia"),
    CONTINENT_AMERICA("American Traveler", "Visited a country in America"),
    CONTINENT_AFRICA("African Traveler", "Visited a country in Africa"),
    PHOTO_REPORTER("Photo Reporter", "Posted 5 travel posts"),
    CRITIC("Critic", "Rated 10 countries");

    private final String label;
    private final String description;

    BadgeType(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }
}
