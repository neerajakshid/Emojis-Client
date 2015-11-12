package com.example.neeraja.emojisclient;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


public class HomeActivity extends AppCompatActivity {
    public static final String BASE_URL = "https://api.github.com/emojis";
    private static int RESULT_SIZE = 8;
    private static int PAGE_ONE = 1;
    private GridView gvEmotions;
    private TextView tvSelectedEmotion;
    private ImageView ivSelectedEmotion;
    private ArrayList<EmojisResults> alResults;
    private ResultsAdapter adResults;
    private Toolbar mToolbar;
    private ProgressBar pbImage;
    private SearchView svQuery;
    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);

        // setup Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        HomeActivity.this.setSupportActionBar(mToolbar);

        pbImage = (ProgressBar) findViewById(R.id.pbImage);
        tvSelectedEmotion = (TextView) findViewById(R.id.tvSelectedEmotion);
        ivSelectedEmotion = (ImageView) findViewById(R.id.ivSelectedEmotion);
        gvEmotions = (GridView) findViewById(R.id.gvEmotions);
        alResults = new ArrayList<EmojisResults>();
        adResults = new ResultsAdapter(this, alResults);
        gvEmotions.setAdapter(adResults);

        fetchEmotions(PAGE_ONE);

        // Attach the listener to the AdapterView onCreate for Infinity scrolling
        gvEmotions.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                fetchEmotions(page);
                // or customLoadMoreDataFromApi(totalItemsCount);
            }
        });

        gvEmotions.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EmojisResults result = alResults.get(position);
                tvSelectedEmotion.setText(result.getText());
                ivSelectedEmotion.setImageResource(0);
                Picasso.with(HomeActivity.this).load(result.image).resize(75, 75).placeholder(R.mipmap.ic_launcher).into(ivSelectedEmotion);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_home, menu);
        searchItem = menu.findItem(R.id.action_search);
        svQuery = (SearchView) MenuItemCompat.getActionView(searchItem);
        svQuery.setQueryHint("Enter search term");
        svQuery.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                fetchSearchResults("%" + s + "%");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void fetchSearchResults (String query) {
        // soft keyboard
        InputMethodManager imm = (InputMethodManager) HomeActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(svQuery.getWindowToken(), 0);

        if (query != null && query != "") {
            alResults.clear();
            alResults = (ArrayList)EmojisResults.getItemSearchQuery(query);
            if (!alResults.isEmpty()) {
                adResults.clear();
                adResults.addAll(alResults);
                adResults.notifyDataSetChanged();
            }
        }
    }

    public void fetchEmotions(final int page) {
        if (NetworkManager.isNetworkAvailable(HomeActivity.this)) {
            if (page < PAGE_ONE || page > RESULT_SIZE) {
                return;
            }

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://api.github.com/emojis";
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // TODO Auto-generated method stub
                    Iterator<?> keys = response.keys();
                    // delete data from SQLite database
                    new Delete().from(EmojisResults.class).execute();
                    alResults.clear();
                    while( keys.hasNext() ) {
                        String key = (String)keys.next();
                        // load data to SQLite database from API
                        try {
                            EmojisResults results = new EmojisResults(key, response.getString(key));
                            results.save();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    alResults.clear();
                    alResults = (ArrayList) EmojisResults.loadEmojis();

                    // load the selected image with first icon from the list
                    EmojisResults result = alResults.get(0);
                    tvSelectedEmotion.setText(result.getText());
                    ivSelectedEmotion.setImageResource(0);
                    Picasso.with(HomeActivity.this).load(result.image).resize(75, 75).placeholder(R.mipmap.ic_launcher).into(ivSelectedEmotion);


                    if(!alResults.isEmpty()) {
                        adResults.clear();
                        adResults.addAll(alResults);

                    }
                    pbImage.setVisibility(View.GONE); // Hide progressbar
                    adResults.notifyDataSetChanged();




                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                    Log.e("onFailure", "onFailure ------     " + error);
                    pbImage.setVisibility(View.GONE); // Hide progressbar
                }
            });

            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(jsObjRequest);

        } else {
            NetworkManager.showAlertDialog(HomeActivity.this, getBaseContext().getString(R.string.network_error), false);
            Log.e("network", "network failure ");
            pbImage.setVisibility(View.GONE); // Hide progressbar

            // load persisting data from SQLite Database
            alResults.clear();
            alResults = (ArrayList) EmojisResults.loadEmojis();
            if(!alResults.isEmpty()) {
                adResults.clear();
                adResults.addAll(alResults);
            }
            adResults.notifyDataSetChanged();
            // load the selected image with first icon from the list
            EmojisResults result = alResults.get(0);
            tvSelectedEmotion.setText(result.getText());

            ivSelectedEmotion.setImageResource(0);
            Picasso.with(HomeActivity.this).load(result.image).resize(75, 75).placeholder(R.mipmap.ic_launcher).into(ivSelectedEmotion);

        }


    }
}
