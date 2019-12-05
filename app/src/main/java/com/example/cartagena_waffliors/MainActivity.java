package com.example.cartagena_waffliors;

import java.util.Random;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import br.com.senac.pdm.mepresidenta.lobby.CriarJogoActivity;
import br.com.senac.pdm.mepresidenta.lobby.EscolherJogoActivity;

//Classe MainActivity responsável por criar a partida do jogador e introduzi-lo em outras partidas
public class MainActivity extends AppCompatActivity {

    String userName;
    Intent intentFromPreMain;
    Button findMatchButton, createMatchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intentFromPreMain = this.getIntent();
        userName = intentFromPreMain.getStringExtra("userName");
        createMatchButton = findViewById(R.id.criar);
        findMatchButton = findViewById(R.id.entrar);
        createMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CriarJogoActivity.class);
                intent.putExtra("nomeJogador",userName);
                intent.putExtra("nomeJogo", userName);
                intent.putExtra("senhaJogo", sortNumber(3));
                intent.putExtra("criar",true);
                intent.putExtra("atividadeJogo","com.example.cartagena_waffliors.GameActivity");
                startActivity(intent);
            }
        });
        findMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EscolherJogoActivity.class);
                intent.putExtra("nomeJogador",userName);
                intent.putExtra("atividadeJogo","com.example.cartagena_waffliors.GameActivity");
                intent.putExtra("criar",false);
                startActivity(intent);
            }
        });
    }

    //Método que sorteia um número de um tamanho especificado
    private String sortNumber(int size){
        Random rand = new Random();
        String resp = "";

        for(int i = 0; i < size; i++){
            resp = resp + rand.nextInt(9);
        }
        return resp;
    }
}
