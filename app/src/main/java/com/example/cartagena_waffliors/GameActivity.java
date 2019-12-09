package com.example.cartagena_waffliors;

import retrofit2.Call;

import android.graphics.drawable.Drawable;
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
import androidx.core.content.ContextCompat;

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

    private ImageLoader imageLoaderTile, imageLoaderCards;

    private ViewGroup container_tiles;
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
        containerCards = (ViewGroup) findViewById(R.id.container_cards);
        //Coleta informações do jogador para gerenciar o jogo
        SharedPreferences pref = getApplicationContext().getSharedPreferences("jogo", 0);
        idJogo = pref.getString("idJogo","");
        nomeJogo = pref.getString("nomeJogo","");
        senhaJogo = pref.getString("senhaJogo","");
        idJogador = pref.getString("idJogador","");
        nomeJogador = pref.getString("nomeJogador","");
        senhaJogador = pref.getString("senhaJogador","");
        container_tiles = (ViewGroup) findViewById(R.id.container_tiles);
        //Inicializa retrofit usado na chamada
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://kingme.azurewebsites.net/cartagena/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //Inicializa API do usuário
        final MyService api = retrofit.create(MyService.class);

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
                        addCardToFragment(retorno[i].getTipo(), retorno[i].getQtd());
                    }
                }
            }
            @Override
            public void onFailure(Call<Carta[]> call, Throwable t) {
                Toast.makeText(GameActivity.this, "Deu Merda", Toast.LENGTH_LONG).show();
            }
        }));
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
                .inflate(R.layout.fragment_tile, container_tiles, false);

        ImageView imagemView = (ImageView) cardView.findViewById(R.id.image_tile);
        String url = "";
        switch (tipo) {
            case "X":
                // Prisao
                url = "https://i.imgur.com/9dun6Xz.png";
                break;
            case "C":
                url = "https://i.imgur.com/H75We3n.png";
                break;
            case "F":
                url = "https://i.imgur.com/JpHrcpH.png";
                break;
            case "E":
                url = "https://i.imgur.com/Ikfy3Ac.png";
                break;
            case"P":
                url = "https://i.imgur.com/WVC8Poy.png";
                break;
            case "T":
                url = "https://i.imgur.com/CXs0wkN.png";
                break;
            case "G":
                url = "https://i.imgur.com/9dun6Xz.png";
                break;
            case "B":
                // Barco
                url = "https://i.imgur.com/9dun6Xz.png";
                break;
        }

        System.out.println("Desenha tile");
        System.out.println(url);

        imageLoaderTile = ImageLoader.getInstance();
        imageLoaderTile.init(ImageLoaderConfiguration.createDefault(this));
        imageLoaderTile.displayImage(url, imagemView);

        container_tiles.addView(cardView);
    }

    public void addCardToFragment(String tipoCarta, int qtdCarta)
    {
        CardView cardView2 = (CardView) LayoutInflater.from(this)
                .inflate(R.layout.cards, containerCards, false);

        ImageView image = (ImageView) cardView2.findViewById(R.id.cardImage);
        String url = "";

        switch (tipoCarta)
        {
            case "T":
                url = "https://imgur.com/2MANop4.png";
                break;
            case "C":
                url = "https://imgur.com/6uWJgky.png";
                break;
            case "F":
                url = "https://imgur.com/YrktWor.png";
                break;
            case "G":
                url = "https://imgur.com/wHf1OJ4.png";
                break;
            case "E":
                url = "https://imgur.com/SbVxLwB.png";
                break;
            case "P":
                url = "https://imgur.com/c6K5e8d.png";
                break;
        }

        imageLoaderCards = ImageLoader.getInstance();
        imageLoaderCards.init(ImageLoaderConfiguration.createDefault(this));
        imageLoaderCards.displayImage(url, image);

        TextView qtd = (TextView) cardView2.findViewById(R.id.cardQtd);
        qtd.setText("Quantidade: " + qtdCarta);
        TextView tipo = (TextView) cardView2.findViewById(R.id.cardID);
        tipo.setText("Tipo: " + tipoCarta);
        containerCards.addView(cardView2);
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
