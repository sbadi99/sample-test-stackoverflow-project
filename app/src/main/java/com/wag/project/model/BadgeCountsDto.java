package com.wag.project.model;

/**
 * Model Dto associated with the BadgeCount data returned by the StackOverflow API
 */
public class BadgeCountsDto {

    private int gold;
    private int silver;

    public int getSilver() {
        return this.silver;
    }

    public int getGold() {
        return this.gold;
    }

}

