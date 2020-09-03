package com.example.listadefilmesapp.api;

import com.example.listadefilmesapp.model.Resultado;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DataService {

    @GET("v1")
    Call<Resultado> recuperarCapa(
            @Query("q") String q,
            @Query("key") String key,
            @Query("cx") String cx,
            @Query("num") String num
    );
}
