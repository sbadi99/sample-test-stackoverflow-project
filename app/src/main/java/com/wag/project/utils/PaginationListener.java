package com.wag.project.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * This class handles Pagination logic as the user scrolls
 * Extends RecyclerView.OnScrollListener
 */
public abstract class PaginationListener extends RecyclerView.OnScrollListener {

    private final LinearLayoutManager layoutManager;

    public abstract boolean isLoading();

    protected abstract void loadMoreItems();

    /**
     * Constructor
     *
     * @param layoutManager The LinearLayoutManager passed
     */
    public PaginationListener(@NonNull final LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    /**
     * @param recyclerView The RecyclerView passed
     * @param x            The amount of horizontal scroll
     * @param y            The amount of vertical scroll
     */
    @Override
    public void onScrolled(RecyclerView recyclerView, int x, int y) {
        super.onScrolled(recyclerView, x, y);

        final int visibleItemCount = layoutManager.getChildCount();
        final int totalItemCount = layoutManager.getItemCount();
        final int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

        if (!isLoading()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0) {

                loadMoreItems();
            }
        }
    }
}
