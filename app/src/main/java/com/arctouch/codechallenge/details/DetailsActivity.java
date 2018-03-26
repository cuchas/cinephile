package com.arctouch.codechallenge.details;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.arctouch.codechallenge.R;
import com.arctouch.codechallenge.model.Movie;
import com.arctouch.codechallenge.util.MovieImageUrlBuilder;
import com.bumptech.glide.Glide;

public class DetailsActivity extends AppCompatActivity {

    private static final String MOVIE = "MOVIE";
    private Movie movie;
    MovieImageUrlBuilder movieImageUrlBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        init(savedInstanceState != null ? savedInstanceState : getIntent().getExtras());

        showUpNavigationIcon();

        showMovieDetails();
    }

    private void showUpNavigationIcon() {
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMovieDetails() {
        if(movie == null)
            return;

        TextView nameText = findViewById(R.id.nameTextView);
        nameText.setText(movie.title);

        TextView releaseText = findViewById(R.id.releaseDateTextView);
        releaseText.setText(movie.releaseDate);

        TextView overviewText = findViewById(R.id.overviewTextView);
        overviewText.setText(movie.overview);

        ImageView posterImage = findViewById(R.id.posterImageView);
        Glide.with(getApplicationContext())
                .load(movieImageUrlBuilder.buildPosterUrl(movie.posterPath))
                .into(posterImage);


        ImageView backdropImage = findViewById(R.id.backdropImageView);
        Glide.with(getApplicationContext())
                .load(movieImageUrlBuilder.buildBackdropUrl(movie.backdropPath))
                .into(backdropImage);

    }

    private void init(Bundle bundle) {
        movie = bundle.getParcelable(MOVIE);
        movieImageUrlBuilder = new MovieImageUrlBuilder();
    }

    public static void startActivity(Context context, Movie movie) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(MOVIE, movie);

        context.startActivity(intent);
    }
}
