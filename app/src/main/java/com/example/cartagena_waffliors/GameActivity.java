package com.example.cartagena_waffliors;

import retrofit2.Call;
import android.os.Bundle;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Callback;
import android.os.Handler;
import android.widget.Toast;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.content.SharedPreferences;
import androidx.cardview.widget.CardView;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.converter.gson.GsonConverterFactory;

//Classe da atividade in game
public class GameActivity extends AppCompatActivity {
    //Propriedades obtidas do jogador
    private String idJogo,
                   idJogador,
                   nomeJogo,
                   senhaJogo,
                   nomeJogador,
                   senhaJogador;
    //Container que armazena os jogadores
    ViewGroup containerJogadores;
    //Container que armazena os cards
    ViewGroup containerCards;
    //Atualizador do jogo
    private Handler refresher = null;
    private Runnable refresherRunner;
    private int taxaAtualizacaoEmSegundos = 1;
    private int contador = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Bind Container de jogadores
        containerJogadores = (ViewGroup) findViewById(R.id.container_jogadores);
        //Coleta informações do jogador para gerenciar o jogo
        SharedPreferences pref = getApplicationContext().getSharedPreferences("jogo", 0);
        idJogo = pref.getString("idJogo","");
        nomeJogo = pref.getString("nomeJogo","");
        senhaJogo = pref.getString("senhaJogo","");
        idJogador = pref.getString("idJogador","");
        nomeJogador = pref.getString("nomeJogador","");
        senhaJogador = pref.getString("senhaJogador","");
        //Inicializa retrofit usado na chamada
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://kingme.azurewebsites.net/cartagena/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //Inicializa API do usuário
        final MyService api = retrofit.create(MyService.class);
        atualizaListaJogadores(api);
        //Inicializa gameloop
        refresher = new Handler();
        refresherRunner = new Runnable() {
            @Override
            public void run() {
                final Runnable rThis = this;
                gameLogics(api);
                contador+=1;
                //Agenda a chamada da proxima atualização
                refresher.postDelayed(rThis,taxaAtualizacaoEmSegundos * 1000);
            }
        };
        //inicializa o atualizador
        startRefresher(0);
    }

    //Executa as chamadas do webservice
    private void gameLogics(MyService api){
        //Atualiza Cards do jogador
        //Adiciona Cards do jogador no fragmento de cards

        //Checa Status do jogo
        checaStatusJogo(api);
        //Se jogador pode jogar:
        //   - executa lógica para poder jogar

    }

    private void atualizaListaJogadores(MyService api){
        //Executa a chamada para coletar as informações dos usuários que estão na partida, enquanto
        //obtém as informações ele cria cards para cada um deles
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
                        addPlayerToFragment(retorno[i].getNome(), retorno[i].getId().toString());
                    }
                }
            }
            @Override
            public void onFailure(Call<Jogador[]> call, Throwable t) {
                Toast.makeText(GameActivity.this, "Deu Merda", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void addPlayerToFragment(String nomePlayer, String idPlayer){
        CardView cardView = (CardView) LayoutInflater.from(this)
                .inflate(R.layout.players_card, containerJogadores, false);
        System.out.println("Adicionando jogador " + nomePlayer + " ao container de jogadores");
        TextView nome = (TextView) cardView.findViewById(R.id.textView_nomeJogador_CardView);
        TextView id = (TextView) cardView.findViewById(R.id.textViewIDJogador_CardView);
        nome.setText("Jogador: " + nomePlayer);
        id.setText("ID : " + idPlayer);
        containerJogadores.addView(cardView);
        System.out.println("Adicionou jogador na lista");
    }
    private void checaStatusJogo(MyService api){
        Call<Status> chamadaStatusJogo = api.pegaStatusPartida(idJogo);
        chamadaStatusJogo.enqueue(new Callback<Status>() {
            @Override
            public void onResponse(Call<Status> call, Response<Status> response) {
                if(response.code() != 200)
                {
                    Toast.makeText(GameActivity.this, "Deu Merda " + response.code(),
                            Toast.LENGTH_LONG).show();
                }else{
                    Status retorno = response.body();
                    System.out.println("Vez do jogador: " + retorno.getIdJogadorDaVez());
                }
            }

            @Override
            public void onFailure(Call<Status> call, Throwable t) {
                Toast.makeText(GameActivity.this, "Deu Merda", Toast.LENGTH_LONG).show();
            }
        });
    }

    //inicializa o atualizador
    public void startRefresher(long delay) {
        if (refresher != null)
            refresher.postDelayed(refresherRunner, delay);
    }

    //desliga o atualizador
    public void stopRefresher () {
        if (refresher != null)
            refresher.removeCallbacks(refresherRunner);
    }
}
