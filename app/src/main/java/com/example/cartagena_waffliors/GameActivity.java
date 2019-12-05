package com.example.cartagena_waffliors;

import retrofit2.Call;
import android.os.Bundle;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Callback;

import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.content.SharedPreferences;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import androidx.cardview.widget.CardView;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.converter.gson.GsonConverterFactory;

//Classe da atividade in game
public class GameActivity extends AppCompatActivity {

    private String idJogo;

    private ViewGroup tiles;


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
        getSupportActionBar().hide();
        setContentView(R.layout.activity_game);

        //Bind Container de jogadores
        containerJogadores = (ViewGroup) findViewById(R.id.container_jogadores);

        //Coleta informações do jogador para gerenciar o jogo
        SharedPreferences pref = getApplicationContext().getSharedPreferences("jogo", 0);
        idJogo = pref.getString("idJogo","");
        String nomeJogo = pref.getString("nomeJogo","");
        String senhaJogo = pref.getString("senhaJogo","");
        String idJogador = pref.getString("idJogador","");
        String nomeJogador = pref.getString("nomeJogador","");
        String senhaJogador = pref.getString("senhaJogador","");
        tiles = (ViewGroup) findViewById(R.id.scroll_tiles);

        //Inicializa retrofit usado na chamada
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://kingme.azurewebsites.net/cartagena/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //Inicializa API do usuário
        final MyService api = retrofit.create(MyService.class);

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

        Call<Tile[]> chamadaListaTiles = api.pegarTiles(idJogo);
        chamadaListaTiles.enqueue(new Callback<Tile[]>() {
            @Override
            public void onResponse(Call<Tile[]> call, Response<Tile[]> response) {
                if(response.code() != 200){

                    Toast.makeText(GameActivity.this, "Bazinga " + response.code(),
                            Toast.LENGTH_LONG).show();
                }else{
                    Tile[] retorno = response.body();
                    System.out.println("Lista de tiles: \n");
                    for(int i = 0; i < retorno.length; i++){
                        addTile(retorno[i].getTipo(), retorno[i].getQntd());
                    }
                }
            }

            @Override
            public void onFailure(Call<Tile[]> call, Throwable t) {
                Toast.makeText(GameActivity.this, "Deu Merda", Toast.LENGTH_LONG).show();
            }
        });

        //Inicializa gameloop
        refresher = new Handler();
        refresherRunner = new Runnable() {
            @Override
            public void run() {
                final Runnable rThis = this;
                checaStatusJogo(idJogo, api);
                contador+=1;

                //agenda a chamada da proxima atualização
                refresher.postDelayed(rThis,taxaAtualizacaoEmSegundos * 1000);
            }
        };
        //inicializa o atualizador
        startRefresher(0);












         //Executa a chamada para coletar as informações dos cards do jogador, enquanto obtém as
         //informações ele cria cards para cada um deles
        Call<Carta[]> chamadaCartaJogadores = api.pegaCartasJogador(idJogador, senhaJogador);
        chamadaCartaJogadores.enqueue((new Callback<Carta[]>() {
            @Override
            public void onResponse(Call<Carta[]> call, Response<Carta[]> response) {
                if(response.code() != 200)
                {
                    Toast.makeText(GameActivity.this, "Deu Merda " + response.code(),
                            Toast.LENGTH_LONG).show();
                }else{
                    Carta[] retorno = response.body();
                    System.out.println("Lista de Cartas: \n");
                    for(int i = 0; i < retorno.length; i++){
                        //addCardToFragment(retorno[i].getTipo(), retorno[i].getQtd());
                    }
                }
            }
            @Override
            public void onFailure(Call<Carta[]> call, Throwable t) {
                Toast.makeText(GameActivity.this, "Deu Merda", Toast.LENGTH_LONG).show();
            }
        }));
    }




























    public void checaStatusJogo(String idPartida, MyService api){
        Call<Status> chamadaStatusJogo = api.pegaStatusPartida(idPartida);
        chamadaStatusJogo.enqueue(new Callback<Status>() {
            @Override
            public void onResponse(Call<Status> call, Response<Status> response) {
                if(response.code() != 200)
                {
                    Toast.makeText(GameActivity.this, "Deu Merda " + response.code(),
                            Toast.LENGTH_LONG).show();
                }else{
                    Status retorno = response.body();
                    System.out.println("Jogador da vez: " + retorno.getIdJogadorDaVez()+"\n");
                    System.out.println("Número da jogada: " + retorno.getNumeroDaJogada());
                }
            }

            @Override
            public void onFailure(Call<Status> call, Throwable t) {
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
        nome.setText("Jogador: " + nomePlayer);
        id.setText("ID : " + idPlayer);
        containerJogadores.addView(cardView);
        System.out.println("Adicionou jogador na lista");
    }

    // Add tiles
    public void addTile(String tipo, int qntd) {
        CardView cardView = (CardView) LayoutInflater.from(this)
                .inflate(R.layout.fragment_tile, tiles, false);

        ImageView imagem = (ImageView) cardView.findViewById(R.id.image_tile);
        String url = "";
        switch (tipo) {
            case "X":
                // Prisao
                url = "drawable/garrafa.png";
            case "C":
                url = "drawable/chave.png";
            case "F":
                url = "drawable/faca.png";
            case "E":
                url = "drawable/esqueleto.png";
            case"P":
                url = "drawable/pistola.png";
            case "T":
                url = "drawable/tricornio.png";
            case "G":
                url = "drawable/garrafa.png";
            case "B":
                // Barco
                url = "drawable/garrafa.png";
        }

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        imageLoader.displayImage(url, imagem);

        tiles.addView(cardView);
    }

    public void addCardToFragment(String tipoCarta, int qtdCarta)
    {
        CardView cardView2 = (CardView) LayoutInflater.from(this)
                .inflate(R.layout.cards, containerCards, false);

        //TextView nome = (TextView) cardView2.findViewById(R.id.textView_nomeJogador_CardView);
        TextView qtd = (TextView) cardView2.findViewById(R.id.cardQtd);
        //nome.setText("Carta: " + tipoCarta);
        qtd.setText("Quantidade: " + qtdCarta);
        containerJogadores.addView(cardView2);
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
