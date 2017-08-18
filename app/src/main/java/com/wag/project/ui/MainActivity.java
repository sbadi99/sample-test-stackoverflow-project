package com.wag.project.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.wag.project.R;
import com.wag.project.api.StackOverflowAPIController;
import com.wag.project.model.ItemDto;
import com.wag.project.utils.PaginationListener;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Shakeel Badi
 *
 * Project uses StackOverflow API to dynamically retrieves StackOverflow user data including image avatars
 * Project uses Google GSON to process JSON response from StackOverflow API
 * Project uses Android recommended Glide Library to handle loading and caching of user avatar images
 * Project displays rounded images (instead of the default square images)
 * Project supports pagination (Limits it to first 2 pages for demo but is configurable)
 * Project supports swipe to load next page (Swipe down from top - Limits it to first 2 pages for demo but is configurable)
 * Project displays webview with user details on ReclyclerView click
 * Project also includes a custom launcher icon and animated Splash screen
 *
 */
public class MainActivity extends AppCompatActivity implements Observer {

    private static final String TAG = MainActivity.class.getName();

    private SwipeRefreshLayout swipeContainer;
    private StackOverflowAdapter adapter;
    private ProgressBar progressBar;
    private StackOverflowAPIController stackOverflowAPIController;
    private int currentPage = 1;
    private boolean isLoading   = false;

    //Only paginate pages < maxPages limit
    private final int  maxPages = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.main_recycler);
        progressBar = (ProgressBar)findViewById(R.id.main_progress);
        adapter = new StackOverflowAdapter(this);
        stackOverflowAPIController = new StackOverflowAPIController(this);
        swipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipeContainer);


        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                Log.d(TAG, "Swipe from top load next page: ");
                currentPage += 1;
                //loadNextPage
                if (currentPage < maxPages) {
                    stackOverflowAPIController.makeStackOverflowUserApiCall(currentPage);
                }
                swipeContainer.setRefreshing(false);
                swipeContainer.setEnabled(false);
            }
        });

        //Refreshing colors
        swipeContainer.setColorSchemeResources(
          android.R.color.holo_red_light,
          android.R.color.holo_green_light,
          android.R.color.holo_orange_light,
          android.R.color.holo_blue_light
        );

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new PaginationListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                final int delay = 1000;

                if (currentPage < maxPages) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //loadNextPage
                            stackOverflowAPIController.makeStackOverflowUserApiCall(currentPage);
                        }
                    }, delay);
                } else {
                    //no more pages to load
                    adapter.removeLoadingFooter();
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        //load first page
        stackOverflowAPIController.makeStackOverflowUserApiCall(currentPage);

    }

    @Override
    protected void onPause() {
        super.onPause();
        stackOverflowAPIController.deleteObserver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        stackOverflowAPIController.addObserver(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void update(Observable observable, Object data) {

        if(data instanceof VolleyError){
            //Handle Server error
            showServerError();
            Log.d(TAG, "API error: " + ((VolleyError)data).getMessage());
        }

        if (observable instanceof StackOverflowAPIController && currentPage == 1) {
            Log.d(TAG, "StackOverflow API response received first page: " + "nextPageToken:" + currentPage);

        } else if (observable instanceof StackOverflowAPIController) {
            Log.d(TAG, "StackOverflow API response received next page: " + "nextPageToken:" + currentPage);
            adapter.removeLoadingFooter();
            isLoading = false;
            adapter.clear();
        }
        updateUI((List<ItemDto>)data);
    }

    /**
     * Show Server error (StackOverflow API does have issue at times,
     * due to "throttling limit" if too much data is requested at once
     */
    private void showServerError() {
        Snackbar.make(findViewById(android.R.id.content),
                R.string.server_error, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Update UI/adapter after API response
     * @param data List<ItemDto> items
     */
    private void updateUI(List<ItemDto> data) {
        Log.d(TAG, "loadNextPage: " + currentPage);
        progressBar.setVisibility(View.GONE);
        adapter.addAll(data);
        adapter.addLoadingFooter();

    }
}
