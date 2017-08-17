package com.wag.project.model;

import java.util.ArrayList;

/**
 * Model Dto associated with the StackOverflowResponseDto data returned by the StackOverflow API
 */

public class StackOverflowResponseDto
{
    private ArrayList<ItemDto> items;

    public ArrayList<ItemDto> getItems() {
        return this.items;
    }



}
