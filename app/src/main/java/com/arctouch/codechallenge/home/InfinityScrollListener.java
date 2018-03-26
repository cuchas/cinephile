package com.arctouch.codechallenge.home;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by eduardocucharro on 26/03/18.
 */

public class InfinityScrollListener extends RecyclerView.OnScrollListener {

    private static final int CUTLINE_SIZE = 3;
    private final HomeViewModel viewModel;

    public InfinityScrollListener(HomeViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int currentTotalItems = recyclerView.getLayoutManager().getItemCount();

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

        if (lastVisibleItemPosition >= (currentTotalItems - CUTLINE_SIZE)) {
            viewModel.moreMovies();
        }
    }
}
