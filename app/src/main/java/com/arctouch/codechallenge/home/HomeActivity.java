package com.arctouch.codechallenge.home;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arctouch.codechallenge.R;
import com.arctouch.codechallenge.details.DetailsActivity;
import com.arctouch.codechallenge.model.Movie;

public class HomeActivity extends AppCompatActivity implements HomeAdapter.ItemClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private HomeAdapter adapter;
    private HomeViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        this.recyclerView = findViewById(R.id.recyclerView);
        this.progressBar = findViewById(R.id.progressBar);

        adapter = new HomeAdapter();
        adapter.setItemListener(HomeActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int itensOnAdapter = recyclerView.getLayoutManager().getItemCount();

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (firstVisibleItemPosition >= (itensOnAdapter - 10)) {
                    viewModel.moreMovies();
                }
            }
        });

        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        viewModel.getMovieList().observe(this, movies -> adapter.setMovies(movies));
        viewModel.getLoading().observe(this, loading -> progressBar.setVisibility(loading ? View.VISIBLE :  View.GONE));
        viewModel.getErrorState().observe(this, throwable -> Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show());
        viewModel.listMovies();
    }

    @Override
    public void onItemClick(Movie movie) {
        DetailsActivity.startActivity(getApplicationContext(), movie);
    }
}
