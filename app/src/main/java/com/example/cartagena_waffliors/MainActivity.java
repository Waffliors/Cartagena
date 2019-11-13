package com.example.cartagena_waffliors;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import br.com.senac.pdm.mepresidenta.lobby.CriarJogoActivity;
import br.com.senac.pdm.mepresidenta.lobby.EscolherJogoActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button criar = findViewById(R.id.criar);
        Button jogar = findViewById(R.id.entrar);
        criar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(MainActivity.this, CriarJogoActivity.class);
                intent.putExtra("nomeJogador","Mario");
                intent.putExtra("nomeJogo","teste10");
                intent.putExtra("senhaJogo", "abc123");
                intent.putExtra("criar",true);
                intent.putExtra("atividadeJogo","exemplo.pdm.senac.com.br.exemplousolobby.JogoActivity");
                startActivity(intent);

            }
        });

        jogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(MainActivity.this, EscolherJogoActivity.class);
                intent.putExtra("nomeJogador","luigi");
                intent.putExtra("atividadeJogo","exemplo.pdm.senac.com.br.exemplousolobby.JogoActivity");
                intent.putExtra("criar",false);
                startActivity(intent);

            }
        });
    }
}
