package com.wag.project.api;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.wag.project.R;
import com.wag.project.model.ItemDto;
import com.wag.project.model.StackOverflowResponseDto;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Observable;

/**
 * StackOverflowAPIController class handles calls to StackOverflow API Utilizing the Google Volley networking library
 * Volley natively makes networking calls on background thread (to keep UI responsive)
 * On successful StackOverflow API response notifies the UI (using Observable)
 */
public class StackOverflowAPIController extends Observable {

    private static final String TAG = StackOverflowAPIController.class.getName();
    private static final String EMPTY_STRING = "";

    private final RequestQueue stackOverflowQueue;
    private final Activity activity;
    private StackOverflowResponseDto stackOverflowResponseDto;
    private List<ItemDto> stackOverflowItems;

    /**
     * Constructor Initializes the Volley Networking Library instance as well
     * @param activity the activity context passed
     */
    public StackOverflowAPIController(@NonNull final Activity activity) {
        this.activity = activity;
        stackOverflowQueue = Volley.newRequestQueue(activity);
    }

    /**
     * This method makes the StackOverflow API call using Google Volley Networking Library
     * @param page The next page returned by the StackOverflow API
     */
    public void makeStackOverflowUserApiCall(int page) {

        final String url = getUrlParameters(String.valueOf(page)).toString();
        StringRequest StackOverflowApiRequest =
          new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                  Log.d(TAG, response);
                  Gson gson = new Gson();
                  stackOverflowResponseDto = gson.fromJson(response, StackOverflowResponseDto.class);
                  stackOverflowItems = fetchResults(stackOverflowResponseDto);

                  //notify the UI
                  setChanged();
                  notifyObservers(stackOverflowItems);
              }

          }, new com.android.volley.Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                  if (error != null) {
                      Log.e(TAG, error.getMessage());

                      //notify the UI of error
                      setChanged();
                      notifyObservers(error);
                  }
              }
          }) {
          };

        //Add request to the Volley queue
        stackOverflowQueue.add(StackOverflowApiRequest);
    }

    /**
     * @param response extracts List<{@link StackOverflowResponseDto >} from response
     * @return List<ItemDto> of StackOverflow stackOverflowResponseDto item
     */
    private List<ItemDto> fetchResults(@NonNull final StackOverflowResponseDto response) {
        return response.getItems();
    }

    /**
     * The query parameters appended to the StackOverflow API user data request
     * @param nextPageToken The pageToken to retrieve the next set of results from the StackOverflow API
     * @return the URL with appended query parameters
     */
    private URL getUrlParameters(@NonNull final String nextPageToken) {

        final String firstPage          = "1";
        URL          url                = null;
        final String nextPageTokenParam = nextPageToken.equals(firstPage) ? EMPTY_STRING : nextPageToken;

        final Uri uri = new Uri.Builder()
          .scheme(activity.getString(R.string.scheme))
          .authority(activity.getString(R.string.host))
          .path(activity.getString(R.string.path))
          .appendQueryParameter("order", activity.getString(R.string.order))
          .appendQueryParameter("sort", activity.getString(R.string.sort))
          .appendQueryParameter("site", activity.getString(R.string.site))
          .appendQueryParameter("page", nextPageTokenParam)
          .build();
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException error) {
            Log.e(TAG, error.getMessage());
        }
        return url;
    }

}
