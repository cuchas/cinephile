package com.arctouch.codechallenge.home;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.arctouch.codechallenge.api.TmdbApi;
import com.arctouch.codechallenge.api.TmdbApiSettings;
import com.arctouch.codechallenge.data.Cache;
import com.arctouch.codechallenge.model.Genre;
import com.arctouch.codechallenge.model.GenreResponse;
import com.arctouch.codechallenge.model.Movie;
import com.arctouch.codechallenge.model.UpcomingMoviesResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Response;

/**
 * Created by eduardocucharro on 25/03/18.
 */

public class HomeViewModel extends ViewModel {

    private final Executor executor;
    private final TmdbApi api;
    private final Cache cache;
    private MutableLiveData<List<Movie>> movieList = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<Throwable> errorState = new MutableLiveData<>();

    public MutableLiveData<List<Movie>> getMovieList() {
        return movieList;
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }

    public MutableLiveData<Throwable> getErrorState() {
        return errorState;
    }

    public HomeViewModel() {
        this.executor = Executors.newSingleThreadExecutor();

        api = TmdbApi.newInstance();

        cache = Cache.newInstance();
    }

    public HomeViewModel(Executor executor, TmdbApi api, Cache cache) {
        this.executor = executor;
        this.api = api;
        this.cache = cache;
    }

    public void listMovies() {

        List<Movie> movieList = this.movieList.getValue();

        //uses cached values
        if(movieList != null && movieList.size() > 0)
            return;

        loading.setValue(true);

        executor.execute(() -> {
            try {

                if(cache.getGenres().size() == 0) {
                    Response<GenreResponse> response =
                            api.genres(TmdbApiSettings.API_KEY, TmdbApiSettings.DEFAULT_LANGUAGE).execute();

                    List<Genre> genres = response.body().genres;

                    cache.setGenres(genres);
                }

                Response<UpcomingMoviesResponse> response =
                        api.upcomingMovies(TmdbApiSettings.API_KEY, TmdbApiSettings.DEFAULT_LANGUAGE, 1L, TmdbApiSettings.DEFAULT_REGION).execute();

                for (Movie movie : response.body().results) {
                    movie.genres = new ArrayList<>();

                    for (Genre genre : cache.getGenres()) {
                        if (movie.genreIds.contains(genre.id)) {
                            movie.genres.add(genre);
                        }
                    }
                }

                this.movieList.postValue(response.body().results);
                loading.postValue(false);

            } catch (Exception e) {
                errorState.postValue(e);
                loading.postValue(false);
            }
        });
    }
}
