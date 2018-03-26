package com.arctouch.codechallenge.home;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arctouch.codechallenge.R;
import com.arctouch.codechallenge.details.DetailsActivity;
import com.arctouch.codechallenge.model.Movie;

public class HomeActivity extends AppCompatActivity implements HomeAdapter.ItemClickListener, SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private HomeAdapter adapter;
    private HomeViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        showToolbar();

        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        viewModel.getMovieList().observe(this, movies -> adapter.setMovies(movies));
        viewModel.getLoading().observe(this, loading -> progressBar.setVisibility(loading ? View.VISIBLE :  View.GONE));
        viewModel.getErrorState().observe(this, throwable -> Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show());

        this.recyclerView = findViewById(R.id.recyclerView);
        this.progressBar = findViewById(R.id.progressBar);

        adapter = new HomeAdapter();
        adapter.setItemListener(HomeActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new InfinityScrollListener(viewModel));

        viewModel.listMovies();
    }

    private void showToolbar() {
        setTitle(getString(R.string.app_name));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = new SearchView(this);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
        searchView.setOnQueryTextListener(this);

        if(!TextUtils.isEmpty(viewModel.getQuery())) {
            searchView.setQuery(viewModel.getQuery(), false);
            searchView.setQueryHint(getString(R.string.type_movie_name));
        }

        MenuItemCompat.setShowAsAction(menuItem, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        MenuItemCompat.setActionView(menuItem, searchView);

        return true;
    }

    @Override
    public void onItemClick(Movie movie) {
        DetailsActivity.startActivity(getApplicationContext(), movie);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        viewModel.search(query);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(TextUtils.isEmpty(newText)) {
            viewModel.search(newText);
        }
        return true;
    }
}
