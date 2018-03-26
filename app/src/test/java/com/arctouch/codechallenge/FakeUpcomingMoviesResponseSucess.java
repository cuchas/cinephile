package com.arctouch.codechallenge;

import com.arctouch.codechallenge.model.UpcomingMoviesResponse;

import java.io.IOException;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.HTTP;

/**
 * Created by eduardocucharro on 25/03/18.
 */

public class FakeUpcomingMoviesResponseSucess implements Call<UpcomingMoviesResponse> {
    @Override
    public Response<UpcomingMoviesResponse> execute() throws IOException {
        return Response.success(new UpcomingMoviesResponse());
    }

    @Override
    public void enqueue(Callback<UpcomingMoviesResponse> callback) {

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
    public Call<UpcomingMoviesResponse> clone() {
        return null;
    }

    @Override
    public Request request() {
        return null;
    }
}
