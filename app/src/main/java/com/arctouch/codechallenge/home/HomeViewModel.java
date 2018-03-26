package com.arctouch.codechallenge.home;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;

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
    private long page = 0;
    private boolean noMoreMovies = false;
    private MutableLiveData<List<Movie>> movieList = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<Throwable> errorState = new MutableLiveData<>();
    private List<Movie> movies = new ArrayList<>();
    private List<Movie> searchMovieList = new ArrayList<>();
    private long searchPage = 0;
    private boolean noMoreSearch = false;
    private String query;

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

        //uses cached values
        if(movies.size() > 0)
            return;

        moreMovies();
    }

    public void moreMovies() {

        loading.setValue(true);

        if(!TextUtils.isEmpty(query)) {
            search(query);
            return;
        }

        executor.execute(() -> {
            try {

                fetchGenres();

                fetchUpcomingMovies();

            } catch (Exception e) {
                errorState.postValue(e);
                loading.postValue(false);

                fixPaging();
            }
        });
    }

    private void fetchUpcomingMovies() throws java.io.IOException {

        if(noMoreMovies) {
            loading.postValue(false);
            return;
        }

        page++;

        Response<UpcomingMoviesResponse> response = api.upcomingMovies(TmdbApiSettings.API_KEY, TmdbApiSettings.DEFAULT_LANGUAGE, page, TmdbApiSettings.DEFAULT_REGION).execute();

        UpcomingMoviesResponse body = response.body();

        if(body == null)
            return;

        if(body.results.size() == 0) {
            fixPaging();
            loading.postValue(false);
            return;
        }

        for (Movie movie : body.results) {
            movie.genres = new ArrayList<>();

            for (Genre genre : cache.getGenres()) {
                if (movie.genreIds.contains(genre.id)) {
                    movie.genres.add(genre);
                }
            }
        }
        movies.addAll(body.results);
        movieList.postValue(movies);
        loading.postValue(false);

        if(page >= body.totalPages)
            noMoreMovies = true;
    }

    private void fetchGenres() throws java.io.IOException {
        if(cache.getGenres().size() == 0) {
            Response<GenreResponse> response = api.genres(TmdbApiSettings.API_KEY, TmdbApiSettings.DEFAULT_LANGUAGE).execute();

            GenreResponse body = response.body();

            if(body == null) {
                return;
            }

            List<Genre> genres = body.genres;

            cache.setGenres(genres);
        }
    }

    private void fixPaging() {
        if(page > 1)
            page--;
    }

    public void search(String query) {

        if(TextUtils.isEmpty(query)) {
            searchPage = 0;
            searchMovieList.clear();
            this.query = query;
            movieList.setValue(movies);
            return;
        }

        this.query = query;

        executor.execute(() -> {
            try {
                if(noMoreSearch) {
                    loading.postValue(false);
                    return;
                }

                searchPage++;

                Response<UpcomingMoviesResponse> response = api.search(TmdbApiSettings.API_KEY, query, searchPage).execute();;

                UpcomingMoviesResponse body = response.body();

                if(body == null)
                    return;

                if(body.results.size() == 0) {
                    searchPage--;
                    loading.postValue(false);
                    return;
                }

                for (Movie movie : body.results) {
                    movie.genres = new ArrayList<>();

                    for (Genre genre : cache.getGenres()) {
                        if (movie.genreIds.contains(genre.id)) {
                            movie.genres.add(genre);
                        }
                    }
                }
                searchMovieList.addAll(body.results);
                movieList.postValue(searchMovieList);
                loading.postValue(false);

                if(searchPage >= body.totalPages)
                    noMoreSearch = true;

            } catch (Exception e) {
                errorState.postValue(e);
                loading.postValue(false);

                searchPage--;
            }
        });
    }

    public String getQuery() {
        return query;
    }
}
