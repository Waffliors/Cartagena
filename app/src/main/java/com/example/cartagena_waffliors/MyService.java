package com.example.cartagena_waffliors;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MyService {

    @GET("rest/v1/jogador/{idPartida}")
    public Call<Jogador[]> pegarListaJogadores(@Path("idPartida") String idPartida);
}
