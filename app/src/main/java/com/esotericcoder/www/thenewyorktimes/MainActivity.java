package com.esotericcoder.www.thenewyorktimes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.esotericcoder.www.thenewyorktimes.article_view_holder.ArticleAdapter;
import com.esotericcoder.www.thenewyorktimes.data.ApiClient;
import com.esotericcoder.www.thenewyorktimes.data.ArticleResponse;
import com.esotericcoder.www.thenewyorktimes.data.NewsfeedInterface;
import com.esotericcoder.www.thenewyorktimes.fragment.FilterSettingFragment;
import com.esotericcoder.www.thenewyorktimes.model.Article;
import com.esotericcoder.www.thenewyorktimes.model.FilterSetting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements FilterSettingFragment.Listener{

    private final String TAG = "MainActivity";
    private final String FRAGMENT = "FilterSettingFragment";
    List<String> flArray = Arrays.asList("web_url",
            "snippet", "headline", "multimedia", "news_desk");
    List<Article> articles;
    ArticleAdapter articleAdapter;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;
    private String searchQuery;
    private FilterSetting filterSetting;
    private int articlePage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        articleAdapter = new ArticleAdapter(this);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, 1);
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(articleAdapter);
        recyclerView.setHasFixedSize(true);

        scrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);

        filterSetting = new FilterSetting(20170701,
                "oldest",
                "news_desk:(\"Arts\" \"Fashion & Style\" \"Sports\")");

        makeApiCall();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                searchQuery = query.replace(' ', '+');
                articlePage = 1;
                articleAdapter.clearData();

                makeApiCall();


                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void loadNextDataFromApi(int offset) {
        makeApiCall();
    }

    public void makeApiCall(){
        if(isNetworkAvailable()){
            if(isOnline()){
                NewsfeedInterface apiService =
                        ApiClient.getClient().create(NewsfeedInterface.class);

                Call<ArticleResponse> call = apiService.listArticles(BuildConfig.API_KEY,
                        filterSetting.getBeginDate(),
                        filterSetting.getSort(),
                        searchQuery,
                        articlePage,
                        filterSetting.getNewsDesk(),
                        flArray);
                call.enqueue(new Callback<ArticleResponse>() {
                    @Override
                    public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                        if(response.code() == 200){
                            articles = response.body().getResults();
                            articlePage++;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    articleAdapter.addData(articles);
                                }
                            });
                        } else {
                            Log.d(TAG, "onResponse: " + "Failed");
                            Toast.makeText(MainActivity.this, "Response code: " + response.code(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArticleResponse>call, Throwable t) {
                        // Log error here since request failed
                        Log.e(TAG, t.toString());
                        Toast.makeText(MainActivity.this, "Call Failed: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }else{
                Toast.makeText(MainActivity.this, "No internet connection.",
                        Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(MainActivity.this, "Network not available.",
                    Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onFilterSettingReceived(FilterSetting filterSetting){
        this.filterSetting = filterSetting;
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }


    public void showFilterSettingDialog(MenuItem menuItem){
        FilterSettingFragment fragment = new FilterSettingFragment();
        fragment.setListener(MainActivity.this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, FRAGMENT);
        transaction.commit();

    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }
}
