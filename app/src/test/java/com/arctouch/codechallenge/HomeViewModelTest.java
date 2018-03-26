package com.arctouch.codechallenge;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.arctouch.codechallenge.api.TmdbApi;
import com.arctouch.codechallenge.api.TmdbApiSettings;
import com.arctouch.codechallenge.data.Cache;
import com.arctouch.codechallenge.home.HomeViewModel;
import com.arctouch.codechallenge.model.Genre;
import com.arctouch.codechallenge.model.Movie;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by eduardocucharro on 25/03/18.
 */

@RunWith(JUnit4.class)
public class HomeViewModelTest {

    @Rule
    public TestRule testRule = new InstantTaskExecutorRule();

    @Mock
    TmdbApi api;

    @Mock
    Cache cache;

    HomeViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        viewModel = new HomeViewModel(Runnable::run, api, cache);
    }

    @Test
    public void listMovies_should_useCache_when_exists() {
        ArrayList<Movie> movieList = new ArrayList<>();
        movieList.add(new Movie());

        viewModel.getMovieList().setValue(movieList);
        viewModel.listMovies();

        verifyZeroInteractions(api);
    }

    @Test
    public void listMovies_should_getFromApi_when_noCache() {
        when(cache.getGenres()).thenReturn(new ArrayList<>());

        FakeGenreResponseSuccess genreResponseSuccess = new FakeGenreResponseSuccess(new ArrayList<>());
        when(api.genres(TmdbApiSettings.API_KEY, TmdbApiSettings.DEFAULT_LANGUAGE)).thenReturn(genreResponseSuccess);

        when(api.upcomingMovies(TmdbApiSettings.API_KEY, TmdbApiSettings.DEFAULT_LANGUAGE, 1L, TmdbApiSettings.DEFAULT_REGION)).thenReturn(new FakeUpcomingMoviesResponseSucess());

        viewModel.listMovies();

        verify(api).genres(TmdbApiSettings.API_KEY, TmdbApiSettings.DEFAULT_LANGUAGE);
        verify(api).upcomingMovies(TmdbApiSettings.API_KEY, TmdbApiSettings.DEFAULT_LANGUAGE, 1L, TmdbApiSettings.DEFAULT_REGION);
    }

    @Test
    public void listMovies_should_addToCache_when_getGenresSucess() {
        when(cache.getGenres()).thenReturn(new ArrayList<>());

        List<Genre> genreList = new ArrayList<>();
        FakeGenreResponseSuccess genreResponseSuccess = new FakeGenreResponseSuccess(genreList);
        when(api.genres(TmdbApiSettings.API_KEY, TmdbApiSettings.DEFAULT_LANGUAGE)).thenReturn(genreResponseSuccess);

        when(api.upcomingMovies(TmdbApiSettings.API_KEY, TmdbApiSettings.DEFAULT_LANGUAGE, 1L, TmdbApiSettings.DEFAULT_REGION)).thenReturn(new FakeUpcomingMoviesResponseSucess());

        viewModel.listMovies();

        verify(cache).setGenres(genreList);

    }
}
