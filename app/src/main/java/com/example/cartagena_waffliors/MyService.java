package com.example.cartagena_waffliors;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MyService {

    @GET("rest/v1/jogador/{idPartida}")
    public Call<Jogador[]> pegarListaJogadores(@Path("idPartida") String idPartida);
    
    @GET("rest/v1/jogador/mao/{idJogador}/{senhaJogador}")
    public Call<Carta[]> pegaCartasJogador(@Path("idJogador") String idPartida, @Path("senhaJogador") String senhaJogador);

    @GET("rest/v1/jogo/tabuleiro/{idPartida}")
    public Call<Tile[]> pegaTiles(@Path("idPartida") String idPartida);
}
