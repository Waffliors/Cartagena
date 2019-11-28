package com.example.cartagena_waffliors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GameActivity extends AppCompatActivity {

    ViewGroup containerJogadores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        containerJogadores = (ViewGroup) findViewById(R.id.container_jogadores);


        System.out.println("Criou sala de jogo");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("jogo", 0);
        String idJogador = pref.getString("idJogador","");
        String nomeJogador = pref.getString("nomeJogador","");
        final String senhaJogador = pref.getString("senhaJogador","");
        String idJogo = pref.getString("idJogo","");
        String nomeJogo = pref.getString("nomeJogo","");
        String senhaJogo = pref.getString("senhaJogo","");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://kingme.azurewebsites.net/cartagena/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final MyService api = retrofit.create(MyService.class);
        Call<Jogador[]> chamadaListaJogadores = api.pegarListaJogadores(idJogo);
        chamadaListaJogadores.enqueue(new Callback<Jogador[]>() {
            @Override
            public void onResponse(Call<Jogador[]> call, Response<Jogador[]> response) {
                if(response.code() != 200){
                    Toast.makeText(GameActivity.this, "Deu Merda " + response.code(),
                                           Toast.LENGTH_LONG).show();
                }else{
                    Jogador[] retorno = response.body();
                    System.out.println("Lista de Jogadores: \n");
                    for(int i = 0; i < retorno.length; i++){
                        Call<Carta[]> chamadaListaCartas = api.pegaMao(retorno[i].getId(), retorno[i].getSenha());
                        addPlayerToFragment(retorno[i].getNome(), retorno[i].getId().toString());
                        System.out.println(api.pegaMao(retorno[i].getId(), retorno[i].getSenha()));
                        //retorno[i].setCartas();
                    }
                }
            }
            @Override
            public void onFailure(Call<Jogador[]> call, Throwable t) {
                Toast.makeText(GameActivity.this, "Deu Merda", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void addPlayerToFragment(String nomePlayer, String idPlayer){
        CardView cardView = (CardView) LayoutInflater.from(this)
                .inflate(R.layout.players_card, containerJogadores, false);

        System.out.println("Adicionando jogador " + nomePlayer + " ao container de jogadores");
        TextView nome = (TextView) cardView.findViewById(R.id.textView_nomeJogador_CardView);
        TextView id = (TextView) cardView.findViewById(R.id.textViewIDJogador_CardView);
        nome.setText("Nome do jogador: " + nomePlayer);
        id.setText("ID do jogador: " + idPlayer);
        containerJogadores.addView(cardView);
        System.out.println("Adicionou jogador na lista");
    }
}
