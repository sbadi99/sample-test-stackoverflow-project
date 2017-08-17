package com.wag.project.model;

import android.support.annotation.NonNull;

/**
 * Model Dto associated with the ItemDto data returned by the StackOverflow API
 */

public class ItemDto {

    private BadgeCountsDto badge_counts;
    private String         link;
    private String         profile_image;
    private String         display_name;

    @NonNull
    public BadgeCountsDto getBadgeCounts() {
        return this.badge_counts;
    }

    @NonNull
    public String getLink() {
        return this.link;
    }

    @NonNull
    public String getProfileImage() {
        return this.profile_image;
    }

    @NonNull
    public String getDisplayName() {
        return this.display_name;
    }

}
