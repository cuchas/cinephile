package com.arctouch.codechallenge;

import com.arctouch.codechallenge.model.Genre;
import com.arctouch.codechallenge.model.GenreResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by eduardocucharro on 25/03/18.
 */

public class FakeGenreResponseSuccess implements Call<GenreResponse> {

    private final List<Genre> genreList;

    public FakeGenreResponseSuccess(List<Genre> genreList) {
        this.genreList = genreList;
    }

    @Override
    public Response<GenreResponse> execute() throws IOException {
        GenreResponse body = new GenreResponse();
        body.genres = genreList;
        return Response.success(body);
    }

    @Override
    public void enqueue(Callback<GenreResponse> callback) {

    }

    @Override
    public boolean isExecuted() {
        return false;
    }

    @Override
    public void cancel() {

    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public Call<GenreResponse> clone() {
        return null;
    }

    @Override
    public Request request() {
        return null;
    }
}
